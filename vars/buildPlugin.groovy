#!groovy

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
                    // send build started notifications
                    sendNotifications 'STARTED'
                }
            }
            stage('Clean') {
                steps {
                    sh "mvn clean -e"
                }
            }
            stage('Compile') {
                steps {
                    sh "mvn compile -e"
                }
            }
            stage('Test') {
                steps {
                    sh "mvn test -e -Dsurefire.useFile=false"
                }
            }
            stage('Package') {
                steps {
                    sh "mvn package -DskipTests=true -e"
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
