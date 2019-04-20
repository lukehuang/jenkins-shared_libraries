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
            SONAR_LOGIN = credentials("SONAR_${sonarProjectKey}")
            DOCKER = credentials('docker-credentials')
        }

        stages {
            stage('Start') {
                steps {
                    sh './gradlew clean'
                }
            }
            stage('Assemble') {
                steps {
                    sh './gradlew assemble'
                }
            }
            stage('Test') {
                steps {
                    sh './gradlew test -x bootBuildInfo'
                }
            }
            stage('Analyse') {
                steps {
                    sh "./gradlew sonarqube \
                          -Dsonar.projectKey=${sonarProjectKey} \
                          -Dsonar.organization=frogdevelopment \
                          -Dsonar.host.url=https://sonarcloud.io \
                          -Dsonar.login=${SONAR_LOGIN} \
                          -x bootBuildInfo test"
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
                sh 'JENKINS_NODE_COOKIE=dontKillMe'
            }
        }
    }
}


def getGitBranchName() {
    return scm.branches[0].name
}
