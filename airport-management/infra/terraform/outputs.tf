output "resource_group_name" {
  value = azurerm_resource_group.airport.name
}

output "container_registry" {
  value = azurerm_container_registry.airport.login_server
}

output "postgres_host" {
  value = azurerm_postgresql_flexible_server.airport.fqdn
}

output "api_url" {
  value = try("https://${azurerm_container_app.airport[0].ingress[0].fqdn}", null)
}
