plugins {
    `java-library`
}

version = "1.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")
    implementation("com.google.inject:guice:5.0.1")
    implementation(files("../server/bundler/libraries/spigot-api-1.18.1-R0.1-SNAPSHOT.jar"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}