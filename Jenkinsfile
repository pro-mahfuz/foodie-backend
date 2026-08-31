pipeline {
  agent any

  options {
    disableConcurrentBuilds()
    timestamps()
  }

  stages {
    stage('Test') {
      steps {
        sh 'mvn clean verify'
      }
    }

    stage('Build images') {
      steps {
        sh '''
          docker compose build \
            eureka-server \
            config-server \
            user-service \
            food-service \
            order-service \
            api-gateway
        '''
      }
    }

    stage('Deploy') {
      when {
        branch 'main'
      }
      steps {
        sh 'docker compose up -d --remove-orphans'
      }
    }
  }

  post {
    success {
      echo 'Build and deployment completed successfully.'
    }
    failure {
      echo 'Build or deployment failed. The existing deployment was preserved where possible.'
    }
  }
}