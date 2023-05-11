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
    env:
    - name: BUILD_NUMBER
      value: "${env.BUILD_NUMBER}"
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
                        // Git 설치
                        sh 'apk add --no-cache git'

                        sh 'git config --global --add safe.directory /home/jenkins/agent/workspace/alarm'

                        // Git commit 해시 가져오기
                        def gitCommitHash = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()

                        // kubernetes secret에서 파일을 가져와 resources 디렉토리에 복사
                        sh 'cp /etc/secrets/firebase_service_key.json src/main/resources/firebase_service_key.json'

                        // Docker 이미지 빌드 및 푸시
                        sh "docker build -t sungwookoo/alarm:develop-${gitCommitHash} ."
                        withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
                            sh 'docker login -u $DOCKER_HUB_USERNAME -p $DOCKER_HUB_PASSWORD'
                            sh "docker push sungwookoo/alarm:develop-${gitCommitHash}"
                        }
                        env.DOCKER_IMAGE_TAG = "sungwookoo/alarm:develop-${gitCommitHash}"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                container('kubectl') {
                    script {
                        withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                            sh 'apt-get update' // 패키지 목록 업데이트
                            sh 'apt-get install -y gettext' // gettext 설치
                            sh "export DOCKER_IMAGE_TAG=${env.DOCKER_IMAGE_TAG} && envsubst < alarm-deployment-dev.yaml > temp-deployment.yaml"
                            sh 'kubectl apply -f temp-deployment.yaml -n dev --kubeconfig=$KUBECONFIG'
                            sh 'kubectl apply -f alarm-service-dev.yaml -n dev --kubeconfig=$KUBECONFIG'
                            sh 'rm temp-deployment.yaml' // 임시 파일 삭제
                        }
                    }
                }
            }
        }
    }
}
