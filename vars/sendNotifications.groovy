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
    def color = ''
    def message= ''

    // Override default values based on build
    if (currentBuild.result == null) {
        color = 'warning'
        message = currentBuild.getBuildCauses().shortDescription
    } else {
        if (currentBuild.currentResult == 'SUCCESS') {
            color = 'good'
        } else {
            color = 'danger'
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
