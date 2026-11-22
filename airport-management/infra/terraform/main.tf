resource "random_string" "suffix" {
  length  = 8
  lower   = true
  upper   = false
  numeric = true
  special = false
}

locals {
  prefix        = "airport-${var.environment}-${random_string.suffix.result}"
  registry_name = substr(replace("airport${var.environment}${random_string.suffix.result}", "-", ""), 0, 50)
  postgres_dns  = "private.postgres.database.azure.com"
}

resource "azurerm_resource_group" "airport" {
  name     = "${local.prefix}-rg"
  location = var.location
}

resource "azurerm_virtual_network" "airport" {
  name                = "${local.prefix}-vnet"
  location            = azurerm_resource_group.airport.location
  resource_group_name = azurerm_resource_group.airport.name
  address_space       = ["10.40.0.0/16"]
}

resource "azurerm_subnet" "container_apps" {
  name                 = "aca-infrastructure"
  resource_group_name  = azurerm_resource_group.airport.name
  virtual_network_name = azurerm_virtual_network.airport.name
  address_prefixes     = ["10.40.0.0/23"]

  delegation {
    name = "container-apps"

    service_delegation {
      name = "Microsoft.App/environments"
      actions = [
        "Microsoft.Network/virtualNetworks/subnets/join/action",
      ]
    }
  }
}

resource "azurerm_subnet" "postgres" {
  name                 = "postgres"
  resource_group_name  = azurerm_resource_group.airport.name
  virtual_network_name = azurerm_virtual_network.airport.name
  address_prefixes     = ["10.40.2.0/28"]

  delegation {
    name = "postgres-flexible-server"

    service_delegation {
      name = "Microsoft.DBforPostgreSQL/flexibleServers"
      actions = [
        "Microsoft.Network/virtualNetworks/subnets/join/action",
      ]
    }
  }
}

resource "azurerm_private_dns_zone" "postgres" {
  name                = local.postgres_dns
  resource_group_name = azurerm_resource_group.airport.name
}

resource "azurerm_private_dns_zone_virtual_network_link" "postgres" {
  name                  = "${local.prefix}-dns-link"
  resource_group_name   = azurerm_resource_group.airport.name
  private_dns_zone_name = azurerm_private_dns_zone.postgres.name
  virtual_network_id    = azurerm_virtual_network.airport.id
  registration_enabled  = false
}

resource "azurerm_container_registry" "airport" {
  name                = local.registry_name
  resource_group_name = azurerm_resource_group.airport.name
  location            = azurerm_resource_group.airport.location
  sku                 = "Basic"
  admin_enabled       = false
}

resource "azurerm_user_assigned_identity" "registry" {
  name                = "${local.prefix}-registry"
  resource_group_name = azurerm_resource_group.airport.name
  location            = azurerm_resource_group.airport.location
}

resource "azurerm_role_assignment" "registry_pull" {
  scope                = azurerm_container_registry.airport.id
  role_definition_name = "AcrPull"
  principal_id         = azurerm_user_assigned_identity.registry.principal_id
  principal_type       = "ServicePrincipal"
}

resource "azurerm_postgresql_flexible_server" "airport" {
  name                          = "${local.prefix}-db"
  resource_group_name           = azurerm_resource_group.airport.name
  location                      = azurerm_resource_group.airport.location
  version                       = "16"
  administrator_login           = var.postgres_admin_login
  administrator_password        = var.postgres_admin_password
  delegated_subnet_id           = azurerm_subnet.postgres.id
  private_dns_zone_id           = azurerm_private_dns_zone.postgres.id
  public_network_access_enabled = false
  sku_name                      = var.postgres_sku_name
  storage_mb                    = var.postgres_storage_mb
  backup_retention_days         = var.postgres_backup_retention_days
  geo_redundant_backup_enabled  = var.environment == "prod"

  dynamic "high_availability" {
    for_each = var.postgres_ha_enabled ? [true] : []

    content {
      mode                      = "ZoneRedundant"
      standby_availability_zone = "2"
    }
  }

  depends_on = [
    azurerm_private_dns_zone_virtual_network_link.postgres,
  ]
}

resource "azurerm_postgresql_flexible_server_database" "airport" {
  name      = "airport"
  server_id = azurerm_postgresql_flexible_server.airport.id
  collation = "en_US.utf8"
  charset   = "UTF8"
}

resource "azurerm_container_app_environment" "airport" {
  name                       = "${local.prefix}-apps"
  location                   = azurerm_resource_group.airport.location
  resource_group_name        = azurerm_resource_group.airport.name
  infrastructure_subnet_id   = azurerm_subnet.container_apps.id
  internal_load_balancer_enabled = false
}

resource "azurerm_container_app" "airport" {
  count                        = var.deploy_app ? 1 : 0
  name                         = "${local.prefix}-api"
  resource_group_name          = azurerm_resource_group.airport.name
  container_app_environment_id = azurerm_container_app_environment.airport.id
  revision_mode                = "Single"

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.registry.id]
  }

  registry {
    server   = azurerm_container_registry.airport.login_server
    identity = azurerm_user_assigned_identity.registry.id
  }

  secret {
    name  = "postgres-password"
    value = var.postgres_admin_password
  }

  template {
    min_replicas = var.container_min_replicas
    max_replicas = var.container_max_replicas

    container {
      name   = "airport-management"
      image  = "${azurerm_container_registry.airport.login_server}/airport-management:${var.image_tag}"
      cpu    = var.container_cpu
      memory = var.container_memory

      env {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:postgresql://${azurerm_postgresql_flexible_server.airport.fqdn}:5432/airport?sslmode=require"
      }

      env {
        name  = "SPRING_DATASOURCE_USERNAME"
        value = var.postgres_admin_login
      }

      env {
        name        = "SPRING_DATASOURCE_PASSWORD"
        secret_name = "postgres-password"
      }
    }
  }

  ingress {
    external_enabled           = true
    target_port                = 8080
    transport                  = "auto"
    allow_insecure_connections = false

    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }

  depends_on = [
    azurerm_role_assignment.registry_pull,
    azurerm_postgresql_flexible_server_database.airport,
  ]
}
