pipeline {
    agent any

    tools {
        // Tell Jenkins to use Maven installed on the Jenkins instance
        maven 'maven-latest'  // Make sure your Jenkins has Maven installed and named appropriately
    }

    environment {
        // Set environment variables (optional)
        DOCKER_REGISTRY = 'nadege713/my-spring-app'
    }

    stages {
        stage('Checkout') {
            steps {
                // Pull the code from your GitHub repository using credentials
                git branch: 'main',
                    url: 'https://github.com/your-username/my-app.git',
                    credentialsId: 'b901875b-2c86-4658-af99-c4e2772c94bc' // Replace with your GitHub credentials ID
            }
        }

        stage('Build') {
            steps {
                // Clean and build the Maven project
                script {
                    sh 'mvn clean package'
                }
            }
        }

        stage('Test') {
            steps {
                // Run the unit tests using Maven
                script {
                    sh 'mvn test'
                }
            }
        }

        stage('Docker Build') {
            steps {
                // Build the Docker image for your app
                script {
                    sh 'docker build -t ${DOCKER_REGISTRY} .'
                }
            }
        }

        stage('Docker Push') {
            steps {
                // Log in and push the Docker image to Docker Hub
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push ${DOCKER_REGISTRY}'
                }
            }
        }

        stage('Deploy') {
            steps {
                // Deploy the Docker container (for example, on a VM or Kubernetes)
                sh 'docker run -d -p 8081:8080 ${DOCKER_REGISTRY}'
            }
        }
    }

    post {
        always {
            // Clean up resources (optional)
            echo 'Cleaning up...'
            sh 'docker system prune -f'
        }
    }
}
