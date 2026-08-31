pipeline {
  agent any
  stages {
    stage('Test') { steps { sh 'mvn test' } }
    stage('Build images') { steps { sh 'docker compose build eureka-server config-server user-service food-service order-service api-gateway' } }
    stage('Deploy') {
      when { branch 'main' }
      steps { echo 'Push the image to ECR and deploy to EC2 here using Jenkins credentials.' }
    }
  }
}
