#!groovy

node {
    stage ('Checkout') {
        checkout scm
    }

    stage ('Add to Jenkins shared-libraries'){
        steps {
          step {
            sh "rm /var/lib/jenkins/workflow-libs/*"
          }
          step {
            sh "cp ${WORKSPACE}/ /var/lib/jenkins/workflow-libs"
          }
        }
    }
}
