plugins {
    java
    id("com.palantir.graal") version ("0.10.0")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("info.picocli:picocli-codegen:4.6.3")
    implementation("info.picocli:picocli:4.6.3")
    implementation(project(":common"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

graal {
    mainClass("io.github.jjelliott.progsdump.ProgsDumpPrototyper")
    outputName("pd-monster-prototyper")
    graalVersion("22.0.0.2")
    javaVersion("11")
}

