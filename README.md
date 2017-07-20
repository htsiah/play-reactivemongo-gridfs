Reactivemongo - GridFS for Play 2.4 Example
=======================
This is a Play24 GridFS Example using reactiveMongo driver. It demonstrates how to upload, view and delete attachment.

This example use the following:
<ul>
<li>Play Framework 2.4.4</li>
<li>Reactive Scala Driver for MongoDB 11.0</li>
</ul>

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


Notes:
=======================
<ul>
<li>MongoDB configuration is in FileModel, not in application.conf. With this approach, developer can set different types of attachment to store in different MongoDB.</li>
<li>This example has 2 attachment upload - standard and ajax.</li>
<li>Standard maximum file size limit is 2MB and configuration is in application.conf at play.http.parser.maxDiskBuffer = 2024k. In standard file upload, user select the attachment and click on submit button to upload attachment.</li>
<li>Ajax maximum file size limit is 1MB and configuration is at Action -> Action.async(parse.maxLength(1 * 1024 * 1024, gridFSBodyParser(gridFS))). In ajax file upload, attachment is upload using jquery ajax post.</li>
<li>Both file size limit will prompt error message if exceeded size limit. However attachment still upload into MongoDB.</li>
<li>Next Steps: If attachment file size limit exceeded, should no upload into MongoDB.</li>
</ul>

References:
=======================
http://blog.intelligencecomputing.io/tags/reactivemongo

https://github.com/sgodbillon/reactivemongo-demo-app

https://groups.google.com/forum/?fromgroups#!topic/reactivemongo/egniFzCLpDg
