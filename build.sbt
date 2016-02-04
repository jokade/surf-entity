//import SonatypeKeys._

lazy val commonSettings = Seq(
  organization := "de.surfice",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-deprecation","-unchecked","-feature","-Xlint"),
  resolvers += Resolver.sonatypeRepo("snapshots")
)

lazy val root = project.in(file(".")).
  aggregate(entityJVM,entityJS).
  settings(commonSettings:_*).
  //settings(sonatypeSettings: _*).
  settings(
    name := "surfice-entity",
    publish := {},
    publishLocal := {}
  )

lazy val entity = crossProject.in(file(".")).
  settings(commonSettings:_*).
  //settings(publishingSettings:_*).
  settings(
    name := "surfice-entity",
    libraryDependencies ++= Seq(
      "biz.enef" %%% "slogging" % "0.4.0",
      "de.surfice" %%% "surf-core" % "0.1-SNAPSHOT",
      "com.lihaoyi" %%% "utest" % "0.3.1"
    ),
    testFrameworks += new TestFramework("utest.runner.Framework")
  ).
  jvmSettings(
  ).
  jsSettings(
    //preLinkJSEnv := NodeJSEnv().value,
    //postLinkJSEnv := NodeJSEnv().value
  )

lazy val entityJVM = entity.jvm
lazy val entityJS = entity.js


lazy val publishingSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := (
    <url>https://github.com/jokade/surfice-entity</url>
    <licenses>
      <license>
        <name>MIT License</name>
        <url>http://www.opensource.org/licenses/mit-license.php</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:jokade/surfice-entity</url>
      <connection>scm:git:git@github.com:jokade/surfice-entity.git</connection>
    </scm>
    <developers>
      <developer>
        <id>jokade</id>
        <name>Johannes Kastner</name>
        <email>jokade@karchedon.de</email>
      </developer>
    </developers>
  )
)
 
