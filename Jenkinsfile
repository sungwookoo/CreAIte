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
    securityContext:
      runAsUser: 0
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
        
		/*
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
		*/

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
							def buildNumber = env.BUILD_NUMBER
							def deploymentTemplate = readFile("gateway-deployment-dev.yaml")
              def deployment = deploymentTemplate.replaceAll("\\\\$\\\\{BUILD_NUMBER\\\\}", buildNumber)
							
							writeFile(file: "temp-deployment.yaml", text: deployment)
							sh "kubectl apply -f temp-deployment.yaml -n dev --kubeconfig=$KUBECONFIG"
							sh "kubectl apply -f gateway-service-dev.yaml -n dev --kubeconfig=$KUBECONFIG"
							sh "rm temp-deployment.yaml" // 임시 파일 삭제
                        }
                    }
                }
            }
        }
    }
}
