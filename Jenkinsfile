pipeline {
    agent any

    environment {
        DOCKER_USER = credentials('dockerhub-creds').username
        DOCKER_PASS = credentials('dockerhub-creds').password
        IMAGE_NAME = "rajini-app"
        IMAGE_TAG = "${BUILD_NUMBER}"
        DEPLOY_COLOR = "${BUILD_NUMBER.toInteger() % 2 == 0 ? 'blue' : 'green'}"
        FULL_IMAGE = "${DOCKER_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test Java App') {
            steps {
                echo "🔧 Building the Java app"
                sh 'mvn clean package'
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                echo "🐳 Building and pushing Docker image to DockerHub"
                sh """
                    docker build -t ${FULL_IMAGE} .
                    echo "${DOCKER_PASS}" | docker login -u "${DOCKER_USER}" --password-stdin
                    docker push ${FULL_IMAGE}
                """
            }
        }

        stage('Deploy to EKS with Blue/Green') {
            steps {
                echo "🚀 Deploying to EKS using ${DEPLOY_COLOR} environment"
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                    script {
                        def yamlFile = "k8s/deployment-${DEPLOY_COLOR}.yaml"
                        sh """
                            sed -i "s|<your-dockerhub-username>/rajini-app:<tag>|${FULL_IMAGE}|" ${yamlFile}
                            kubectl apply -f ${yamlFile}
                            kubectl apply -f k8s/service.yaml
                            kubectl apply -f k8s/hpa.yaml
                        """
                    }
                }
            }
        }
    }
}

