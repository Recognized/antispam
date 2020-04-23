buildscript {
    repositories {
        jcenter()
    }
}

repositories {
    jcenter()
    mavenCentral()
    google()
    flatDir {
        dirs = setOf(file("libs"))
    }

//    maven("https://dl.bintray.com/kotlin/ktor")
//    maven("https://dl.bintray.com/kotlin/kotlinx")
}

plugins {
    kotlin("jvm") version Versions.kotlin
    application
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("log4j:log4j:1.2.17")
    implementation("org.slf4j:slf4j-simple:1.7.29")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation(files("libs/morph-1.5.jar", "libs/russian-1.5.jar", "libs/english-1.5.jar"))
    implementation("com.github.ajalt:clikt:1.4.0")
}

application {
    mainClassName = "antispam.MainKt"
}