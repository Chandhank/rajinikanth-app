pipeline {
    agent any

    environment {
        IMAGE_NAME = "rajini-app"
        IMAGE_TAG = "${BUILD_NUMBER}"
        DEPLOY_COLOR = "${BUILD_NUMBER.toInteger() % 2 == 0 ? 'blue' : 'green'}"
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test Java App') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    script {
                        def fullImage = "${DOCKER_USER}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                        sh """
                            docker build -t ${fullImage} .
                            echo "${DOCKER_PASS}" | docker login -u "${DOCKER_USER}" --password-stdin
                            docker push ${fullImage}
                        """
                        env.FULL_IMAGE = fullImage
                    }
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                    script {
                        def yamlFile = "k8s/deployment-${env.DEPLOY_COLOR}.yaml"
                        sh """
                            sed -i "s|<your-dockerhub-username>/rajini-app:<tag>|${env.FULL_IMAGE}|" ${yamlFile}
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

