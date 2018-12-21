#!groovy

node {
    stage ('Checkout') {
        checkout scm
    }

    stage ('Add to Jenkins shared-libraries'){
        sh "rm -f /var/lib/jenkins/workflow-libs/*"
        sh "cp -r ${WORKSPACE}/ /var/lib/jenkins/workflow-libs"
    }
}
