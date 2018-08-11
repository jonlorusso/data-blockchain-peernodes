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
        VERSION = readMavenPom().getVersion().replace('-SNAPSHOT', '')
        ARTIFACT_ID = readMavenPom().getArtifactId()

        // DOCKER env
	REGISTRY = "docker.dev.ruvpfs.swatt.exchange" 
        branchTag = makeDockerTag("${env.BRANCH_NAME}")
        IMAGE_NAME ="${REGISTRY}/${ARTIFACT_ID}"
        IMAGE_TAG = "${tag}-${VERSION}.${env.BUILD_NUMBER}"

// TODO git_password is being echo'd to jenkins console, suppress output with:
// Pass custom shebang line without -x: sh('#!/bin/sh -e\n' + 'echo shellscript.sh arg1 arg2')TODO git_password is being echo'd to jenkins console, suppress output with:
// Pass custom shebang line without -x: sh('#!/bin/sh -e\n' + 'echo shellscript.sh arg1 arg2')
        // GIT env
	GIT_CREDENTIALS = credentials('80610dce-f3b7-428e-b69f-956eb087225d')
	GIT_USERNAME = "${env.GIT_CREDENTIALS_USR}"
	GIT_PASSWORD = java.net.URLEncoder.encode("${env.GIT_CREDENTIALS_PSW}", "UTF-8")
        ORIGIN = "https://${GIT_USERNAME}:${GIT_PASSWORD}@${scmUrl}"

    }
  stages {

    stage ('Start') {
      steps {
	slackSend (color: '#FFFF00', message: "STARTED: ${JOB}")
      }
    }

    stage('Test') {
      steps {
	sh 'mvn clean package'
      }
      post {
	always {
	  junit(allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml')
	}
      }
    }

    stage('Deploy') {
      steps {
	script {
	    sh "mvn clean deploy -Ddockerfile.tag=${IMAGE_TAG} --activate-profiles docker"
	}
      }
    }

//    state('Release Hotfix')
//    state('Release Release')

    // when a PR is merged to the develop branch,
    // create a release candidate Docker image
    stage('Release') {
      when {
	branch 'develop'
      }
      steps {
	sh("git checkout -B release/${VERSION}")
          sh("git push ${ORIGIN} release/${VERSION}")

	  sh('docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:release-${VERSION}.${env.BUILD_NUMBER}')
	  sh('docker push ${IMAGE_NAME}:release-${VERSION}.${env.BUILD_NUMBER}')
      }
    }

    // when a release/hotfix branch is merged to master,
    // add a git tag for that specific version
    stage('Post Release') {
      when {
	branch 'master'
      }
      steps {
	sh("git tag -f ${VERSION}")
	sh("git push --tags  ${VERSION}")
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
