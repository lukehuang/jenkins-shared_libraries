#!groovy

def call(String sonarProjectKey, String sonarToken) {
    pipeline {
        agent any

        stages {
            stage('Clean') {
                steps {
                    withMaven(
                            maven: 'Default',
                            jdk: 'Default'
                    ) {
                        sh "mvn clean -e"
                    }
                }
            }
            stage('Compile') {
                steps {
                    withMaven(
                            maven: 'Default',
                            jdk: 'Default'
                    ) {
                        sh "mvn compile -e"
                    }
                }
            }
            stage('Test') {
                steps {
                    withMaven(
                            maven: 'Default',
                            jdk: 'Default'
                    ) {
                        sh "mvn test -e -Dsurefire.useFile=false"
                    }
                }
            }

            stage('Analyse') {
                steps {
                    withMaven(
                            maven: 'Default',
                            jdk: 'Default'
                    ) {
                        sh "mvn sonar:sonar \
                          -Dsonar.projectKey=${sonarProjectKey} \
                          -Dsonar.organization=frogdevelopment \
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
                            jdk: 'Default'
                    ) {
                        sh "mvn install -Dmaven.test.skip=true -e "
                    }
                }
            }
        }
    }
}
