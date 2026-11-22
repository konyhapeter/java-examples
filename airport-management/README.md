# Airport Management

A Spring Boot REST API for aircraft, flight schedules, passengers, and seat bookings. The module follows domain-driven design with hexagonal (ports-and-adapters) boundaries:

- `domain` contains framework-independent airport concepts and invariants.
- `application` contains use cases, commands, and outbound persistence ports.
- `adapter.in.web` translates HTTP requests and responses.
- `adapter.out.persistence` implements the ports using JPA; Flyway owns the PostgreSQL schema.

## Run with Docker Compose

From this directory, start PostgreSQL and the API:

```shell
docker compose up --build
```

The API is available at `http://localhost:8080`. Compose persists PostgreSQL data in the `airport-postgres-data` volume. Local Maven runs connect to PostgreSQL at `localhost:5432` by default; override `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` as needed.

Run the API without Docker after starting a PostgreSQL server:

```shell
mvn -pl airport-management spring-boot:run
```

## Test

Tests run against an isolated PostgreSQL 17 Testcontainer. Docker must be available to Maven:

```shell
mvn -pl airport-management test
```

## Infrastructure and deployment

The module includes parallel Azure infrastructure definitions and Kubernetes Kustomize overlays for `dev`, `stable`, and `prod`:

- `infra/bicep` provisions a private-VNet Azure Container Apps environment, Azure Container Registry, and private Azure Database for PostgreSQL Flexible Server.
- `infra/terraform` provisions the same resources. Terraform state is intentionally not configured; use a secured remote backend for shared or production deployments.
- `kubernetes` deploys the API to an existing cluster. The `dev` overlay includes a single-replica PostgreSQL StatefulSet with a persistent volume claim; `stable` and `prod` expect an externally managed PostgreSQL database and image registry.

### Azure Bicep

Create a resource group, then deploy one of the environment parameter files. The first deployment creates the database and ACR without the app (`deployApp=false`). Push the application image, then redeploy with `deployApp=true` (the default) and the matching environment image tag. Supply the database password through a secret manager or environment variable; do not commit it in parameter files. Example for `dev`:

```powershell
$env:PG_ADMIN_PASSWORD = Read-Host "PostgreSQL admin password"
az deployment group create --resource-group airport-dev-rg --template-file airport-management/infra/bicep/main.bicep --parameters airport-management/infra/bicep/environments/dev.bicepparam --parameters postgresAdminPassword=$env:PG_ADMIN_PASSWORD deployApp=false
az acr login --name <registry-name>
docker build -f airport-management/Dockerfile -t <registry-login-server>/airport-management:dev .
docker push <registry-login-server>/airport-management:dev
az deployment group create --resource-group airport-dev-rg --template-file airport-management/infra/bicep/main.bicep --parameters airport-management/infra/bicep/environments/dev.bicepparam --parameters postgresAdminPassword=$env:PG_ADMIN_PASSWORD deployApp=true
```

Use the corresponding `stable.bicepparam` or `prod.bicepparam` file for those environments. Production enables zone-redundant PostgreSQL HA, geo-redundant backups, longer retention, and higher replica counts. Select a region that supports the chosen SKU and availability zones.

### Azure Terraform

The Terraform configuration creates its resource group and the same private networking and Azure services. Authenticate with `az login`; keep the password out of files and shell history:

```powershell
cd airport-management/infra/terraform
$env:TF_VAR_postgres_admin_password = Read-Host "PostgreSQL admin password"
terraform init
terraform apply -var-file=environments/dev.tfvars -var="deploy_app=false"
$registry = terraform output -raw container_registry
az acr login --name $registry.Split('.')[0]
docker build -f ..\..\Dockerfile -t "$registry/airport-management:dev" ..\..\..
docker push "$registry/airport-management:dev"
terraform apply -var-file=environments/dev.tfvars
```

Terraform has the same two-step flow: first apply with `-var="deploy_app=false"` to create ACR and PostgreSQL, build and push the matching image tag, then apply again without that override. Use `stable.tfvars` or `prod.tfvars` for the other environments. Configure a remote, encrypted state backend before using Terraform collaboratively or for production.

### Kubernetes

Each Kustomize overlay has environment-specific replica counts, resources, autoscaling, and disruption budgets. The `dev` overlay starts PostgreSQL in the cluster and connects the API to the `postgres` Service. It uses a persistent volume claim; deleting the namespace or PVC can delete development data. `stable` and `prod` use an externally managed database: replace the sample database hostname and the image `newName` in each overlay's `kustomization.yaml` with your database endpoint and published image. The cluster must be authorized to pull that image, have metrics-server installed for the stable/prod HPAs, and have an ingress or gateway configured if the API should be reachable outside the cluster.

#### Run the dev overlay on Docker Desktop Kubernetes (PowerShell)

1. In Docker Desktop, enable Kubernetes under **Settings → Kubernetes** and wait for the cluster to finish starting. Then select and verify its context:

```powershell
kubectl config use-context docker-desktop
kubectl cluster-info
kubectl get nodes
```

2. From the repository root, build the image with the same tag used by the dev overlay (`airport-management:dev`):

```powershell
docker build -f airport-management\Dockerfile -t airport-management:dev .
docker image inspect airport-management:dev
```

Docker Desktop Kubernetes can use images in Docker Desktop's local image store. The dev Deployment has `imagePullPolicy: IfNotPresent`, so it will use this local image.


3(Deprecated). Create the namespace and a Kubernetes Secret for PostgreSQL. The password is prompted securely, not saved in the manifests:

```powershell
kubectl apply -f airport-management\kubernetes\overlays\dev\namespace.yaml
$pgUser = Read-Host "PostgreSQL username"
$pgPasswordSecure = Read-Host "PostgreSQL password" -AsSecureString
$pgPassword = [System.Net.NetworkCredential]::new("", $pgPasswordSecure).Password
kubectl create secret generic airport-management-db -n airport-management-dev --from-literal="POSTGRES_USER=$pgUser" --from-literal="POSTGRES_PASSWORD=$pgPassword" --dry-run=client -o yaml | kubectl apply -f -
Remove-Variable pgPassword, pgPasswordSecure
```

The command applies or updates the Secret if it already exists. Its keys must be `POSTGRES_USER` and `POSTGRES_PASSWORD`; the PostgreSQL StatefulSet and API Deployment read these exact keys. Use a PostgreSQL username made of ASCII letters, digits, or underscores, starting with a letter or underscore (for example `airportuser`); avoid quotes, dollar signs, hyphens, and backslashes. Keep the username and password for the PostgreSQL pod together; changing the Secret after PostgreSQL has initialized its persistent volume does not change the existing database user's password.

4. Deploy the dev overlay. This creates the PostgreSQL StatefulSet, persistent volume claim, Services, and API Deployment:

```powershell
kubectl apply -k airport-management/kubernetes/overlays/dev
kubectl rollout status statefulset/postgres -n airport-management-dev --timeout=180s
kubectl rollout status deployment/airport-management -n airport-management-dev --timeout=180s
```

5. Check pod status and logs. PostgreSQL startup and Flyway migrations can take a short time:

```powershell
kubectl get pods,pvc,services -n airport-management-dev
kubectl logs statefulset/postgres -n airport-management-dev
kubectl logs deployment/airport-management -n airport-management-dev
```

Both pods should be `Running`, and the API pod should show `1/1` ready. If a pod is restarting, use `kubectl describe pod <pod-name> -n airport-management-dev` and `kubectl logs <pod-name> -n airport-management-dev --previous`.

6. Forward the API Service port. Keep this command running in one terminal:

```powershell
kubectl port-forward service/airport-management 8080:80 -n airport-management-dev
```

In another PowerShell terminal, call the health endpoint and list aircraft:

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
Invoke-RestMethod http://localhost:8080/api/aircraft
```

`{"status":"UP"}` confirms the app is healthy; an empty aircraft list is expected for a fresh database. To add a test aircraft:

```powershell
$body = '{"registration":"N123AB","model":"A320","capacity":180,"status":"AVAILABLE"}'
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/aircraft -ContentType "application/json" -Body $body
Invoke-RestMethod http://localhost:8080/api/aircraft
```

For dev, the Secret named `airport-management-db` must contain `POSTGRES_USER` and `POSTGRES_PASSWORD`; the StatefulSet initializes the `airport` database with these credentials, and the API uses the same keys to connect. Secret values are intentionally not stored in YAML. For stable/prod, the Secret must contain the same keys with credentials for the external database. Repeat the Secret creation in `airport-management-stable` or `airport-management-prod` for those overlays. For production, provision credentials with an external secret manager and use a managed PostgreSQL service with backups, HA, and private connectivity. Kubernetes manifests use Spring Boot Actuator liveness/readiness probes.

## API

All endpoints accept and return JSON. Collection resources support `GET` and `POST`; individual resources support `GET`, `PUT`, and `DELETE`.

| Resource | URL |
| --- | --- |
| Aircraft | `/api/aircraft` |
| Passengers | `/api/passengers` |
| Flights and timetable | `/api/flights` or `/api/timetable` |
| Bookings | `/api/bookings` |
| Bookings for a flight | `/api/bookings/flight/{flightId}` |

Create flights with ISO-8601 local date-time strings, for example `2026-12-15T09:30:00`. Flights require a distinct origin and destination, positive duration, and an available aircraft. Aircraft schedules cannot overlap. Bookings are restricted by aircraft capacity, and active seats/passengers cannot be booked twice on a flight. Validation errors return `400`, missing resources `404`, and uniqueness or schedule conflicts `409`.

Example aircraft request:

```json
{
  "registration": "N123AB",
  "model": "A320",
  "capacity": 180,
  "status": "AVAILABLE"
}
```
