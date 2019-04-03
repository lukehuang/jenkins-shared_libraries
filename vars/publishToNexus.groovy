#!/usr/bin/env groovy

def call() {
    withCredentials([[$class: 'UsernamePasswordMultiBinding',
                      credentialsId: 'nexus',
                      usernameVariable: 'ORG_GRADLE_PROJECT_nexusUsername',
                      passwordVariable: 'ORG_GRADLE_PROJECT_nexusPassword']]) {
        sh './gradlew publish'
    }
}
