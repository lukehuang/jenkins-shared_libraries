#!/usr/bin/env groovy

def call() {
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

        parameters {
            string(name: 'VERSION', description: 'What is the new version to release ?')
        }

        stages {
            stage('Start') {
                steps {
                   script {
                       if (params.VERSION == null) {
                           error("Build failed because of missing version to release")
                       }
                   }
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
                steps {
                    sh "git tag -af -m 'release ${params.VERSION}' ${params.VERSION}"
                    sh './gradlew version'
                    sh 'git push --follow-tags'
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
