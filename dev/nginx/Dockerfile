# Use an official Nginx image as the base
FROM nginx:latest

# Install OpenSSL to generate self-signed certificates
RUN apt-get update && apt-get install -y openssl

# Create directory for SSL certificates
RUN mkdir -p /etc/ssl/private /etc/ssl/certs

# Generate self-signed SSL certificate
RUN openssl req -x509 -newkey rsa:2048 -keyout /etc/ssl/private/nginx.key -out /etc/ssl/certs/nginx.crt -days 365 -nodes -subj "/CN=localhost"

# Copy custom Nginx configuration
COPY nginx.conf.template /etc/nginx/nginx.conf.template

# Expose ports
EXPOSE 80 443

CMD envsubst < /etc/nginx/nginx.conf.template | sed -e 's/§/$/g' > /etc/nginx/nginx.conf && nginx -g "daemon off;"