ARG POSTGRES_VERSION
FROM postgres:${POSTGRES_VERSION}

COPY init.sql /docker-entrypoint-initdb.d/

RUN chmod 644 /docker-entrypoint-initdb.d/init.sql
