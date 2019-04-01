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
            stage('Clean') {
                steps {
                    withMaven(
                            maven: 'Default',
                            jdk: 'OpenJ9'
                    ) {
                        sh 'mvn --version'
                    }
                }
            }
        }

        post {
            always {
                sendNotifications currentBuild.result
            }
        }
    }
}
