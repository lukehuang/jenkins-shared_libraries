#!groovy

def call() {
    pipeline {
        agent any

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
                            jdk: 'Default'
                    ) {
                        sh "mvn clean -e"
                    }
                }
            }
            stage('Compile') {
                steps {
                    withMaven(
                            maven: 'Default',
                            jdk: 'Default'
                    ) {
                        sh "mvn compile -e"
                    }
                }
            }
            stage('Test') {
                steps {
                    withMaven(
                            maven: 'Default',
                            jdk: 'Default'
                    ) {
                        sh "mvn test -e -Dsurefire.useFile=false"
                    }
                }
            }

            stage('Package') {
                steps {
                    withMaven (
                            maven: 'Default',
                            jdk: 'Default'
                    ) {
                        sh "mvn package -DskipTests=true -e"
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