#!groovy
def call(String dockerBuild) {
    node {

        stage ('Checkout') {
            checkout scm
        }

        stage ('Build image') {
            containerImage = docker.build("$dockerBuild", "--no-cache .")
        }

        stage ('Push image') {
            docker.withRegistry('','docker-credentials') {
                containerImage.push("latest")
                containerImage.push("${BUILD_NUMBER}")
            }
        }
    }

}