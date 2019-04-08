#!/usr/bin/env groovy

import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

/**
 * Send notifications based on build status string
 */
def call(RunWrapper currentBuild) {
    // build message
    def pretext = "${currentBuild.currentResult} : ${currentBuild.fullProjectName}"
    def title = "${env.JOB_NAME}#${env.BUILD_NUMBER}"
    def title_link = "${env.BUILD_URL}"
    def color = currentBuild.currentResult == 'SUCCESS' ? 'good' : 'danger'
    def message = getCauses() + "\n" + getChangeString(currentBuild)
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
def getCauses(RunWrapper currentBuild) {
    def causes = currentBuild!=null ? "Build causes: ${currentBuild.getBuildCauses()}": 'Build causes: unknown'

    return causes
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
