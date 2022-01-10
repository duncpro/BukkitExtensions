plugins {
    java
    `java-library`
}

version = "1.0"

repositories {
    mavenCentral()
}

val embedded: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val embeddedApi: Configuration by configurations.creating {
    configurations.api.get().extendsFrom(this)
}

dependencies {
    embeddedApi("com.fasterxml.jackson.module:jackson-module-guice:2.13.1")
    embeddedApi("com.fasterxml.jackson.core:jackson-databind:2.13.1")
    embedded("commons-cli:commons-cli:1.5.0")
    embedded("com.h2database:h2:2.0.204")
    embeddedApi("com.google.inject:guice:5.0.1")
    compileOnly("com.google.auto.factory:auto-factory:1.0.1")
    annotationProcessor("com.google.auto.factory:auto-factory:1.0.1")
    implementation(files("../server/bundler/libraries/spigot-api-1.18.1-R0.1-SNAPSHOT.jar"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.jar {
    dependsOn(embedded)
    embedded.forEach { file -> from(zipTree(file)) }
    embeddedApi.forEach { file -> from(zipTree(file)) }
    duplicatesStrategy = DuplicatesStrategy.WARN
}

configurations.create("pluginJar")
artifacts.add("pluginJar", tasks.jar)
