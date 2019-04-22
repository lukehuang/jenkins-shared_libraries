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
        }

//        parameters {
//            string(name: 'VERSION', description: 'What is the new version to release ?')
//        }

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

//            stage('Release') {
//                when {
//                    branch 'master'
//                    expression { return params.VERSION }
//                }
//                steps {
//                    sh "git tag -af -m 'release ${params.VERSION}' ${params.VERSION}"
//                    sh 'git tag -l'
//                    sh "git push ${params.VERSION}:${params.VERSION}"
//                    sh './gradlew version'
//                }
//            }

            stage('Analyse') {
                steps {
//                    analyseSource(sonarProjectKey, sonarToken, sonarOrganization)
                    sh "./gradlew sonarqube \
                          -Dsonar.projectKey=${sonarProjectKey} \
                          -Dsonar.organization=frogdevelopment \
                          -Dsonar.host.url=https://sonarcloud.io \
                          -Dsonar.login=${SONAR_TOKEN}"
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
