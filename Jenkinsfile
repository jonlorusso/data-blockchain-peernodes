pipeline {
    agent {
        docker {
            image 'maven'
            args '-v $HOME/.m2:/root/.m2:z -u root'
            reuseNode true
        }
    }
    environment {
        JOB = "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
        VERSION = readMavenPom().getVersion()
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
                sh 'mvn clean deploy -Ddockerfile.tag=${env.BRANCH_NAME}-${VERSION}.${env.BUILD_NUMBER} --activate-profiles jenkins'
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
