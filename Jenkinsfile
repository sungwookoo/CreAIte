pipeline {
    agent {
        kubernetes {
            label 'kubernetes-agent'
            defaultContainer 'jnlp'
            yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    app: jenkins-agent
spec:
  containers:
  - name: jnlp
    image: jenkins/inbound-agent:latest
    args: ['\$(JENKINS_SECRET)', '\$(JENKINS_NAME)']
    volumeMounts:
      - mountPath: /var/jenkins_home
        name: workspace-volume
  - name: java
    image: openjdk:11-jdk
    command:
    - cat
    tty: true
    env:
    - name: JAVA_HOME
      value: "/usr/local/openjdk-11"
    volumeMounts:
      - mountPath: /var/jenkins_home
        name: workspace-volume
  - name: docker
    image: docker:latest
    command:
    - cat
    tty: true
    volumeMounts:
      - name: docker-socket
        mountPath: /var/run/docker.sock
  - name: kubectl
    image: bitnami/kubectl:latest
    command:
    - cat
    tty: true
    workingDir: '/home/jenkins/agent'
  volumes:
  - name: docker-socket
    hostPath:
      path: /var/run/docker.sock
  - name: workspace-volume
    emptyDir: {}
"""
        }
    }

    stages {
        stage('Build') {
            steps {
                container('java') {
                    script {
                        // 실행 권한 추가
                        sh 'chmod +x ./gradlew'
                        
                        // Gradle을 사용해 빌드
                        sh './gradlew clean build'
                    }
                }
            }
        }

        stage('Docker build and push') {
            steps {
                container('docker') {
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
        }

        stage('Deploy to Kubernetes') {
            steps {
                container('kubectl') {
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
}
