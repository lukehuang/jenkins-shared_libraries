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
                    sh "mvn clean -e -B"
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
            stage('Package') {
                steps {
                    sh "mvn package -DskipTests=true -e -B"
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
