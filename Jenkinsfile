#!groovy

node {
    stage ('Checkout') {
        checkout scm
    }

    stage ('Add to Jenkins shared-libraries'){
        sh "rm -f /var/jenkins_home/workflow-libs/*"
        sh "cp -r ${WORKSPACE}/vars /var/jenkins_home/workflow-libs"
    }
}
