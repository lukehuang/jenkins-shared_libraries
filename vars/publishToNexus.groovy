#!/usr/bin/env groovy

// Upload to Nexus
def call() {
    def nexusCredentialsId = 'nexus'
    withCredentials([[$class: 'UsernamePasswordMultiBinding',
                      credentialsId: nexusCredentialsId,
                      usernameVariable: 'ORG_GRADLE_PROJECT_nexusUsername',
                      passwordVariable: 'ORG_GRADLE_PROJECT_nexusPassword']]) {
        sh '/gradlew publish'
    }
}
