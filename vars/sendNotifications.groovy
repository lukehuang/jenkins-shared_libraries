#!/usr/bin/env groovy

/**
 * Send notifications based on build status string
 */
def call(String buildStatus = 'STARTED') {
    // build status of null means successful
    buildStatus = buildStatus ?: 'SUCCESS'

    // Default values
    def pretext= 'Job change of state'
    def title = "${env.JOB_NAME}#${env.BUILD_NUMBER}"
    def title_link = "${env.BUILD_URL}"
    def message = "Current status: ${buildStatus}"
    def color = ''

    // Override default values based on build status
    if (buildStatus == 'STARTED') {
        color = 'warning'
    } else if (buildStatus == 'SUCCESS') {
        color = 'good'
    } else {
        color = 'danger'
    }

    def attachments = [
            [
                    fallback: "${pretext} - ${title}",
                    pretext: pretext,
                    title: title,
                    title_link: title_link,
                    color: color,
                    text: message
            ]
    ]

    // Send notifications
    slackSend(attachments: attachments)
}
