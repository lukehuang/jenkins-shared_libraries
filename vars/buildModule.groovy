#!/usr/bin/env groovy

def call(String sonarProjectKey, String sonarToken, String sonarOrganization = 'frogdevelopment') {
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
            stage('Analyse') {
                steps {
                    analyseSource(sonarProjectKey, sonarToken, sonarOrganization)
                }
            }
            stage('Install') {
                steps {
                    sh "mvn install -Dmaven.test.skip=true -e -B"
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
