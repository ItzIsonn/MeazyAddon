plugins {
    java
}

group = "me.itzisonn_.meazy_addon"
version = "2.7"
description = "MeazyAddon"
java.sourceCompatibility = JavaVersion.VERSION_25

val lombokVersion = "1.18.42"



repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("..\\Meazy\\builds\\v2.7\\Meazy-v2.7.jar")) //Your Meazy jar file

    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
}



tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}