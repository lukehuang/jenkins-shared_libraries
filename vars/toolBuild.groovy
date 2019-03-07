#!groovy

def call() {
    pipeline {
        agent any

        options {
            // Only keep the 10 most recent builds
            buildDiscarder(logRotator(numToKeepStr:'10'))
        }

        stages {
            stage ('Start') {
                steps {
                    // send build started notifications
                    sendNotifications 'STARTED'
                }
            }
            stage('Clean') {
                steps {
                    withMaven(
                            maven: 'Default',
                            jdk: 'OpenJ9'
                    ) {
                        sh "mvn clean -e"
                    }
                }
            }

            stage('Package') {
                steps {
                    withMaven (
                            maven: 'Default',
                            jdk: 'OpenJ9'
                    ) {
                        sh "mvn package -e"
                    }
                }
            }

            stage('Docker Build') {
                steps {
                    withMaven (
                            maven: 'Default',
                            jdk: 'OpenJ9'
                    ) {
                        sh "mvn dockerfile:build -e"
                    }
                }
            }
            stage('Docker Push') {
                steps {
                    withMaven (
                            maven: 'Default',
                            jdk: 'OpenJ9'
                    ) {
                        withCredentials([
                                usernamePassword(credentialsId: 'docker-credentials',
                                        usernameVariable: 'USERNAME',
                                        passwordVariable: 'PASSWORD')]) {
                            sh "mvn dockerfile:push -e -B -Ddockerfile.username=$USERNAME -Ddockerfile.password=$PASSWORD"
                        }
                    }
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
