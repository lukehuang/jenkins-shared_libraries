#!/usr/bin/env groovy

def call() {
    pipeline {
        agent any

        options {
            // Only keep the 10 most recent builds
            buildDiscarder(logRotator(numToKeepStr: '10'))
            disableConcurrentBuilds()
            ansiColor('xterm')
        }

        stages {
            stage('Clean Repository') {
                steps {
                    sh 'rm -rf /root/.m2/repository'
                }
            }
            stage('Clean Docker') {
                steps {
                    sh 'docker system prune -af'
                }
            }
        }

        post {
            failure {
                sendNotifications currentBuild.result
            }
        }
    }
}
