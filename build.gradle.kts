plugins {
    `java-library`
    java
    idea
}

val junit_version: String  = "5.6.0"
val mockito_version: String  = "2.21.0"
val hamcrest_version: String  = "2.2"

allprojects {
    apply {
        plugin("java")
        plugin("idea")
        plugin("java-library")
    }
    repositories {
        jcenter()
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")
        testImplementation("org.mockito:mockito-core:$mockito_version")
        testImplementation ("org.hamcrest:hamcrest:$hamcrest_version")
    }

    tasks.test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
