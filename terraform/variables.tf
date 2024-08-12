variable "environment" {
  description = "Application environment, normally this should be the branch name set automatically"
  type        = string
}

variable "parent_folder" {
  description = "Folder in which create the project. By default it is '/tf/devteam'"
  type        = string
  default     = "folders/674835777858"
}

variable "default_region" {
  description = "Default region to create resources where applicable."
  type        = string
  default     = "europe-west1"
}

variable "terraform_service_account" {
  description = "Terraform Gitlab main service account from the bootstrap step"
  type        = string
}

variable "terraform_ci_service_account" {
  description = "Terraform Gitlab CI/CD service account from the bootstrap step"
  type        = string
}

variable "terraform_ci_project" {
  description = "Terraform Gitlab CI/CD project id"
  type        = string
}

variable "billing_account" {
  description = "The ID of the billing account to associate projects with."
  type        = string
}

variable "org_id" {
  description = "GCP Organization ID"
  type        = string
}
