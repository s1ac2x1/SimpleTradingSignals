import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ta4j:ta4j-parent:0.14")
    implementation("org.ta4j:ta4j-core:0.14")
    implementation("com.squareup.okhttp:okhttp:2.7.5")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.jetbrains:annotations:15.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.googlecode.combinatoricslib:combinatoricslib:2.3")
    implementation("org.ktorm:ktorm-core:3.5.0")
    implementation("org.ktorm:ktorm-support-postgresql:3.5.0")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.platform:junit-platform-launcher:1.8.2")
    testImplementation("org.assertj:assertj-core:3.6.1")
    testImplementation(kotlin("test"))

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("Main")
}