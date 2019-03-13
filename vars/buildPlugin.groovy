#!groovy

def call() {
    pipeline {
        agent any

        options {
            // Only keep the 10 most recent builds
            buildDiscarder(logRotator(numToKeepStr:'10'))
            disableConcurrentBuilds()
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
                            jdk: 'Default'
                    ) {
                        ansiColor("xterm") {
                            sh "mvn clean -e"
                        }
                    }
                }
            }
            stage('Compile') {
                steps {
                    withMaven(
                            maven: 'Default',
                            jdk: 'Default'
                    ) {
                        ansiColor("xterm") {
                            sh "mvn compile -e"
                        }
                    }
                }
            }
            stage('Test') {
                steps {
                    withMaven(
                            maven: 'Default',
                            jdk: 'Default'
                    ) {
                        ansiColor("xterm") {
                            sh "mvn test -e -Dsurefire.useFile=false"
                        }
                    }
                }
            }
            stage('Package') {
                steps {
                    withMaven (
                            maven: 'Default',
                            jdk: 'Default'
                    ) {
                        ansiColor("xterm") {
                            sh "mvn package -DskipTests=true -e"
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
}
