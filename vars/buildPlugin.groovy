#!/usr/bin/env groovy

def call() {
    pipeline {
        agent any

        tools {
            maven 'Default'
            jdk 'Default'
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
                    sh "mvn clean -e"
                }
            }
            stage('Compile') {
                steps {
                    sh "mvn compile -e -B"
                }
            }
            stage('Test') {
                steps {
                    sh "mvn test -e -B -Dsurefire.useFile=false"
                }
            }
            stage('Package') {
                steps {
                    sh "mvn package -DskipTests=true -e -B"
                }
            }
        }

        post {
            always {
                sendNotifications currentBuild
            }
        }
    }
}
