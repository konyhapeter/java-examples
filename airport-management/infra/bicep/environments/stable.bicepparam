using '../main.bicep'

param environment = 'stable'
param imageTag = 'stable'
param postgresSkuName = 'Standard_D2s_v3'
param postgresSkuTier = 'GeneralPurpose'
param postgresStorageSizeGB = 64
param postgresBackupRetentionDays = 14
param postgresHaMode = 'Disabled'
param minReplicas = 2
param maxReplicas = 5
param containerCpu = 1
param containerMemory = '2Gi'
