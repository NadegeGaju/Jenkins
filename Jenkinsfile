pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'nadege713/my-spring-app:latest'
        REGISTRY_CREDENTIALS_ID = '66caee6d-a632-43d3-b8af-71b764d9211a'
        DOCKER_HOST = 'tcp://localhost:2375'
        GIT_REPO_URL = 'https://github.com/NadegeGaju/Jenkins.git'
        GIT_BRANCH = 'main'
        DEPLOY_ENV = 'blue'
        CANARY_PERCENTAGE = 10
    }
    stages {
        stage('Checkout') {
            steps {
                script {
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
                        withEnv(["DOCKER_HOST=${DOCKER_HOST}"]) {
                            withCredentials([usernamePassword(credentialsId: "${REGISTRY_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                                bat '''
                                REM Print Docker version for debugging
                                docker --version
                                REM Login to Docker Hub
                                echo %DOCKER_PASSWORD% | docker login -u %DOCKER_USERNAME% --password-stdin
                                REM Push the Docker image
                                docker push %DOCKER_IMAGE%
                                REM Deployment Strategy - Blue-Green Deployment
                                if "%DEPLOY_ENV%" == "blue" (
                                    echo Deploying using Blue-Green strategy...
                                    docker stop my_container_blue || true
                                    docker rm my_container_blue || true
                                    docker run -d --name my_container_blue %DOCKER_IMAGE%
                                    echo Deployment complete.
                                ) else if "%DEPLOY_ENV%" == "green" (
                                    echo Deploying using Blue-Green strategy...
                                    docker stop my_container_green || true
                                    docker rm my_container_green || true
                                    docker run -d --name my_container_green %DOCKER_IMAGE%
                                    echo Deployment complete.
                                ) else (
                                    if %CANARY_PERCENTAGE% gtr 0 (
                                        echo Deploying using Canary strategy...
                                        echo Running canary deployment with %CANARY_PERCENTAGE%%%...
                                        echo Canary deployment complete.
                                    ) else (
                                        exit /b 1
                                    )
                                )
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
