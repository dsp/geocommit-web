import sbt._
class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
    val proguard = "org.scala-tools.sbt" % "sbt-proguard-plugin" % "0.0.5"
}

// vim: set ts=4 sw=4 et:
