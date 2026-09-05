pipeline {
    agent any

    options {
        timestamps()
        skipDefaultCheckout(true)
    }

    environment {
        IMAGE_TAG = "${BUILD_NUMBER}"
        COMPOSE_PROJECT_NAME = "foodie-backend"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Docker') {
            steps {
                sh '''
                    echo "Docker:"
                    docker version

                    echo ""
                    echo "Docker Compose:"
                    docker compose version
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                sh '''
                    echo "======================================"
                    echo "Building Docker images"
                    echo "Image tag: ${IMAGE_TAG}"
                    echo "======================================"

                    IMAGE_TAG=${IMAGE_TAG} docker compose build
                '''
            }
        }

        stage('Stop Old Containers') {
            steps {
                sh '''
                    echo "Stopping old containers..."

                    IMAGE_TAG=${IMAGE_TAG} docker compose down \
                        --remove-orphans || true
                '''
            }
        }

        stage('Deploy Containers') {
            steps {
                sh '''
                    echo "Starting containers with tag ${IMAGE_TAG}..."

                    IMAGE_TAG=${IMAGE_TAG} docker compose up \
                        -d \
                        --no-build \
                        --remove-orphans
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    echo "Waiting for services..."
                    sleep 15

                    echo ""
                    echo "======================================"
                    echo "Docker Compose"
                    echo "======================================"

                    IMAGE_TAG=${IMAGE_TAG} docker compose ps

                    echo ""
                    echo "======================================"
                    echo "Running Containers"
                    echo "======================================"

                    docker ps

                    echo ""
                    echo "======================================"
                    echo "API Gateway"
                    echo "======================================"

                    curl --fail \
                        --retry 10 \
                        --retry-delay 3 \
                        http://127.0.0.1:8080 || {
                            echo "API Gateway is not responding."
                            IMAGE_TAG=${IMAGE_TAG} docker compose logs \
                                --tail=200 api-gateway
                            exit 1
                        }
                '''
            }
        }

        stage('Cleanup') {
            steps {
                sh '''
                    docker image prune -f
                '''
            }
        }
    }

    post {

        success {
            echo "foodie-backend build #${BUILD_NUMBER} deployed successfully."

            sh '''
                IMAGE_TAG=${IMAGE_TAG} docker compose ps
            '''
        }

        failure {
            echo "foodie-backend build #${BUILD_NUMBER} failed."

            sh '''
                echo "=== Containers ==="
                docker ps -a || true

                echo ""
                echo "=== Compose logs ==="
                IMAGE_TAG=${IMAGE_TAG} docker compose logs \
                    --tail=300 || true
            '''
        }
    }
}