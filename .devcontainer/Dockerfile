# Base image: Java 21 devcontainer (includes Maven, NVM, Git, and common tools)
FROM mcr.microsoft.com/devcontainers/java:1-21-bullseye

# Install Node.js 20 LTS via NVM (bundled in the base image)
ARG NODE_VERSION=20
RUN su vscode -c "source /usr/local/share/nvm/nvm.sh && nvm install ${NODE_VERSION} && nvm alias default ${NODE_VERSION}" 2>&1

# Install pnpm
RUN su vscode -c "source /usr/local/share/nvm/nvm.sh && npm install -g pnpm@latest" 2>&1

# Persist Maven local repository as a volume-friendly location
RUN mkdir -p /home/vscode/.m2 && chown -R vscode:vscode /home/vscode/.m2
