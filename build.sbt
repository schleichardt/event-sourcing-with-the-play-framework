name := "es"

version := "1.0-SNAPSHOT"

resolvers += "Schleichardts GitHub" at "http://schleichardt.github.io/jvmrepo/"

play.Project.playScalaSettings

libraryDependencies += "com.typesafe.akka" %% "akka-persistence-experimental" % "2.3.1"