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

        stage('Stop Old Containers') {
            steps {
                sh '''
                    echo "======================================"
                    echo "Stopping old containers"
                    echo "======================================"

                    IMAGE_TAG=${IMAGE_TAG} docker compose down \
                        --remove-orphans || true
                '''
            }
        }

        stage('Delete Old Docker Images') {
            steps {
                sh '''
                    echo "======================================"
                    echo "Deleting old foodie Docker images"
                    echo "======================================"

                    docker images --format '{{.Repository}}:{{.Tag}}' \
                        | grep -E '^foodie-(eureka-server|config-server|user-service|food-service|order-service|api-gateway):' \
                        | xargs -r docker rmi -f

                    echo ""
                    echo "Remaining foodie images:"
                    docker images | grep foodie || true
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

        stage('Deploy Containers') {
            steps {
                sh '''
                    echo "======================================"
                    echo "Starting containers"
                    echo "Image tag: ${IMAGE_TAG}"
                    echo "======================================"

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
		            echo "======================================"
		            echo "Docker Compose Status"
		            echo "======================================"
		
		            IMAGE_TAG=${IMAGE_TAG} docker compose ps
		
		            echo ""
		            echo "======================================"
		            echo "Waiting for API Gateway"
		            echo "======================================"
		
		            MAX_ATTEMPTS=30
		            ATTEMPT=1
		
		            while [ $ATTEMPT -le $MAX_ATTEMPTS ]; do
		
		                echo "Health check $ATTEMPT/$MAX_ATTEMPTS..."
		
		                if curl -fsS \
		                    --connect-timeout 3 \
		                    --max-time 5 \
		                    http://127.0.0.1:8084/actuator/health; then
		
		                    echo ""
		                    echo "API Gateway is healthy."
		                    exit 0
		                fi
		
		                echo "API Gateway not ready yet."
		                sleep 5
		
		                ATTEMPT=$((ATTEMPT + 1))
		            done
		
		            echo ""
		            echo "ERROR: API Gateway did not become healthy."
		
		            echo "=== API Gateway logs ==="
		            IMAGE_TAG=${IMAGE_TAG} docker compose logs \
		                --tail=200 api-gateway
		
		            echo "=== Config Server logs ==="
		            IMAGE_TAG=${IMAGE_TAG} docker compose logs \
		                --tail=200 config-server
		
		            exit 1
		        '''
		    }
		}

        stage('Cleanup') {
            steps {
                sh '''
                    echo "Cleaning unused Docker resources..."

                    docker image prune -f
                '''
            }
        }
    }

    post {

        success {
            echo "foodie-backend build #${BUILD_NUMBER} deployed successfully."

            sh '''
                echo ""
                echo "======================================"
                echo "Docker Compose Status"
                echo "======================================"

                IMAGE_TAG=${IMAGE_TAG} docker compose ps

                echo ""
                echo "======================================"
                echo "Foodie Docker Images"
                echo "======================================"

                docker images | grep foodie || true
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
