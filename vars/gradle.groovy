#!/usr/bin/env groovy

def call(args) {
    sh "chmod +x gradlew; ./gradlew ${args}"
}
