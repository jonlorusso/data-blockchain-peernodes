def scmUrl = scm.getUserRemoteConfigs()[0].getUrl().replaceAll('https://', '')

def String makeDockerTag(String input) {
  return input
    .replaceFirst('^[#\\.]', '') // delete the first letter if it is a period or dash
    .replaceAll('[^a-zA-Z0-9_#\\.]', '_'); // replace everything that's not allowed with an underscore
}

pipeline {
  agent any
  tools {
      maven 'maven3'
  }
    environment {
      JOB = "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
        VERSION = readMavenPom().getVersion().replace("-SNAPSHOT", "")
        ARTIFACT_ID = readMavenPom().getArtifactId()
        DOCKER_FRIENDLY_BRANCH_NAME = makeDockerTag("${env.BRANCH_NAME}")
        TAG = "${DOCKER_FRIENDLY_BRANCH_NAME}-${VERSION}.${env.BUILD_NUMBER}"
    }
  stages {

    stage ('Start') {
      steps {
        slackSend (color: '#FFFF00', message: "STARTED: ${JOB}")
      }
    }

    stage('Deploy') {
      steps {
        script {
            sh "mvn clean deploy -DskipTests -Ddockerfile.tag=${TAG} --activate-profiles docker"

            // Spotify Docker plugin:
            // mvn package -> .jar
            // mvn deploy -> artifactory
            // docker build -t NAME .
            // docker push NAME
        }
      }
    }

    stage('Release') {
      environment {
        REGISTRY = "docker.dev.ruvpfs.swatt.exchange"
          GIT_CREDENTIALS = credentials('80610dce-f3b7-428e-b69f-956eb087225d')
          GIT_USERNAME = "${env.GIT_CREDENTIALS_USR}"
          GIT_PASSWORD = java.net.URLEncoder.encode("${env.GIT_CREDENTIALS_PSW}", "UTF-8")
      }
      when {
        branch 'develop'
      }
      steps {
        sh("git checkout -B release/${VERSION}")

          // need to hide git password from jenkins console
          sh("git push https://${GIT_USERNAME}:${GIT_PASSWORD}@${scmUrl} release/${VERSION}")

          sh("docker tag ${REGISTRY}/${ARTIFACT_ID}:${TAG} ${REGISTRY}/${ARTIFACT_ID}:release-${VERSION}")
          sh("docker push ${REGISTRY}/${ARTIFACT_ID}:release-${VERSION}")
      }
    }
  }
  post {
    success {
      slackSend (color: '#00FF00', message: "SUCCESSFUL: ${JOB}")
    }
    failure {
      slackSend (color: '#FF0000', message: "FAILED: ${JOB}")
    }
  }
}
