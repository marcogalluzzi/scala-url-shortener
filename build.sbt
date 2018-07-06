name := "scala-url-shortener"
 
version := "1.0" 
      
lazy val `scala-url-shortener` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice,
  "org.picoworks" %% "pico-hashids"  % "4.4.141",
  "net.debasishg" %% "redisclient" % "3.7",
  "io.circe" %% "circe-core" % "0.9.3",
  "io.circe" %% "circe-generic" % "0.9.3",
  "io.circe" %% "circe-parser" % "0.9.3")

unmanagedResourceDirectories in Test += baseDirectory ( _ /"target/web/public/test" ).value

      