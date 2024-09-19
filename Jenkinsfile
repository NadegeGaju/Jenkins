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
                                bat """
                                REM Print Docker version for debugging
                                docker --version
                                REM Login to Docker Hub
                                echo Logging into Docker Hub...
                                docker login -u %DOCKER_USERNAME% -p %DOCKER_PASSWORD%
                                REM Push the Docker image
                                echo Pushing Docker image ${DOCKER_IMAGE}...
                                docker push ${DOCKER_IMAGE}
                                REM Deployment Strategy - Blue-Green Deployment
                                IF "%DEPLOY_ENV%"=="blue" (
                                    echo Deploying using Blue-Green strategy...
                                    REM Stop and remove the existing container in the target environment
                                    echo Stopping and removing existing container (if exists)...
                                    docker stop my_container_%DEPLOY_ENV% || echo Container not running.
                                    docker rm my_container_%DEPLOY_ENV% || echo Container does not exist.
                                    REM Run the new Docker container in the target environment
                                    echo Running new Docker container...
                                    docker run -d --name my_container_%DEPLOY_ENV% ${DOCKER_IMAGE}
                                    echo Deployment complete.
                                ) ELSE IF "%DEPLOY_ENV%"=="green" (
                                    echo Deploying using Blue-Green strategy...
                                    REM Stop and remove the existing container in the target environment
                                    echo Stopping and removing existing container (if exists)...
                                    docker stop my_container_%DEPLOY_ENV% || echo Container not running.
                                    docker rm my_container_%DEPLOY_ENV% || echo Container does not exist.
                                    REM Run the new Docker container in the target environment
                                    echo Running new Docker container...
                                    docker run -d --name my_container_%DEPLOY_ENV% ${DOCKER_IMAGE}
                                    echo Deployment complete.
                                ) ELSE (
                                    IF %CANARY_PERCENTAGE% GTR 0 (
                                        echo Deploying using Canary strategy...
                                        REM Add your canary deployment logic here
                                        echo Running canary deployment with %CANARY_PERCENTAGE%%...
                                        echo Canary deployment complete.
                                    ) ELSE (
                                        echo Invalid deployment strategy specified.
                                        EXIT /B 1
                                    )
                                )
                                """
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
