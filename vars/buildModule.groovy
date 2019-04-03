#!/usr/bin/env groovy

def call(String sonarProjectKey, String sonarToken, String sonarOrganization = 'frogdevelopment') {
    pipeline {
        agent any

        tools {
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
                    sh './gradlew clean'
                }
            }
            stage('Assemble') {
                steps {
                    gradle 'assemble'
                }
            }
            stage('Test') {
                steps {
                    gradle 'test'
                }
            }
            stage('Analyse') {
                steps {
                    analyseSource(sonarProjectKey, sonarToken, sonarOrganization)
                }
            }
            stage('Publish') {
                steps {
                    gradle 'publishToMavenLocal'
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
