plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.8"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "dev.omega24.hoppersort"
version = "0.1.0"

dependencies {
    paperDevBundle("1.19-R0.1-SNAPSHOT")
}

tasks {
    runServer {
        minecraftVersion("1.19")
    }

    reobfJar {
        outputJar.set(project.layout.buildDirectory.file("libs/${rootProject.name}-${rootProject.version}.jar"))
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to project.group,
                "version" to project.version
            )
        }
    }
}
