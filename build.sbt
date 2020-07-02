val Http4sVersion = "0.21.4"
val CirceVersion = "0.13.0"
val Specs2Version = "4.9.3"
val LogbackVersion = "1.2.3"
val DoobieVersion = "0.8.8"
val postgresVersion   = "42.2.9"

lazy val root = (project in file("."))
  .settings(
    organization := "com.norm",
    name := "http4s-demo",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      "joda-time" % "joda-time" % "2.10.6",
      "org.tpolecat" %% "doobie-core"      % DoobieVersion,
      "org.tpolecat" %% "doobie-hikari"    % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres"  % DoobieVersion,
      "org.tpolecat" %% "doobie-h2"        % DoobieVersion,
      "com.h2database"  %  "h2"                  % "1.4.200",
      "org.postgresql"  %  "postgresql"          % postgresVersion,
      "org.flywaydb"    %  "flyway-core"         % "6.3.1",
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "io.circe"        %% "circe-config"        % "0.7.0",
      "org.specs2"      %% "specs2-core"         % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
)

enablePlugins(UniversalPlugin)
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)

enablePlugins(GraalVMNativeImagePlugin)
graalVMNativeImageGraalVersion := Some("latest")
//dockerBaseImage := "openjdk:jre-alpine" //jre8
dockerBaseImage := "azul/zulu-openjdk-alpine:11"
dockerEnvVars := Map("PROJECT_ID"-> "classkick-v1", "USE-DB"->"db")