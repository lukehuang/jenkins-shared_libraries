#!/usr/bin/env groovy

def call() {
    withCredentials([usernamePassword(
            credentialsId: 'docker-credentials',
            usernameVariable: 'USERNAME',
            passwordVariable: 'PASSWORD')]) {
        sh "mvn dockerfile:push -e -B \
            -Ddockerfile.username=$USERNAME\
            -Ddockerfile.password=$PASSWORD"
    }
}
