// Define the project
lazy val root = (project in file("."))
  .settings(
    scalaVersion := "3.3.1",
    
    // Include ScalaFX dependency
    libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32",

    // Determine OS version of JavaFX binaries
    libraryDependencies ++= {
      val osName = System.getProperty("os.name") match {
        case n if n.startsWith("Linux")   => "linux"
        case n if n.startsWith("Mac")     => "mac"
        case n if n.startsWith("Windows") => "win"
        case _                            => throw new Exception("Unknown platform!")
      }
      Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
        .map(m => "org.openjfx" % s"javafx-$m" % "19" classifier osName)
    },

    // Specify the main class for the application
    mainClass := Some("scimg.gui.MainWindow"),

    // Include Maven Central repository
    resolvers += Resolver.mavenCentral
  )
