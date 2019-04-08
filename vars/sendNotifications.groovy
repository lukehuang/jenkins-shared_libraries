#!/usr/bin/env groovy

import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

/**
 * Send notifications based on build status string
 */
def call(RunWrapper currentBuild) {
    // Default values
    def pretext = 'Job change of state'
    def title = "${env.JOB_NAME}#${env.BUILD_NUMBER}"
    def title_link = "${env.BUILD_URL}"
    def color = 'good'
    def message = ''

    // Override default values based on build
    if (currentBuild.result == null) {
        color = 'warning'
        message = "Build causes: ${currentBuild.getBuildCauses()}"
    } else {
        if (currentBuild.currentResult != 'SUCCESS') {
            color = 'danger'
            message = currentBuild.rawBuild.getLog(10)
        }
    }

    def attachments = [
            [
                    fallback  : "${pretext} - ${title}",
                    pretext   : pretext,
                    title     : title,
                    title_link: title_link,
                    color     : color,
                    text      : message
            ]
    ]

    // Send notifications
    slackSend(attachments: attachments)
}
