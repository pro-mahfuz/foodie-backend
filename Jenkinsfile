pipeline {
  agent any

  options {
    disableConcurrentBuilds()
    timestamps()
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Verify tools') {
      steps {
        sh '''
          java -version
          mvn -version
          docker version
          docker compose version
        '''
      }
    }

    stage('Test') {
      steps {
        sh 'mvn -B clean verify'
      }
    }

    stage('Build Docker images') {
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

    stage('Deploy containers') {
      when {
        branch 'main'
      }
      steps {
        sh '''
          docker compose up -d --remove-orphans
          docker compose ps
        '''
      }
    }

    stage('Verify deployment') {
      when {
        branch 'main'
      }
      steps {
        sh '''
          for attempt in $(seq 1 30); do
            if curl --fail --silent \
              http://localhost:8080/actuator/health; then
              exit 0
            fi
            sleep 5
          done

          docker compose logs --tail=200
          exit 1
        '''
      }
    }
  }

  post {
    success {
      echo 'Docker images built and containers deployed successfully.'
    }
    failure {
      sh 'docker compose ps || true'
      echo 'Build or deployment failed.'
    }
  }
}