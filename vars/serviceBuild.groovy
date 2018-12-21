#!groovy

def call() {
    pipeline {
        agent any

        stages {
            stage('Clean') {
                steps {
                    withMaven(
                            maven: 'Default',
                            jdk: 'Java 10'
                    ) {
                        sh "mvn clean -e"
                    }
                }
            }
            stage('Compile') {
                steps {
                    withMaven(
                            maven: 'Default',
                            jdk: 'Java 10'
                    ) {
                        sh "mvn compile -e"
                    }
                }
            }
            stage('Test') {
                steps {
                    withMaven(
                            maven: 'Default',
                            jdk: 'Java 10'
                    ) {
                        sh "mvn test -e"
                        step( [ $class: 'JacocoPublisher' ] )
                    }
                }
            }
//        stage('Verify') {
//            steps {
//                withMaven(
//                        maven: 'Default',
//                        jdk: 'Java 10'
//                ) {
//                    sh "mvn verify -e"
//                }
//            }
//        }

            stage('Package') {
                steps {
                    withMaven (
                            maven: 'Default',
                            jdk: 'Java 10'
                    ) {
                        sh "mvn package -DskipTests=true -e"
                    }
                }
            }

            stage('Docker Build') {
                steps {
                    withMaven (
                            maven: 'Default',
                            jdk: 'Java 10'
                    ) {
                        sh "mvn dockerfile:build -e"
                    }
                }
            }
            stage('Docker Push') {
                steps {
                    withMaven (
                            maven: 'Default',
                            jdk: 'Java 10'
                    ) {
                        withCredentials([
                                usernamePassword(
                                        credentialsId: 'docker-credentials',
                                        usernameVariable: 'USERNAME',
                                        passwordVariable: 'PASSWORD')]
                        ) {
                            sh "mvn dockerfile:push -e -Ddockerfile.username=$USERNAME -Ddockerfile.password=$PASSWORD"
                        }
                    }
                }
            }
        }
    }
}