output "repository_registry" {
  description = "Docker repository domain"
  value       = "${google_artifact_registry_repository.repo.location}-docker.pkg.dev"
}

output "repository_image_prefix" {
  description = "Docker repository image prefix"
  value       = "${google_artifact_registry_repository.repo.location}-docker.pkg.dev/${module.project-factory.project_id}/${google_artifact_registry_repository.repo.name}"
}

output "project_id" {
  value = module.project-factory.project_id
}

output "controller_service_name" {
  value = google_cloud_run_v2_service.controller.name
}

output "controller_service_region" {
  value = google_cloud_run_v2_service.controller.location
}