#!/usr/bin/env groovy

/**
 * Send notifications based on build status string
 */
def call(String buildStatus = 'STARTED') {
    // build status of null means successful
    buildStatus = buildStatus ?: 'SUCCESS'

    // Default values
    def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
    def summary = "${subject} (${env.BUILD_URL})"

    // Override default values based on build status
    if (buildStatus == 'STARTED') {
        colorCode = '#FFFF00' // YELLOW
    } else if (buildStatus == 'SUCCESS') {
        colorCode = '#00FF00' // GREEN
    } else {
        colorCode = '#FF0000' // RED
    }

    // Send notifications
    slackSend (color: colorCode, message: summary)
}
