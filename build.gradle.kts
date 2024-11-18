import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.29.0"
}

group = "dev.thorinwasher.schem"
version = System.getenv("SCHEM_VERSION") ?: "dev"
description = "Schematic reader and writer for paper"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-nbt:4.17.0")
    implementation("org.joml:joml:1.10.8")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("net.kyori:adventure-nbt:4.17.0")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.3.1")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)

    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    coordinates(project.group.toString(), "schem-reader", project.version.toString())

    pom {
        description.set("A set of OpenRewrite recipes designed to help developers refactor projects that use MockBukkit.")
        name.set("openrewrite-recipes")
        url.set("https://github.com/Thorinwasher/schem")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://raw.githubusercontent.com/Thorinwasher/schem/refs/heads/main/LICENSE")
            }
        }
        developers {
            developer {
                id.set("thorinwasher")
                name.set("Hjalmar Gunnarsson")
                email.set("officialhjalmar.gunnarsson@outlook.com")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/Thorinwasher/schem.git")
            developerConnection.set("scm:git:ssh://github.com:Thorinwasher/schem.git")
            url.set("https://github.com/Thorinwasher/schem")
        }
    }
    signAllPublications()
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
}