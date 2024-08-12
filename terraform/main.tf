locals {
  #TODO: derive from environment variables
  env      = var.environment
  tf_sa    = var.terraform_service_account
  tf_ci_sa = var.terraform_ci_service_account
  parent   = var.parent_folder
}

provider "google" {
  impersonate_service_account = local.tf_sa
  region                      = var.default_region
}

provider "google-beta" {
  impersonate_service_account = local.tf_sa
}

module "project-factory" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 14.4"

  name                = "palantir2-${local.env}"
  folder_id           = var.parent_folder
  random_project_id   = true
  auto_create_network = true
  org_id              = var.org_id
  billing_account     = var.billing_account
  activate_apis = [
    "compute.googleapis.com",
    "artifactregistry.googleapis.com",
    "run.googleapis.com",
    "sqladmin.googleapis.com",
    "monitoring.googleapis.com",
    "cloudtrace.googleapis.com"
  ]
}

resource "google_artifact_registry_repository" "repo" {
  location      = var.default_region
  repository_id = "docker"
  format        = "DOCKER"
  project       = module.project-factory.project_id

}

data "google_project" "project" {
  project_id = var.terraform_ci_project
}

resource "google_artifact_registry_repository_iam_member" "member_cloudbuild" {
  project    = google_artifact_registry_repository.repo.project
  location   = google_artifact_registry_repository.repo.location
  repository = google_artifact_registry_repository.repo.name
  role       = "roles/artifactregistry.repoAdmin"
  member     = "serviceAccount:${data.google_project.project.number}@cloudbuild.gserviceaccount.com"
}

resource "google_artifact_registry_repository_iam_member" "member_ci" {
  project    = google_artifact_registry_repository.repo.project
  location   = google_artifact_registry_repository.repo.location
  repository = google_artifact_registry_repository.repo.name
  role       = "roles/artifactregistry.repoAdmin"
  member     = "serviceAccount:${local.tf_ci_sa}"
}

resource "google_sql_database_instance" "main" {
  name                = "main-instance"
  database_version    = "POSTGRES_15"
  region              = var.default_region
  project             = module.project-factory.project_id
  deletion_protection = false
  settings {
    tier = "db-f1-micro"
    backup_configuration {
      enabled = false
    }
    database_flags {
      name  = "max_connections"
      value = "100"
    }
  }
}

resource "google_sql_database" "database" {
  name     = "main"
  instance = google_sql_database_instance.main.name
  project  = module.project-factory.project_id
}

resource "google_sql_user" "controller" {
  name     = "controller"
  password = random_password.database_user_password.result
  instance = google_sql_database_instance.main.name
  project  = module.project-factory.project_id
}

resource "random_password" "database_user_password" {
  length  = 30
  special = false
}

resource "google_cloud_run_v2_service" "controller" {
  project  = module.project-factory.project_id
  name     = "controller"
  location = var.default_region

  template {
    scaling {
      max_instance_count = 1
      min_instance_count = 1
    }
    containers {
      image = "us-docker.pkg.dev/cloudrun/container/hello"
      resources {
        limits = {
          cpu : "2000m",
          memory : "1Gi"
        }
        startup_cpu_boost = true
      }
      volume_mounts {
        name       = "cloudsql"
        mount_path = "/cloudsql"
      }
      env {
        name  = "INSTANCE_CONNECTION_NAME"
        value = google_sql_database_instance.main.connection_name
      }
      env {
        name  = "DB_NAME"
        value = google_sql_database.database.name
      }
      env {
        name  = "DB_USER"
        value = google_sql_user.controller.name
      }
      env {
        name  = "DB_PASS"
        value = google_sql_user.controller.password
      }
      env {
        name  = "STACKDRIVER_PROJECT_ID"
        value = module.project-factory.project_id
      }
      env {
        name  = "MICRONAUT_ENVIRONMENTS"
        value = "dev"
      }
    }
    volumes {
      name = "cloudsql"
      cloud_sql_instance {
        instances = [google_sql_database_instance.main.connection_name]
      }
    }
  }
  lifecycle {
    ignore_changes = [
      launch_stage,
      client,
      client_version,
      template[0].containers[0].image,
    ]
  }
}

data "google_iam_policy" "noauth" {
  binding {
    role = "roles/run.invoker"
    members = [
      "allUsers",
    ]
  }
}

resource "google_cloud_run_service_iam_policy" "noauth" {
  location = google_cloud_run_v2_service.controller.location
  project  = google_cloud_run_v2_service.controller.project
  service  = google_cloud_run_v2_service.controller.name
  policy_data = data.google_iam_policy.noauth.policy_data
}
