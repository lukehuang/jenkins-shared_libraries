#!/usr/bin/env groovy

def call() {
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
                   script {
                       if (params.VERSION == null) {
                           error("Build failed because of missing version to release")
                       }
                   }
                    sh 'git fetch'
                    sh './gradlew clean'
                }
            }
//            stage('Assemble') {
//                steps {
//                    sh './gradlew assemble'
//                }
//            }
//            stage('Test') {
//                steps {
//                    sh './gradlew test'
//                }
//            }
            stage('Release') {
                steps {
                    echo "git tag -a -m \"release ${params.VERSION}\" ${params.VERSION}"
                    sh "git tag -a -m \"release ${params.VERSION}\" ${params.VERSION}"
                    sh 'git tag -l'
//                    sh 'git push --follow-tags origin master'
                }
            }
            stage('Publish') {
                steps {
//                    publishToNexus()
                    sh './gradlew version'
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
