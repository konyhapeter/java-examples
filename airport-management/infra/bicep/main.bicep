targetScope = 'resourceGroup'

@allowed([
  'dev'
  'stable'
  'prod'
])
param environment string
param location string = resourceGroup().location
param imageTag string
@secure()
param postgresAdminPassword string
param postgresAdminLogin string = 'airportadmin'
param postgresSkuName string = 'Standard_B1ms'
@allowed([
  'Burstable'
  'GeneralPurpose'
  'MemoryOptimized'
])
param postgresSkuTier string = 'Burstable'
param postgresStorageSizeGB int = 32
param postgresBackupRetentionDays int = 7
@allowed([
  'Disabled'
  'ZoneRedundant'
])
param postgresHaMode string = 'Disabled'
param minReplicas int = 1
param maxReplicas int = 2
param deployApp bool = true
param containerCpu int = 1
param containerMemory string = '2Gi'

var suffix = uniqueString(subscription().id, resourceGroup().id, environment)
var prefix = 'airport-${environment}-${suffix}'
var registryName = replace('airport${environment}${suffix}', '-', '')
var vnetName = '${prefix}-vnet'
var acaSubnetName = 'aca-infrastructure'
var postgresSubnetName = 'postgres'
var privateDnsName = 'private.postgres.database.azure.com'
var acrPullRoleId = '7f951dda-4ed3-4680-a7ca-43fe172d538d'
var postgresSubnetId = resourceId('Microsoft.Network/virtualNetworks/subnets', vnetName, postgresSubnetName)
var acaSubnetId = resourceId('Microsoft.Network/virtualNetworks/subnets', vnetName, acaSubnetName)

resource vnet 'Microsoft.Network/virtualNetworks@2024-05-01' = {
  name: vnetName
  location: location
  properties: {
    addressSpace: {
      addressPrefixes: [
        '10.40.0.0/16'
      ]
    }
    subnets: [
      {
        name: acaSubnetName
        properties: {
          addressPrefix: '10.40.0.0/23'
          delegations: [
            {
              name: 'container-apps'
              properties: {
                serviceName: 'Microsoft.App/environments'
              }
            }
          ]
        }
      }
      {
        name: postgresSubnetName
        properties: {
          addressPrefix: '10.40.2.0/28'
          delegations: [
            {
              name: 'postgres-flexible-server'
              properties: {
                serviceName: 'Microsoft.DBforPostgreSQL/flexibleServers'
              }
            }
          ]
        }
      }
    ]
  }
}

resource postgresPrivateDns 'Microsoft.Network/privateDnsZones@2024-06-01' = {
  name: privateDnsName
  location: 'global'
}

resource postgresDnsLink 'Microsoft.Network/privateDnsZones/virtualNetworkLinks@2024-06-01' = {
  parent: postgresPrivateDns
  name: '${prefix}-link'
  location: 'global'
  properties: {
    registrationEnabled: false
    virtualNetwork: {
      id: vnet.id
    }
  }
}

resource registry 'Microsoft.ContainerRegistry/registries@2023-07-01' = {
  name: registryName
  location: location
  sku: {
    name: 'Basic'
  }
  properties: {
    adminUserEnabled: false
    publicNetworkAccess: 'Enabled'
  }
}

resource registryIdentity 'Microsoft.ManagedIdentity/userAssignedIdentities@2023-01-31' = {
  name: '${prefix}-registry'
  location: location
}

resource registryPullRole 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(registry.id, registryIdentity.properties.principalId, acrPullRoleId)
  scope: registry
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', acrPullRoleId)
    principalId: registryIdentity.properties.principalId
    principalType: 'ServicePrincipal'
  }
}

resource postgres 'Microsoft.DBforPostgreSQL/flexibleServers@2024-08-01' = {
  name: '${prefix}-db'
  location: location
  sku: {
    name: postgresSkuName
    tier: postgresSkuTier
  }
  properties: {
    version: '16'
    administratorLogin: postgresAdminLogin
    administratorLoginPassword: postgresAdminPassword
    createMode: 'Create'
    network: {
      publicNetworkAccess: 'Disabled'
      delegatedSubnetResourceId: postgresSubnetId
      privateDnsZoneArmResourceId: postgresPrivateDns.id
    }
    storage: {
      storageSizeGB: postgresStorageSizeGB
      autoGrow: 'Enabled'
    }
    backup: {
      backupRetentionDays: postgresBackupRetentionDays
      geoRedundantBackup: environment == 'prod' ? 'Enabled' : 'Disabled'
    }
    highAvailability: {
      mode: postgresHaMode
      standbyAvailabilityZone: postgresHaMode == 'ZoneRedundant' ? '2' : null
    }
  }
  dependsOn: [
    postgresDnsLink
  ]
}

resource postgresDatabase 'Microsoft.DBforPostgreSQL/flexibleServers/databases@2024-08-01' = {
  parent: postgres
  name: 'airport'
  properties: {
    charset: 'UTF8'
    collation: 'en_US.utf8'
  }
}

resource appEnvironment 'Microsoft.App/managedEnvironments@2024-03-01' = {
  name: '${prefix}-apps'
  location: location
  properties: {
    vnetConfiguration: {
      infrastructureSubnetId: acaSubnetId
      internal: false
    }
  }
}

resource app 'Microsoft.App/containerApps@2024-03-01' = if (deployApp) {
  name: '${prefix}-api'
  location: location
  identity: {
    type: 'UserAssigned'
    userAssignedIdentities: {
      '${registryIdentity.id}': {}
    }
  }
  properties: {
    managedEnvironmentId: appEnvironment.id
    configuration: {
      activeRevisionsMode: 'Single'
      ingress: {
        external: true
        targetPort: 8080
        transport: 'auto'
        allowInsecure: false
      }
      registries: [
        {
          server: registry.properties.loginServer
          identity: registryIdentity.id
        }
      ]
      secrets: [
        {
          name: 'postgres-password'
          value: postgresAdminPassword
        }
      ]
    }
    template: {
      containers: [
        {
          name: 'airport-management'
          image: '${registry.properties.loginServer}/airport-management:${imageTag}'
          resources: {
            cpu: containerCpu
            memory: containerMemory
          }
          env: [
            {
              name: 'SPRING_DATASOURCE_URL'
              value: 'jdbc:postgresql://${postgres.properties.fullyQualifiedDomainName}:5432/airport?sslmode=require'
            }
            {
              name: 'SPRING_DATASOURCE_USERNAME'
              value: postgresAdminLogin
            }
            {
              name: 'SPRING_DATASOURCE_PASSWORD'
              secretRef: 'postgres-password'
            }
          ]
        }
      ]
      scale: {
        minReplicas: minReplicas
        maxReplicas: maxReplicas
      }
    }
  }
  dependsOn: [
    registryPullRole
    postgresDatabase
  ]
}

output registryLoginServer string = registry.properties.loginServer
output postgresHost string = postgres.properties.fullyQualifiedDomainName
output containerAppName string = deployApp ? '${prefix}-api' : ''
