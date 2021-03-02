val playJson = "com.typesafe.play" %% "play-json" % "2.8.1"

lazy val server = (project in file("server"))
  .settings(commonSettings)
  .settings(
    name := "sqlottery-server",
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    libraryDependencies ++= Seq(
      jdbc,
      evolutions,
      guice,
      "net.codingwell" %% "scala-guice" % "4.2.7",
      "com.iheart" %% "ficus" % "1.4.7",
      "com.enragedginger" %% "akka-quartz-scheduler" % "1.8.4-akka-2.6.x",
      "com.vmunier" %% "scalajs-scripts" % "1.1.4",
      "com.h2database" %  "h2" % "1.4.200",
      "io.getquill" %% "quill-jdbc" % "3.5.1",
      "com.typesafe.play" %% "play-mailer" % "8.0.0",
      "com.typesafe.play" %% "play-mailer-guice" % "8.0.0",
      "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
      "com.mohiva" %% "play-silhouette-persistence" % "7.0.0",
      "com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0",
      "com.mohiva" %% "play-silhouette-totp" % "7.0.0",
      "org.webjars" %% "webjars-play" % "2.8.0",
      "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4",
      "com.lihaoyi" %% "upickle" % "1.1.0",
      specs2 % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
      "org.mockito" % "mockito-core" % "2.27.0" % Test,
    ),
    // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
    EclipseKeys.preTasks := Seq(compile in Compile)
  )
  .enablePlugins(PlayScala)
  .dependsOn(sharedJvm)

lazy val client = (project in file("client"))
  .settings(commonSettings)
  .settings(
    name := "sqlottery-client",
    //scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
      "me.shadaj" %%% "slinky-core" % "0.6.5",
      "me.shadaj" %%% "slinky-web" % "0.6.5",
      "com.lihaoyi" %%% "upickle" % "1.1.0",
      "org.scalatest" %%% "scalatest" % "3.1.1" % "test"
    ),
    //scalacOptions += "-P:scalajs:sjsDefinedByDefault",
    scalacOptions += "-Ymacro-annotations",
  )
  //.enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .enablePlugins(SbtWeb)
  .dependsOn(sharedJs)


lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(
    name := "sqlottery-shared",
    commonSettings,
    libraryDependencies += "com.mohiva" %% "play-silhouette" % "7.0.0",
    libraryDependencies += "com.lihaoyi" %% "upickle" % "1.1.0"
  )

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.13.1",
  organization := "com.v15k",
  resolvers += "Atlassian's Maven Public Repository" at "https://packages.atlassian.com/maven-public/",
  libraryDependencies += "org.julienrf" %% "play-jsmessages" % "4.0.0",
)

onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}

