#!/usr/bin/env groovy

/**
 * Send notifications based on build status string
 */
def call(String buildStatus = 'STARTED') {
    // build status of null means successful
    buildStatus = buildStatus ?: 'SUCCESS'

    // Default values
    def pretext= 'Build status'
    def title = "${env.JOB_NAME}#${env.BUILD_NUMBER}"
    def title_link = "${env.BUILD_URL})"
    def message = "Current status: ${buildStatus}"

    // Override default values based on build status
    if (buildStatus == 'STARTED') {
        colorCode = 'warning'
    } else if (buildStatus == 'SUCCESS') {
        colorCode = 'good'
    } else {
        colorCode = 'danger'
    }

    // Send notifications
    slackSend (color: colorCode, pretext:pretext, title: title, title_link: title_link, message: message)
}
