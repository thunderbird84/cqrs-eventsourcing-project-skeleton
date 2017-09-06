import java.io.File

import sbt.Keys._
import sbt._

import scala.util.Try
import scala.collection.JavaConverters._

object build extends com.typesafe.sbt.pom.PomBuild {

  lazy val defaultSettings = Seq(
    publishArtifact in packageSrc := false,
    cancelable in Global := true,
    crossPaths in ThisBuild := false,
    scalaVersion in ThisBuild := "2.12.2",
    publishArtifact in packageDoc := false,

    javacOptions in ThisBuild ++= Seq(
      "-source", "1.8",
      "-target", "1.8",
      "-encoding", "UTF-8"
    ),
    packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
      "Built-By" -> "robin",
      "Implementation-Version" -> java.time.Instant.now().toString
    ),
    exportJars := true,
    exportJars in Test := false,
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-s")
  )


  def checkChanged(timestampFile: File, glob: PathFinder): Boolean = {
    val prevLastModified: Long = Try { IO.read(timestampFile).toLong } getOrElse 0L
    timestampFile.delete()

    val lastModifiedFile: File = glob.get.toVector
      .filter { _.isFile }
      .sortBy { _.lastModified() }
      .last

    IO.write(timestampFile, lastModifiedFile.lastModified().toString)
    lastModifiedFile.lastModified() != prevLastModified
  }

  lazy val dockerPack = taskKey[Unit]("Package app jars")
  lazy val dockerComp = taskKey[Unit]("Builds a  docker-compose")

  lazy val dockerSettings = Seq(
    dockerPack := {
      val dockerTarget: File = baseDirectory.value / "target" / "docker"
      val classPathB = Vector.newBuilder[String]
      var fileCount = 0
      for (jar: Attributed[File] <- (fullClasspath in Compile).value) {
        val m: ModuleID = jar.metadata(Keys.moduleID.key)
        val dir: String = if (m.revision.endsWith("-SNAPSHOT")) "libs" else "repo"
        val pathSegments: Array[String] = dir +: m.organization.split('.') :+ m.name :+ m.revision :+ jar.data.name
        val path: String = pathSegments.mkString("/")
        IO.copyFile(jar.data, new File(dockerTarget, path), preserveLastModified = true)
        fileCount += 1
        classPathB += "repo/"+pathSegments.drop(1).mkString("/")
      }
      //println(s"Copied $fileCount jar-files to docker target")

      IO.copyDirectory(baseDirectory.value / "src" / "main" / "docker", dockerTarget)
      IO.write(dockerTarget / "bin" / "classpath", classPathB.result.mkString(":"))
    },

    dockerComp := {
      println(dockerPack.value)
      val stageFolder: File = target.value
      if (!checkChanged(timestampFile = stageFolder / ".dockerlast", glob = stageFolder.***)) {
        streams.value.log.info("No changes in " + stageFolder)
      } else {
        s"docker-compose rm -f ${name.value}".!
        s"docker-compose build ${name.value}".!
        s"docker-compose up -d --no-deps ${name.value}".!
      }
    }
  )

  override def projectDefinitions(baseDirectory: File): Seq[Project] = {
    super.projectDefinitions(baseDirectory) map { project: Project =>
      var p = project
        //.settings(spray.revolver.RevolverPlugin.Revolver.settings)
        .settings(defaultSettings)

      if (new File(project.base, "src/main/docker").exists())
        p = p.settings(dockerSettings)

      p
    }
  }
}
