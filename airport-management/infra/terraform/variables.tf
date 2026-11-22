variable "environment" {
  description = "Deployment environment."
  type        = string

  validation {
    condition     = contains(["dev", "stable", "prod"], var.environment)
    error_message = "environment must be dev, stable, or prod."
  }
}

variable "location" {
  description = "Azure region for the deployment."
  type        = string
}

variable "image_tag" {
  description = "Previously pushed airport-management image tag."
  type        = string
}

variable "deploy_app" {
  description = "Create/update the Container App after its image has been pushed to ACR."
  type        = bool
  default     = true
}

variable "postgres_admin_login" {
  description = "PostgreSQL administrator login."
  type        = string
  default     = "airportadmin"
}

variable "postgres_admin_password" {
  description = "PostgreSQL administrator password. Supply through TF_VAR_postgres_admin_password."
  type        = string
  sensitive   = true
}

variable "postgres_sku_name" {
  description = "Azure Database for PostgreSQL Flexible Server SKU."
  type        = string
}

variable "postgres_storage_mb" {
  description = "PostgreSQL storage allocation in MB."
  type        = number
}

variable "postgres_backup_retention_days" {
  description = "PostgreSQL backup retention in days."
  type        = number
}

variable "postgres_ha_enabled" {
  description = "Enable zone-redundant high availability for PostgreSQL."
  type        = bool
  default     = false
}

variable "container_min_replicas" {
  description = "Minimum Container Apps replicas."
  type        = number
}

variable "container_max_replicas" {
  description = "Maximum Container Apps replicas."
  type        = number
}

variable "container_cpu" {
  description = "Container CPU allocation."
  type        = number
}

variable "container_memory" {
  description = "Container memory allocation (for example 1Gi)."
  type        = string
}
