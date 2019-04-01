#!groovy

def call(String dockerBuild) {
    node {
        options {
            // Only keep the 10 most recent builds
            buildDiscarder(logRotator(numToKeepStr: '10'))
            disableConcurrentBuilds()
            ansiColor('xterm')
        }

        stage('Start') {
            sendNotifications 'STARTED'
        }

        stage('Checkout') {
            checkout scm
        }

        stage('Build image') {
            containerImage = docker.build("$dockerBuild", "--no-cache .")
        }

        stage('Push image') {
            docker.withRegistry('', 'docker-credentials') {
                containerImage.push("latest")
                containerImage.push("${BUILD_NUMBER}")
            }
        }

        post {
            always {
                sendNotifications currentBuild.result
            }
        }
    }

}
