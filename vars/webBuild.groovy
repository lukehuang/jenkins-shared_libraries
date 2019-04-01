#!groovy

def call(String dockerBuild) {
    pipeline {
        agent any

        options {
            // Only keep the 10 most recent builds
            buildDiscarder(logRotator(numToKeepStr: '10'))
            disableConcurrentBuilds()
            ansiColor('xterm')
        }

        environment {
            def containerImage
        }

        stages {
            stage('Start') {
                steps {
                    sendNotifications 'STARTED'
                }
            }

            stage('Build image') {
                steps {
                    containerImage = docker.build("$dockerBuild", "--no-cache .")
                }
            }

            stage('Push image') {
                steps {
                    docker.withRegistry('', 'docker-credentials') {
                        containerImage.push("latest")
                        containerImage.push("${BUILD_NUMBER}")
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
