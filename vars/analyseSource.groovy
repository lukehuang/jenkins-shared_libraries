

def call(String sonarProjectKey, String sonarToken, String sonarOrganization = 'frogdevelopment') {
    sh "mvn sonar:sonar \
      -Dsonar.projectKey=${sonarProjectKey} \
      -Dsonar.organization=${sonarOrganization} \
      -Dsonar.host.url=https://sonarcloud.io \
      -Dsonar.login=${sonarToken} \
      -e -B"
}
