# docker build -t alonalbert/padserver .
# docker run -p 8080:8080 alonalbert/padserver
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
VOLUME /config
VOLUME /plex
WORKDIR /config
COPY build/libs/server.jar /app.jar
ENTRYPOINT [ \
    "java", \
    "-Dplex.database.path=/plex/com.plexapp.plugins.library.db", \
    "-jar", \
    "/app.jar" \
    ]
