plugins {
    id("java")
}

group = "dev.thorinwasher"
version = System.getenv("TAG_VERSION") ?: "dev"
description = "Schematic reader and writer for paper"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-nbt:4.17.0")
    implementation("org.joml:joml:1.10.8")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)

    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform()
}