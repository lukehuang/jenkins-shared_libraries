#!groovy

node {
    stage ('Checkout') {
        checkout scm
    }

    stage ('Add to Jenkins shared-libraries'){
        steps {
            sh "rm /var/lib/jenkins/workflow-libs/*"
            sh "cp ${WORKSPACE}/ /var/lib/jenkins/workflow-libs"
        }
    }
}
