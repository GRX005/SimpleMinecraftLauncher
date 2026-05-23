plugins {
    id("java")
}

group = "_1ms.sml"
version = "1.7"

repositories {
    mavenCentral()
}

tasks.named<Jar>("jar") {
    manifest {
        // set the Main‑Class attribute
        attributes["Main-Class"] = "_1ms.sml.Main"
    }
    // include all runtime dependencies (unzipped if they are jars)
    /*from(
        configurations
            .runtimeClasspath
            .get()
            .map { file ->
                if (file.isDirectory) file
                else zipTree(file)
            }
    )*/
}