using '../main.bicep'

param environment = 'dev'
param imageTag = 'dev'
param postgresSkuName = 'Standard_B1ms'
param postgresStorageSizeGB = 32
param postgresBackupRetentionDays = 7
param postgresHaMode = 'Disabled'
param minReplicas = 1
param maxReplicas = 2
param containerCpu = 1
param containerMemory = '2Gi'
