Reactivemongo - GridFS for Play 2.4 Example
=======================
This is a Play24 GridFS Example using reactiveMongo driver. It demontrates how to upload, view and delete attachment.

This example use the following:
<ul>
<li>Play Framework 2.4.4</li>
<li>Reactive Scala Driver for MongoDB 11.0</li>
</ul>

References:

http://blog.intelligencecomputing.io/tags/reactivemongo

https://github.com/sgodbillon/reactivemongo-demo-app

Setup Instruction
=======================

Modify Build.sbt
<div class="highlight highlight-scala"><pre>
name := """play-reactivemongo-gridfs"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.11.0.play24"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
</pre></div>

Modify application.conf
<div class="highlight highlight-scala"><pre>
# ****************************************** #
# *** ReactiveMongo Plugin configuration *** #
# ****************************************** #
play.modules.enabled += "play.modules.reactivemongo.ReactiveMongoModule"

# Simple configuration (by URI)
mongodb.uri = "mongodb://localhost:27017/play-reactivemongo-gridfs"

# URI + Authentication
# mongodb.uri = "mongodb://jack:jack@localhost:27017/toto"

# Legacy configuration (prefer URI)
# mongodb.servers = ["localhost:27017"]
# mongodb.db = "databasename"

# If you want to turn on logging for ReactiveMongo, uncomment and customize this line
# logger.reactivemongo=DEBUG
logger.reactivemongo=INFO
</pre></div>
