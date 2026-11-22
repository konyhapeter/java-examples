using '../main.bicep'

param environment = 'prod'
param imageTag = 'prod'
param postgresSkuName = 'Standard_D2s_v3'
param postgresSkuTier = 'GeneralPurpose'
param postgresStorageSizeGB = 128
param postgresBackupRetentionDays = 35
param postgresHaMode = 'ZoneRedundant'
param minReplicas = 3
param maxReplicas = 10
param containerCpu = 2
param containerMemory = '4Gi'
