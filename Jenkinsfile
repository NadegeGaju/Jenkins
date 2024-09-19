pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'nadege713/my-spring-app:latest'
        REGISTRY_CREDENTIALS_ID = '66caee6d-a632-43d3-b8af-71b764d9211a' // Docker Hub credentials ID
        DOCKER_HOST = 'tcp://localhost:2375' // Docker server IP and port
        GIT_REPO_URL = 'https://github.com/NadegeGaju/Jenkins.git'
        GIT_BRANCH = 'main' // Change 'main' to your branch if different
        DEPLOY_ENV = 'blue' // Change to 'green' for blue-green deployment
        CANARY_PERCENTAGE = 10 // Percentage for canary deployment
    }
    stages {
        stage('Checkout') {
            steps {
                script {
                    // Checkout code from Git
                    checkout([$class: 'GitSCM',
                        branches: [[name: "*/${GIT_BRANCH}"]],
                        doGenerateSubmoduleConfigurations: false,
                        extensions: [],
                        userRemoteConfigs: [[url: "${GIT_REPO_URL}"]]
                    ])
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    // Build the Java application using Maven
                    try {
                        echo "Building application..."
                        bat 'mvn clean package'
                    } catch (Exception e) {
                        error "Build failed: ${e.message}"
                    }
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    // Run tests
                    try {
                        echo "Running tests..."
                        bat 'mvn test'
                    } catch (Exception e) {
                        error "Tests failed: ${e.message}"
                    }
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image
                    try {
                        echo "Building Docker image ${DOCKER_IMAGE}..."
                        bat "docker build -t ${DOCKER_IMAGE} ."
                    } catch (Exception e) {
                        error "Docker image build failed: ${e.message}"
                    }
                }
            }
        }
      stage('Deploy') {
                  steps {
                      script {
                          try {
                              echo "Starting Docker deployment..."
                              // Deploy Docker image using Docker remote API
                              withEnv(["DOCKER_HOST=${DOCKER_HOST}"]) {
                                  withCredentials([usernamePassword(credentialsId: "${REGISTRY_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                                      sh '''
                                      # Print Docker version for debugging
                                      docker --version
                                      # Login to Docker Hub
                                      echo "Logging into Docker Hub..."
                                      echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin
                                      # Push the Docker image
                                      echo "Pushing Docker image ${DOCKER_IMAGE}..."
                                      docker push ${DOCKER_IMAGE}
                                      # Deployment Strategy - Blue-Green Deployment
                                      if [ "${DEPLOY_ENV}" = "blue" ] || [ "${DEPLOY_ENV}" = "green" ]; then
                                          echo "Deploying using Blue-Green strategy..."
                                          # Stop and remove the existing container in the target environment
                                          echo "Stopping and removing existing container (if exists)..."
                                          docker stop my_container_${DEPLOY_ENV} || true
                                          docker rm my_container_${DEPLOY_ENV} || true
                                          # Run the new Docker container in the target environment
                                          echo "Running new Docker container..."
                                          docker run -d --name my_container_${DEPLOY_ENV} ${DOCKER_IMAGE}
                                          echo "Deployment complete."
                                      elif [ "${CANARY_PERCENTAGE}" -gt 0 ]; then
                                          echo "Deploying using Canary strategy..."
                                          # Example canary deployment steps (customize as needed)
                                          echo "Running canary deployment with ${CANARY_PERCENTAGE}%..."
                                          # Add your canary deployment logic here
                                          # For example, you might deploy to a subset of servers or instances
                                          echo "Canary deployment complete."
                                      else
                                          error "Invalid deployment strategy specified."
                                      fi
                                      '''
                                  }
                              }
                          } catch (Exception e) {
                              error "Deployment failed: ${e.message}"
                          }
                      }
                  }
              }
          }

    post {
        always {
            // Clean up workspace after the pipeline
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
