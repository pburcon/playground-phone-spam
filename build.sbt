// Project settings
name := "phone-spam"
version := sys.env.getOrElse("version", "0.0.1-SNAPSHOT")

// Scala(c) setting
scalaVersion := "2.13.4"
scalacOptions ++= CompilerOptions.allOptions

// Dependencies
libraryDependencies ++= Dependencies.compileDependencies
resolvers ++= DependencyResolvers.resolvers

// Scapegoat
scapegoatVersion in ThisBuild := ScapegoatOptions.scapegoatVersion
scapegoatDisabledInspections := ScapegoatOptions.disabledInspections
