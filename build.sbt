// Project settings
name := "phone-spam"
version := sys.env.getOrElse("version", "0.0.1-SNAPSHOT")

// Scala(c) setting
scalaVersion := "2.13.3" // #TODO update to 2.13.4 once scapegoat plugin is out
scalacOptions ++= CompilerOptions.allOptions

// Dependencies
libraryDependencies ++= Dependencies.compileDependencies
resolvers ++= DependencyResolvers.resolvers

// Scapegoat
scapegoatVersion in ThisBuild := ScapegoatOptions.scapegoatVersion
scapegoatDisabledInspections := ScapegoatOptions.disabledInspections
