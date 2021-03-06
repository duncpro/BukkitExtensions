plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.6.10"
}

version = "1.1-SNAPSHOT-37"
group = "com.duncpro"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-jdk8
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("com.google.inject.extensions:guice-assistedinject:5.0.1")
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")
    implementation("com.google.inject:guice:5.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

publishing {
    publications {
        repositories {
            maven {
                url = uri("https://duncpro-personal-618824625980.d.codeartifact.us-east-1.amazonaws.com/maven/duncpro-personal/")
                credentials {
                    username = "aws"
                    password = System.getenv("CODEARTIFACT_AUTH_TOKEN")
                }
            }
        }
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}