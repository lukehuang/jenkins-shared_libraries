#!groovy

node {
    stage ('Checkout') {
        checkout scm
    }

    stage ('Add to Jenkins shared-libraries'){
        sh '''
            rsync -av --delete ${WORKSPACE}/ /var/lib/jenkins/workflow-libs
        '''
    }
}
