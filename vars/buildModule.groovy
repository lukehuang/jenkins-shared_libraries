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
//                    sendNotifications 'STARTED'
                    sh './gradlew clean'
                }
            }
            stage('Assemble') {
                steps {
                    echo "gradle version : ${ORG_GRADLE_PROJECT_version}"
                    sh './gradlew assemble'
                }
            }
//            stage('Test') {
//                steps {
//                    sh './gradlew test'
//                }
//            }
//            stage('Analyse') {
//                steps {
////                    analyseSource(sonarProjectKey, sonarToken, sonarOrganization)
//                    sh "./gradlew sonarqube \
//                          -Dsonar.projectKey=${sonarProjectKey} \
//                          -Dsonar.organization=${sonarOrganization} \
//                          -Dsonar.host.url=https://sonarcloud.io \
//                          -Dsonar.login=${sonarToken}"
//                }
//            }
//            stage('Publish') {
//                steps {
//                   publishToNexus()
//                }
//            }
        }

        post {
            always {
//                sendNotifications currentBuild.result
            }
        }
    }
}
