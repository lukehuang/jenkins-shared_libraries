#!/usr/bin/env groovy

import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

/**
 * Send notifications based on build status string
 */
def call(RunWrapper currentBuild) {
    // build message
    def pretext = "Build ${currentBuild.currentResult} for ${currentBuild.fullProjectName}"
    def color = currentBuild.currentResult == 'SUCCESS' ? '#00FF00' : '#FF0000'
    def message = getChangeString(currentBuild)
    def attachments = [
            [
                    fallback  : pretext,
                    pretext   : pretext,
                    title     : currentBuild.fullDisplayName,
                    title_link: currentBuild.absoluteUrl,
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
    def changeString = "Change set:"

    echo "Gathering SCM changes"
    def changeLogSets = currentBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            changeString += "\n\t- by ${entry.author} on ${entry.date}: ${entry.msg}"
        }
    }

    if (!changeString) {
        changeString = " No new changes"
    }
    return changeString
}
