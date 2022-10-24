import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version SPRING_BOOT
    id("io.spring.dependency-management") version SPRING_DEPENDENCY_MANAGEMENT
    kotlin("jvm") version KOTLIN
    kotlin("plugin.spring") version KOTLIN
    kotlin("plugin.jpa") version KOTLIN
    kotlin("plugin.serialization") version KOTLIN
}

group = "com.genlz"
version = "0.0.1-SNAPSHOT"

java {
    targetCompatibility = JAVA
    sourceCompatibility = JAVA
}

repositories {
    mavenCentral()
    maven("https://repo.spring.io/milestone")
    maven("https://repo.spring.io/snapshot")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$KTX_SERIALIZATION")
    implementation("com.google.code.gson:gson")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    runtimeOnly("com.h2database:h2")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JAVA.majorVersion
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
