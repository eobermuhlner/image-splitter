plugins {
    java
    application
}

group = "ch.obermuhlner.imagesplitter"
version = "0.2-SNAPSHOT"

application {
    mainClass.set("ch.obermuhlner.imagesplitter.ImageSplitter")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.compileJava {
    options.release.set(8)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}