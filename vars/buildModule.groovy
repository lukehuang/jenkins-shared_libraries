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

        parameters {
            string(name: 'VERSION', description: 'What is the new version to release ?')
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

            stage('Release') {
                when {
                    branch 'master'
                    expression { return params.VERSION }
                }
                steps {
                    sh "git tag -af -m 'release ${params.VERSION}' ${params.VERSION}"
                    sh "git push ${params.VERSION}:${params.VERSION} HEAD:master"
                    sh './gradlew version'
                }
            }

            stage('Analyse') {
                steps {
//                    analyseSource(sonarProjectKey, sonarToken, sonarOrganization)
                    sh "./gradlew sonarqube \
                          -Dsonar.projectKey=${sonarProjectKey} \
                          -Dsonar.organization=${sonarOrganization} \
                          -Dsonar.host.url=https://sonarcloud.io \
                          -Dsonar.login=${sonarToken}"
                }
            }

            stage('Publish') {
                steps {
                    publishToNexus()
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
