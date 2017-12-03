# neo4j

Neo4j is a graph database which is optimized for OLTP queries over a graph.
The docker image is based on https://github.com/ryguyrg/paradise-neo4j and contains the terrorist sample dataset. 

## starting the database
the following command starts the database and automatically loadas the CSV data for vertices and edges:
```
docker-compose up
```
after a couple of minutes visit http://localhost:7474 and you should find the neo4j UI

## queries

**1-simple-fraudulence**
- undirected
- not considering type of connection
- only single level into the graph
```
MATCH (source)--(destination)
RETURN source.name, source.known_terrorist, avg(destination.known_terrorist)
```

**2-multiple levels**
```
# same as before - single level
MATCH (source:Person)-[*1]-(destination:Person)
RETURN source.name, source.known_terrorist, avg(destination.known_terrorist)

MATCH (source:Person)-[*2]-(destination:Person)
RETURN source.name, source.known_terrorist, avg(destination.known_terrorist)

MATCH (source:Person)-[*3]-(destination:Person)
RETURN source.name, source.known_terrorist, avg(destination.known_terrorist)

# considering all levels up to 3
MATCH (source:Person)-[*1..3]-(destination:Person)
RETURN source.name, source.known_terrorist, avg(destination.known_terrorist)
```


**3-consider different type of relationship**

```
# same as before
MATCH (source:Person)-[:call|text*1..3]-(destination:Person)
RETURN source.name, source.known_terrorist, avg(destination.known_terrorist)

# only call
MATCH (source:Person)-[:call*1..3]-(destination:Person)
RETURN source.name, source.known_terrorist, avg(destination.known_terrorist)

# only text
MATCH (source:Person)-[:text*1..3]-(destination:Person)
RETURN source.name, source.known_terrorist, avg(destination.known_terrorist)
```

**4-considewring directedness of edges**

```
MATCH (source:Person)-[:call|text*1..3]->(destination:Person)
RETURN source.name, source.known_terrorist, avg(destination.known_terrorist)

MATCH (source:Person)<-[:call|text*1..3]-(destination:Person)
RETURN source.name, source.known_terrorist, avg(destination.known_terrorist)
```

**5-combining all the queries into single output result**

Nested subqueries are not yet supported https://stackoverflow.com/questions/47554183/neo4j-multiple-match-aggregations-single-pass-over-graph and https://github.com/petraselmer/openCypher/blob/CIP-nested-subqueries/cip/1.accepted/CIP2016-06-22-nested-updating-and-chained-subqueries.adoc

this leads to quite a lot of text being required when writing the queries 
```
MATCH (source:Person)-[*1]-(destination:Person)
RETURN source.name, source.known_terrorist, avg(destination.known_terrorist), 'undir_1_any' as fraudulence_type
UNION ALL
MATCH (source:Person)-[*2]-(destination:Person)
RETURN source.name, source.known_terrorist, avg(destination.known_terrorist), 'undir_2_any' as fraudulence_type
```

which leads to the graph being processes multiple times:
![plan of queries](img/naive_performance.png "Execution plan combined queries")
Additionally, to get all the different fraudulence results as columns for each vertex a pivot table would be required.

```
MATCH p = (source:Person)-[:call|text]-(destination:Person)
RETURN 
  source.name as Vertex, 
  source.known_terrorist as known_terrorist,
  apoc.coll.avg(COLLECT(
    CASE WHEN ALL(r in relationships(p) where type(r)='call') THEN destination.known_terrorist ELSE NULL END
  )) as type_undir_1_call,
  apoc.coll.avg(COLLECT(
    CASE WHEN ALL(r in relationships(p) where type(r)='text') THEN destination.known_terrorist ELSE NULL END
  )) as type_undir_1_text,
  apoc.coll.avg(COLLECT(
    destination.known_terrorist
  )) as type_undir_1_any
```

the query plan now is more efficient - but so far only 3 out of all the other combinations have been computed. A python script which loops over all the combinations of parameters could be used to query the desired relations - and I belive that this would be more maintainable (but probably less efficient).

## stopping the db

> Note: you need to remove i.e. stop and clean the container when restarting.

```
docker-compose stop # or simply cancel out of the terminal
docker-compose rm -f # to clean up
```