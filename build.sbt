name := """play-reactivemongo-gridfs"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
	"org.reactivemongo" %% "play2-reactivemongo" % "0.11.0.play24",
	"com.github.tototoshi" %% "scala-csv" % "1.3.4"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
