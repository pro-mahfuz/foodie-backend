pipeline {
  agent any
  stages {
    stage('Test') { steps { sh 'mvn test' } }
    stage('Build image') { steps { sh 'docker build -t foodie-backend:${BUILD_NUMBER} .' } }
    stage('Deploy') {
      when { branch 'main' }
      steps { echo 'Push the image to ECR and deploy to EC2 here using Jenkins credentials.' }
    }
  }
}
