ARG STRUCTURIZR_VERSION
FROM structurizr/structurizr:${STRUCTURIZR_VERSION}

# Copy workspace and configuration files into the container
COPY ./data/workspace.json /usr/local/structurizr/workspace.json
COPY ./data/structurizr.properties /usr/local/structurizr/structurizr.properties

ENTRYPOINT ["java", "-Dstructurizr.workspace=/usr/local/structurizr/workspace.json", "-jar", "structurizr-lite.jar"]