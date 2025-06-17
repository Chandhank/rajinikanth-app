pipeline {
    agent any

    environment {
        IMAGE_NAME = "rajini-app"
        IMAGE_TAG = "${BUILD_NUMBER}"
        DEPLOY_COLOR = "${BUILD_NUMBER.toInteger() % 2 == 0 ? 'blue' : 'green'}"
        AWS_REGION = "us-east-1"
        CLUSTER_NAME = "rajini-cluster"
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test Java App') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build & Push Docker Image') {
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

        stage('Configure AWS Credentials') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'aws-creds', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                    sh '''
                        aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
                        aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
                        aws configure set default.region us-east-1
                    '''
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                    script {
                        def yaml = "k8s/deployment-${env.DEPLOY_COLOR}.yaml"
                        sh """
                            sed -i "s|<your-dockerhub-username>/rajini-app:<tag>|${env.FULL_IMAGE}|" ${yaml}
                            kubectl apply -f ${yaml} --validate=false
                            kubectl apply -f k8s/service.yaml --validate=false
                            kubectl apply -f k8s/hpa.yaml --validate=false
                        """
                    }
                }
            }
        }
    }
}

