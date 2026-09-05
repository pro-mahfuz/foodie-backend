pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        timestamps()
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"

        COMPOSE_PROJECT_NAME = 'foodie-backend'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Tools') {
            steps {
                sh '''
                    echo "======================================"
                    echo " Environment"
                    echo "======================================"

                    echo "JAVA_HOME=${JAVA_HOME}"
                    echo "PATH=${PATH}"

                    echo ""
                    echo "Java:"
                    which java
                    java -version

                    echo ""
                    echo "Javac:"
                    which javac
                    javac -version

                    echo ""
                    echo "Maven:"
                    which mvn
                    mvn -version

                    echo ""
                    echo "Docker:"
                    docker version

                    echo ""
                    echo "Docker Compose:"
                    docker compose version

                    echo ""
                    echo "Maven Toolchains:"
                    cat ~/.m2/toolchains.xml 2>/dev/null || true
                '''
            }
        }

        stage('Test') {
            steps {
                sh '''
                    echo "======================================"
                    echo " Running Maven Tests"
                    echo "======================================"

                    mvn -B clean verify
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                sh '''
                    echo "======================================"
                    echo " Building Docker Images"
                    echo " Build Number: ${BUILD_NUMBER}"
                    echo "======================================"

                    IMAGE_TAG=${BUILD_NUMBER} docker compose build
                '''
            }
        }

        stage('Stop Old Containers') {
            steps {
                sh '''
                    echo "======================================"
                    echo " Stopping Old Containers"
                    echo "======================================"

                    IMAGE_TAG=${BUILD_NUMBER} docker compose down \
                        --remove-orphans || true
                '''
            }
        }

        stage('Deploy Containers') {
            steps {
                sh '''
                    echo "======================================"
                    echo " Deploying Containers"
                    echo "======================================"

                    IMAGE_TAG=${BUILD_NUMBER} docker compose up \
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
                    echo " Waiting For Services"
                    echo "======================================"

                    sleep 10

                    echo ""
                    echo "Running containers:"
                    docker compose ps

                    echo ""
                    echo "Docker containers:"
                    docker ps

                    echo ""
                    echo "Checking failed/exited containers..."

                    FAILED=$(docker compose ps \
                        --status exited \
                        --services || true)

                    if [ -n "$FAILED" ]; then
                        echo "ERROR: Some services have exited:"
                        echo "$FAILED"

                        docker compose logs \
                            --tail=200

                        exit 1
                    fi

                    echo ""
                    echo "Deployment containers are running."
                '''
            }
        }

        stage('Cleanup') {
            steps {
                sh '''
                    echo "======================================"
                    echo " Cleanup"
                    echo "======================================"

                    docker image prune -f
                '''
            }
        }
    }

    post {

        success {
            echo "======================================"
            echo "Deployment successful."
            echo "Build: ${BUILD_NUMBER}"
            echo "======================================"

            sh '''
                docker compose ps
            '''
        }

        failure {
            echo "======================================"
            echo "Build or deployment failed."
            echo "Build: ${BUILD_NUMBER}"
            echo "======================================"

            sh '''
                echo ""
                echo "Docker Compose status:"
                docker compose ps || true

                echo ""
                echo "Docker containers:"
                docker ps -a || true

                echo ""
                echo "Recent Docker Compose logs:"
                docker compose logs --tail=200 || true
            '''
        }

        always {
            echo "Pipeline finished."
        }
    }
}