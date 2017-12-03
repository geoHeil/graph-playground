# neo4j

Neo4j is a graph database which is optimized for OLTP queries over a graph.
The docker image is based on https://github.com/ryguyrg/paradise-neo4j and enriched with the terrorist sample dataset. Additionally allows to easily explore the paradise papers.

## starting the database
the following command starts the database and automatically loadas the CSV data for vertices and edges:
```
docker-compose up
```
after a couple of minutes visit http://localhost:7474 and you should find the neo4j UI

## queries

**1-simple-fraudulence**

## stopping the db

```
docker-compose stop # or simply cancel out of the terminal
docker-compose rm -f # to clean up
```