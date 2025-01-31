# edit.md - The Collaborative Markdown Editor

## Development
Dev-Prod parity is maintained by running all services in containers, ensuring that the development and production environments remain as similar as possible. This setup minimizes discrepancies between the two environments, making it easier to catch and fix issues early. In theory, no dev tools or build tools need to be installed on the development machine, even though we would not recommend that. All containers have a development target that starts the services with hot reload functionality, allowing for seamless editing and updates. While the hot reload feature may take some time for Spring Boot services, it should still function correctly. However, switching branches or making significant changes may require a container restart. 

> **IMPORTANT: The docker-compose file is only intended for development purposes and should not be used for deployment.**
### Setting Up the Environment
To get started, first copy the example environment configuration file and fill in the empty fields with the appropriate values. For signing in to the services a GitHub OAuth App has to be created [here](https://github.com/settings/developers).

- Windows:
  ```batch
  copy .env.example .env
  ```

- Linux/Unix:
  ```bash
  cp .env.example .env
  ```

### Starting the Containers
Once the environment file is configured, start all the necessary containers using the command below. This process may take some time as the services are initialized. The command ensures that all services start and automatically restart whenever source files change.
```bash
docker compose watch
```

### Accessing the Services
After starting the containers, the following services will be available at the respective URLs shortly. The Spring Boot based services may take some time to start, so please be patient. 
The ability to access all services through a single IP and port with SSL is provided by a separate Nginx container, which is started in the Docker Compose file. This setup is intended for development purposes only.

- **Frontend:** [https://127.0.0.1/](https://127.0.0.1/)
- **Account Service:** [https://127.0.0.1/api/accounts/](https://127.0.0.1/api/accounts/)
- **Document Service:** [https://127.0.0.1/api/documents/](https://127.0.0.1/api/documents/)
- **File Service:** [https://127.0.0.1/api/files/](https://127.0.0.1/api/files/)

If the frontend is not available, you can still interact with the services directly using the following authentication endpoints:

- **Login (GitHub OAuth):** [https://127.0.0.1/api/accounts/oauth2/authorization/github](https://127.0.0.1/api/accounts/oauth2/authorization/github)
- **Logout:** [https://127.0.0.1/api/accounts/logout](https://127.0.0.1/api/accounts/logout)

This setup provides the core functionality for authenticating users and managing accounts, documents, and files in the collaborative editor.

## Production
The production environment is designed to run in a Kubernetes cluster and can be deployed using the provided Helm chart. The chart is located in the `helm` directory and can be installed using the following tutorial.

### Remote

The production environment runs in Kubernetes (k8s) and can be deployed using the provided Helm chart. The chart is located in the `helm` directory. Before installing the chart, update the `values.yaml` file with the appropriate values. The chart can be installed using the following command:

```bash
./helm/preprocess.sh
helm install <name> ./helm
```

### Locally

When running the chart locally in Minikube, it is recommended to [install the Ingress controller](https://kubernetes.io/docs/tasks/access-application-cluster/ingress-minikube/#enable-the-ingress-controller).  
If running locally in Minikube, use the `values-local.yaml` file with the following command:

```bash
./helm/preprocess.sh
helm install <name> ./helm -f ./helm/values-local.yaml
```

After installing it, you can access the service using the Minikube tunnel command:

```bash
minikube tunnel
```

The services will be available at the following URL:
[https://127.0.0.1/](https://127.0.0.1/)


## Service Overview

![A view of all services and how they interact with each other](https://private-user-images.githubusercontent.com/63405129/408598596-e304fbc9-0e1c-4d37-bb5f-2fb48b1362ac.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MzgzMzM1OTIsIm5iZiI6MTczODMzMzI5MiwicGF0aCI6Ii82MzQwNTEyOS80MDg1OTg1OTYtZTMwNGZiYzktMGUxYy00ZDM3LWJiNWYtMmZiNDhiMTM2MmFjLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTAxMzElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwMTMxVDE0MjEzMlomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTk0NzBkZDQyMDk5OWVlODE0NmI0OWZjMDgzYjM0YzlkYjRhMmUxMDQ0N2FkMTkzYjJjZmYxZGQ5ZTNhM2JhZGImWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.ESzd0rC__fyWsgYb8fwmxVb_bm8dbfjlNiuxnqcAqVk)

### Frontend
The frontend service is a SvelteKit application that provides the user interface for the collaborative editor. It allows users to create, edit, and share documents with other users. The frontend communicates with the account, document, and file services to prerender all the necessary data for the user interface.

### Account Service
The account service is a Spring Boot application that manages user accounts and authentication. It provides endpoints for user login and logout via OAuth2 authentication with GitHub. It also provides endpoints for retrieving user information and validating user sessions.

### Document Service
The document service is a Spring Boot application that manages documents in the collaborative editor. It provides endpoints for creating, updating, and deleting documents as well as provides the live sync functionality. It also provides endpoints for retrieving document information, sharing and unsharing documents with other users.

### File Service
The file service is a Spring Boot application that manages files in the collaborative editor. It provides endpoints for uploading, downloading, and deleting files as well as provides file preview functionality for images. It also provides endpoints for retrieving file information and files for specific documents.
