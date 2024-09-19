pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'nadege713/my-spring-app:latest'
        REGISTRY_CREDENTIALS_ID = '66caee6d-a632-43d3-b8af-71b764d9211a'
        DEPLOY_ENV = 'blue'
        CANARY_PERCENTAGE = 10
    }
    stages {
        stage('Build') {
            steps {
                script {
                    echo "Building application..."
                    sh 'mvn clean package'
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    echo "Running tests..."
                    sh 'mvn test'
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker image ${DOCKER_IMAGE}..."
                    sh "docker build -t ${DOCKER_IMAGE} ."
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    echo "Starting Docker deployment..."
                    withCredentials([usernamePassword(credentialsId: "${REGISTRY_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh '''
                        docker --version
                        echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin
                        docker push ${DOCKER_IMAGE}
                        if [ "${DEPLOY_ENV}" = "blue" ] || [ "${DEPLOY_ENV}" = "green" ]; then
                            docker stop my_container_${DEPLOY_ENV} || true
                            docker rm my_container_${DEPLOY_ENV} || true
                            docker run -d --name my_container_${DEPLOY_ENV} ${DOCKER_IMAGE}
                        elif [ "${CANARY_PERCENTAGE}" -gt 0 ]; then
                            echo "Running canary deployment with ${CANARY_PERCENTAGE}%..."
                        else
                            error "Invalid deployment strategy specified."
                        fi
                        '''
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
