plugins {
    java
    id("com.palantir.graal") version ("0.10.0")
    id("org.openjfx.javafxplugin") version ("0.0.10")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

graal {
    mainClass("io.github.jjelliott.progsdump.ProgsDumpPrototyperGui")
    outputName("pd-monster-prototyper")
    graalVersion("22.0.0.2")
    javaVersion("11")
}

javafx {
//    version = "11.0.2"
    modules ("javafx.controls", "javafx.fxml")
}
