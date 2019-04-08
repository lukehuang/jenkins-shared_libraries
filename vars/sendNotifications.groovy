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
        message += "\n" + getChangeString(currentBuild)
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


@NonCPS
def getChangeString(RunWrapper currentBuild) {
    MAX_MSG_LEN = 100
    def changeString = "Change set: "

    echo "Gathering SCM changes"
    def changeLogSets = currentBuild.rawBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            truncated_msg = entry.msg.take(MAX_MSG_LEN)
            changeString += "\t- ${truncated_msg} [${entry.author}]\n"
        }
    }

    if (!changeString) {
        changeString = " - No new changes"
    }
    return changeString
}
