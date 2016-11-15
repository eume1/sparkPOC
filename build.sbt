name := "TwitterYahooFinanceAnalytics"

scalaVersion := "2.10.4"

version := "0.1.0-SNAPSHOT"

fork := true

mainClass in Compile := Some("TweetYahooFinanceMovingAverages.TwitterYahooFinanceMovingAveragesJob")

resolvers += "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/"

libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.5.0-cdh5.2.1" excludeAll ExclusionRule(organization = "javax.servlet")

//libraryDepeendencies += "org.apache.hadoop" % "hadoop-mapreduce-client-common" % "2.5.0-cdh5.2.1"

//libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.5.0-cdh5.2.1"

libraryDependencies += "org.apache.hbase" % "hbase-client" % "0.98.6-cdh5.2.1"

libraryDependencies += "org.apache.hbase" % "hbase-common" % "0.98.6-cdh5.2.1"

libraryDependencies += "org.apache.hbase" % "hbase-server" % "0.98.6-cdh5.2.1" excludeAll ExclusionRule(organization = "org.mortbay.jetty")

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.1.0-cdh5.2.1" % "provided"

libraryDependencies += "org.apache.spark" % "spark-assembly_2.10" % "1.3.0-cdh5.4.0" % "provided"

libraryDependencies += "joda-time" % "joda-time" % "2.8.2"

libraryDependencies += "org.joda" % "joda-convert" % "1.2"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.4" % "test"

/*
libraryDependencies += "com.cloudera" % "spark-hbase" % "0.0.1-clabs"*/
