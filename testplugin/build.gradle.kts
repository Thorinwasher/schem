plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.thorinwasher"
version = "dev"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    implementation(project(":"))
}

tasks {
    runServer {
        minecraftVersion("1.21")
        downloadPlugins {
            // modrinth("worldedit", "Jo76t1oi")
            url("https://cdn.modrinth.com/data/z4HZZnLr/versions/CyUQUWfI/FastAsyncWorldEdit-Bukkit-2.11.0.jar")
        }
    }

    processResources {
        filesMatching("**/plugin.yml") {
            expand(project.properties)
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}