# Use a slim Node.js base image
FROM node:22-slim AS base

WORKDIR /app

# Set up PNPM and install it globally
ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"
RUN npm install -g pnpm

# Configure pnpm for optimized dependency management
RUN pnpm config set hoist true
RUN pnpm config set node-linker hoisted

# Install production dependencies only
FROM base AS prod-deps
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --prod --frozen-lockfile

# Install all dependencies (including dev dependencies) for build
FROM base AS dev-deps
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile

# Build the application
FROM base AS build
COPY --from=dev-deps /app/node_modules /app/node_modules
COPY . ./
RUN pnpm run build

# Final image for production, includes only necessary files
FROM node:22-slim AS production

WORKDIR /app

# Copy production dependencies and the build output
COPY --from=prod-deps /app/node_modules /app/node_modules
COPY --from=build /app/build /app/build

# Expose the app port and define the startup command
EXPOSE 3000
CMD [ "node", "build" ]