plugins {
    id("java")
    id("io.freefair.lombok") version "8.13.1"
}

group = "me.itzisonn_.meazy_addon"
version = "2.7"
description = "MeazyAddon"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("..\\Meazy\\builds\\v2.7\\Meazy-v2.7.jar")) //Your Meazy jar file

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}