
plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.rabbitmq:amqp-client:5.18.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("de.sfuhrm:sudoku:5.0.3")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.12.1")
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "pcd.ass3.sudoku.CooperativeSudoku"
}
