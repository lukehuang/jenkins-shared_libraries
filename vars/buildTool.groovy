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
            DOCKER = credentials('docker-credentials')
        }

        stages {
            stage('Jar Build') {
                steps {
                    sh './gradlew clean bootJar'
                }
            }

            stage('Docker Build') {
                steps {
                    sh "./gradlew jib \
                                -Djib.to.auth.username=$DOCKER_USR \
                                -Djib.to.auth.password=$DOCKER_PSW \
                                -Djib.to.tags=${getGitBranchName()} \
                                -Djib.console='plain' \
                                -x bootBuildInfo"
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

def getGitBranchName() {
    return scm.branches[0].name
}
