# edit.md - The collaborative Markdown editor

## Development

### Setting Up the Environment
To get started, first copy the example environment configuration file and fill in the empty fields with the appropriate values. You can create a GitHub OAuth App [here](https://github.com/settings/developers) if you haven't already.

- Windows:
  ```batch
  copy .env.example .env
  ```

- Linux/Unix:
  ```bash
  cp .env.example .env
  ```


### Starting the Containers
Once the environment file is configured, start all the necessary containers using the command below. This process may take some time as the services are being initialized. The command will ensure that all services start and automatically restart any time source files change.
```bash
docker compose watch
```

### Accessing the Services
After starting the containers, the following services will be available at the respective URLs:

- **Frontend:** [https://127.0.0.1/](https://127.0.0.1/)  
- **Account Service:** [https://127.0.0.1/api/accounts/](https://127.0.0.1/api/accounts/)  
- **Document Service:** [https://127.0.0.1/api/documents/](https://127.0.0.1/api/documents/)  
- **File Service:** [https://127.0.0.1/api/files/](https://127.0.0.1/api/files/)  

If the frontend is not available, you can still interact with the services directly using the following endpoints for authentication:

- **Login (GitHub OAuth):** [https://127.0.0.1/api/accounts/oauth2/authorization/github](https://127.0.0.1/api/accounts/oauth2/authorization/github)  
- **Logout:** [https://127.0.0.1/api/accounts/logout](https://127.0.0.1/api/accounts/logout)  

This setup provides the core functionality for authenticating users and managing accounts, documents, and files in the collaborative editor.

## Production
### Remote

The production environment is running in k8s. It can be deployed using the provided helm chart. The chart can be found in the `helm` directory.
Before installing the chart, the `values.yaml` file should be updated with the appropriate values.
The chart can be installed using the following command:

```bash
./helm/preprocess.sh
helm install <name> ./helm
```

### Locally

When running the chart locally in minikube it is recommended to [install the ingress controller](https://kubernetes.io/docs/tasks/access-application-cluster/ingress-minikube/#enable-the-ingress-controller).  
If running locally in minikube the ``values-local.yaml`` file should be used using the following command:

```bash
./helm/preprocess.sh
helm install <name> ./helm -f ./helm/values-local.yaml
```

After installing it, it is possible to access the service using the minikube tunnel command:

```bash
minikube tunnel
```

The service will be available at the following URL:
https://127.0.0.1/