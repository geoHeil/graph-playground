FROM neo4j:3.3.0
MAINTAINER geoheil

RUN apk update
RUN apk add --quiet openssl sed wget unzip
RUN cd /var/lib/neo4j/plugins/; wget "https://github.com/neo4j-contrib/neo4j-apoc-procedures/releases/download/3.3.0.1/apoc-3.3.0.1-all.jar" 
RUN cd /var/lib/neo4j/plugins/; wget "https://github.com/neo4j-contrib/neo4j-graph-algorithms/releases/download/3.3.0.0/graph-algorithms-algo-3.3.0.0.jar"

COPY download_db.sh /download_db.sh
COPY configure.cql /configure.cql 

ENV EXTENSION_SCRIPT /download_db.sh
ENV NEO4J_AUTH=none
