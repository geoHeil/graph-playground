#!/bin/sh

NEO4J_HOME=/var/lib/neo4j
DATA_FILE="csv_paradise_papers.2017-11-17.zip"
DATA_DIR="/csv"
CONF=${NEO4J_HOME}/conf/neo4j.conf

# mkdir $DATA_DIR
# cd $DATA_DIR

# if [ ! -f "./$DATA_FILE" ]; then
#   echo "Downloading data"
#   wget "https://offshoreleaks-data.icij.org/offshoreleaks/csv/$DATA_FILE"
# else
#   echo "Not downloading data as file already exists"
# fi

# if [ ! -d "./panama-papers" ]; then
#   unzip "$DATA_FILE"
# fi

# for i in ${DATA_DIR}/*.csv
# do
#     echo "removing "n." in file: $i"
#     sed -i -e '1,1 s/n\.//g' $i
# done

# for i in ${DATA_DIR}/*.csv
# do
# #    sed -i '' -e '1,1 s/node_id,/node_id:ID,/' $i
#     echo "adding ID to node_id property in file: $i"
#     sed -i -e '1,1 s/node_id/node_id:ID/g' $i
# done

# sed -i -e '1,1 s/node_1,rel_type,node_2/node_id:START_ID,rel_type:TYPE,node_id:END_ID/' ${DATA_DIR}/paradise_papers.edges.csv

# grep -q 'rel_type' $DATA_DIR/paradise_papers.edges.csv && sed -i -e '1 d' ${DATA_DIR}/paradise_papers.edges.csv
# tr '[:lower:]' '[:upper:]' < ${DATA_DIR}/paradise_papers.edges.csv | sed  -e 's/[^A-Z0-9,_ ]//g' -e 's/  */_/g' -e 's/,_/_/g' > ${DATA_DIR}/paradise_papers.edges_cleaned.csv

# #echo 'node_id:START_ID,rel_type:TYPE,node_id:END_ID' > ${DATA_DIR}/paradise_papers.edges_header.csv
# echo 'node_id:START_ID,rel_type:TYPE,node_id:END_ID,sourceID,valid_until,start_date,end_date' > ${DATA_DIR}/all_edges_header.csv

# $NEO4J_HOME/bin/neo4j-import --into ${NEO4J_HOME}/data/databases/graph.db \
#   --nodes:Address ${DATA_DIR}/paradise_papers.nodes.address.csv \
#   --nodes:Entity ${DATA_DIR}/paradise_papers.nodes.entity.csv \
#   --nodes:Other ${DATA_DIR}/paradise_papers.nodes.intermediary.csv \
#   --nodes:Intermediary ${DATA_DIR}/paradise_papers.nodes.other.csv \
#   --nodes:Officer ${DATA_DIR}/paradise_papers.nodes.officer.csv \
#   --relationships ${DATA_DIR}/all_edges_header.csv,${DATA_DIR}/paradise_papers.edges_cleaned.csv \
#   --ignore-empty-strings true \
#   --skip-duplicate-nodes true \
#   --skip-bad-relationships true \
#   --bad-tolerance  1500 \
#   --multiline-fields=true

###################
# load terrorist sample dataset
DATA_DIR_SAMPLE=/data_network/
$NEO4J_HOME/bin/neo4j-admin import --mode=csv \
  --database=graph.db \
  --nodes:Person ${DATA_DIR_SAMPLE}/vertices_terrorist.csv \
  --relationships ${DATA_DIR_SAMPLE}/edges_terrorist.csv
###################

cd $NEO4J_HOME

echo 'dbms.security.procedures.unrestricted=apoc.*,algo.*' >> $CONF
echo 'dbms.security.auth_enabled=false' >> $CONF
echo 'browser.remote_content_hostname_whitelist=*' >> $CONF

cp -R plugins ./data/databases/graph.db/
# ./bin/neo4j-shell -path ./data/databases/graph.db -config ./conf/neo4j.conf -file /configure.cql
