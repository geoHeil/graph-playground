install:
	pip3 install virtualenv && \
	virtualenv env && \
	source env/bin/activate && \
		pip install -r requirements.txt

notebook:
	source env/bin/activate && \
	jupyter notebook		

startNeo:
	cd neo4j/ && docker-compose up -d

stopNeo:
	cd neo4j/ && docker-compose stop

cleanNeo:
	cd neo4j/ && docker-compose rm -f