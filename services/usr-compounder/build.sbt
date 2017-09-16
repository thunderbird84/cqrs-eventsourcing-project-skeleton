javaOptions in reStart += "-Xmx128m"
mainClass in reStart := Some("robin.dev.examples.UserCompoundMainDev")
//Revolver.enableDebugging(port = 5050, suspend = false)