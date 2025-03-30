plugins {
    id("java")
    id("io.freefair.lombok") version "8.10.2"
}

group = "me.itzisonn_.meazy_addon"
version = "2.6"

dependencies {
    implementation("com.github.ItzIsonn:Meazy:f1327886fb")

    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources")
        }
    }
}