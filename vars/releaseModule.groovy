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

        environment {
            NEXUS = credentials('nexus')
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
                    sh './gradlew clean version'
                }
            }
            stage('Tag') {
                steps {
                    sh "git tag -af -m 'release ${params.VERSION}' ${params.VERSION}"
                    sh './gradlew version'
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
            stage('Push tag') {
                steps {
//                    sh 'git push --follow-tags'
                    sh 'git push origin master'
                }
            }
            stage('Publish') {
                steps {
                    sh "./gradlew publish -PnexusUsername=$NEXUS_USR -PnexusPassword=$NEXUS_PSW"
                }
            }
        }

        post {

            failure {
                echo 'Deleting failed tag'
                sh "git tag -d ${params.VERSION}"
            }

            changed {
                sendNotifications currentBuild
            }
        }
    }
}
