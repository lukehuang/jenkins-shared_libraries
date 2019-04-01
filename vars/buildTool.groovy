#!/usr/bin/env groovy

def call() {
    pipeline {
        agent any

        tools {
            maven 'Default'
            jdk 'OpenJ9'
        }

        options {
            // Only keep the 10 most recent builds
            buildDiscarder(logRotator(numToKeepStr: '10'))
            disableConcurrentBuilds()
            ansiColor('xterm')
        }

        stages {
            stage('Start') {
                steps {
                    // send build started notifications
                    sendNotifications 'STARTED'
                }
            }
            stage('Clean') {
                steps {
                    sh "mvn clean -e -B"
                }
            }
            stage('Package') {
                steps {
                    sh "mvn package -e -B"
                }
            }
            stage('Docker Build') {
                steps {
                    sh "mvn dockerfile:build -e -B"
                }
            }
            stage('Docker Push') {
                steps {
                    dockerize()
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
