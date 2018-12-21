#!groovy

node {
    stage ('Checkout') {
        checkout scm
    }

    stage ('Add to Jenkins shared-libraries'){
        sh "echo ${JENKINS_HOME}"
        sh "rm -f /var/lib/jenkins/workflow-libs/*"
        sh "cp ${WORKSPACE}/ /var/lib/jenkins/workflow-libs"
    }
}
