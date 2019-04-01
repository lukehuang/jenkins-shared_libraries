#!groovy

def call(String sonarProjectKey, String sonarToken, String sonarOrganization = 'frogdevelopment') {
    pipeline {
        agent any

        options {
            // Only keep the 10 most recent builds
            buildDiscarder(logRotator(numToKeepStr: '10'))
            disableConcurrentBuilds()
        }

        ansiColor("xterm") {
            stages {
                stage('Start') {
                    steps {
                        // send build started notifications
                        sendNotifications 'STARTED'
                    }
                }
                stage('Clean') {
                    steps {
                        withMaven(
                                maven: 'Default',
                                jdk: 'OpenJ9'
                        ) {
                            sh "mvn clean -e"
                        }
                    }
                }
                stage('Compile') {
                    steps {
                        withMaven(
                                maven: 'Default',
                                jdk: 'OpenJ9'
                        ) {
                            sh "mvn compile -e"
                        }
                    }
                }
                stage('Test') {
                    steps {
                        withMaven(
                                maven: 'Default',
                                jdk: 'OpenJ9'
                        ) {
                            sh "mvn test -e -Dsurefire.useFile=false"
                        }
                    }
                }
                stage('Analyse') {
                    steps {
                        withMaven(
                                maven: 'Default',
                                jdk: 'OpenJ9'
                        ) {
                            sh "mvn sonar:sonar \
                                  -Dsonar.projectKey=${sonarProjectKey} \
                                  -Dsonar.organization=${sonarOrganization} \
                                  -Dsonar.host.url=https://sonarcloud.io \
                                  -Dsonar.login=${sonarToken} \
                                  -e "
                        }
                    }
                }
                stage('Install') {
                    steps {
                        withMaven(
                                maven: 'Default',
                                jdk: 'OpenJ9'
                        ) {
                            sh "mvn install -Dmaven.test.skip=true -e "
                        }
                    }
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
