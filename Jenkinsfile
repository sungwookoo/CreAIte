pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: '*/develop',
                    credentialsId: 'github',
                    url: 'https://github.com/3D6B/gateway.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    // Gradle을 사용해 빌드
                    sh './gradlew clean build'
                }
            }
        }

        stage('Docker build and push') {
            steps {
                script {
                    // Docker 이미지 빌드 및 푸시
                    sh 'docker build -t sungwookoo/gateway:develop .'
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
                        sh 'docker login -u $DOCKER_HUB_USERNAME -p $DOCKER_HUB_PASSWORD'
                        sh 'docker push sungwookoo/gateway:develop'
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                        // kubectl을 사용해 쿠버네티스에 배포
                        sh 'kubectl apply -f kubernetes-configs/dev/gateway/gateway-deployment-dev.yaml -n dev --kubeconfig=$KUBECONFIG'
                    }
                }
            }
        }
    }
}