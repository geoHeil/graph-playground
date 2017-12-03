#!/usr/bin/env bash
#git reset --hard
#git pull origin master
sbt assembly

# using parallel gc as recommended by facebook https://www.slideshare.net/databricks/tuning-apache-spark-for-largescale-workloads-gaoxiang-liu-and-sital-kedia

echo "################## Job 1 ###############################################"
spark-submit --verbose \
        --class myOrg.SparkJob \
        --master "local[2]" \
        --driver-memory=500m \
        --conf spark.default.parallelism=4 \
        --conf "spark.driver.extraJavaOptions=-XX:+UseParallelGC -XX:ParallelGCThreads=4" \
	target/scala-2.11/sparkMiniSample-assembly-0.0.1.SNAPSHOT.jar

echo "################## Job 1  big############################################"
error
# TODO set some fitting values
spark-submit \
    --class myOrg.SparkJob \
    --master yarn \
    --deploy-mode cluster \
    --keytab /etc/security/keytabs/service_stage0.service.keytab \
    --files /usr/hdp/current/spark-client/conf/hive-site.xml,/etc/tez/conf/tez-site.xml \
    --driver-memory=3 \
    --executor-memory=10G \
#    --conf spark.memory.offHeap.enabled=true \
#    --conf spark.memory.offHeap.size=15g \
    --num-executors=4 \
    --conf spark.default.parallelism=120 \
    --conf spark.yarn.submit.waitAppCompletion=true \
    --conf spark.driver.maxResultSize=5G \
        --additional.confige="foo" \
        --verbose

# based on https://gist.github.com/bernhardschaefer/4309f728f66879c0a8c062be0801057b
# external shuffle service https://jaceklaskowski.gitbooks.io/mastering-apache-spark/yarn/spark-yarn-YarnShuffleService.html

####################
# below some real dummy config
####################
#spark-submit --verbose \
#        --class at.tmobile.bigdata.hybridAccess.stage0.runner.RunStage0CRM \
#        --master "yarn" \
#        --deploy-mode "cluster" \
#        --driver-memory=1G
#        --executor-memory=2G \ # do not use 800G here but rahter have many executurs?
#        --num-executors 2 --executor-cores 5 --executor-memory 1G \
#        --queue <realtime_queue>
#        --files <hdfs:///path/to/log4j-yarn.properties> \
#        --conf spark.driver.extraJavaOptions=-Dlog4j.configuration=log4j-yarn.properties \
#        --conf spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j-yarn.properties \
#        --conf spark.locality.wait=10 `# Increase job parallelity by reducing Spark Delay Scheduling (potentially big performance impact (!)) (Default: 3s)` \
#        --conf spark.task.maxFailures=8 `# Increase max task failures before failing job (Default: 4)` \
#        --conf spark.logConf=true `# Log Spark Configuration in driver log for troubleshooting` \
#        --conf spark.default.parallelism=30 \
#        --conf "spark.driver.extraJavaOptions=-XX:+UseG1GC -XX:+PrintGCDetails -Xloggc:gcLog_stage0-crm.log" \
#        --conf spark.ui.port=4043 \
#        `# YARN CONFIGURATION` \
#        --conf spark.yarn.driver.memoryOverhead=512 `# [Optional] Set if --driver-memory < 5GB` \
#        --conf spark.yarn.executor.memoryOverhead=1024 `# [Optional] Set if --executor-memory < 10GB` \
#        --conf spark.yarn.maxAppAttempts=4 `# Increase max application master attempts (needs to be <= yarn.resourcemanager.am.max-attempts in YARN, which defaults to 2) (Default: yarn.resourcemanager.am.max-attempts)` \
#        --conf spark.yarn.am.attemptFailuresValidityInterval=1h `# Attempt counter considers only the last hour (Default: (none))` \
#        --conf spark.yarn.max.executor.failures=$((8 * ${num_executors})) `# Increase max executor failures (Default: max(numExecutors * 2, 3))` \
#        --conf spark.yarn.executor.failuresValidityInterval=1h `# Executor failure counter considers only the last hour` \
#target/scala-2.11/geomesaSparkStarter-assembly-0.0.1.SNAPSHOT.jar