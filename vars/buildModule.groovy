#!/usr/bin/env groovy

def call(String sonarProjectKey) {
    pipeline {
        agent any

        tools {
            jdk 'OpenJ9'
        }

        options {
            buildDiscarder(logRotator(numToKeepStr: '10'))
            disableConcurrentBuilds()
            ansiColor('xterm')
        }

        environment {
            SONAR_TOKEN = credentials("SONAR_${sonarProjectKey}")
            NEXUS = credentials('nexus')
        }

        stages {
            stage('Start') {
                steps {
                    sh 'git fetch'
                    sh './gradlew clean version'
                }
            }
            stage('Assemble') {
                steps {
                    sh './gradlew assemble'
                }
            }
            stage('Test') {
                steps {
                    sh './gradlew test'
                }
            }

            stage('Analyse') {
                steps {
                    sh "./gradlew sonarqube \
                          -Dsonar.projectKey=${sonarProjectKey} \
                          -Dsonar.organization=frogdevelopment \
                          -Dsonar.host.url=https://sonarcloud.io \
                          -Dsonar.login=${SONAR_TOKEN}"
                }
            }

            stage('Publish') {
                steps {
                    sh "./gradlew publish -PnexusUsername=$NEXUS_USR -PnexusPassword=$NEXUS_PSW"
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
