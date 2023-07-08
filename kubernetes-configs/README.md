### cka/

- 쿠버네티스 초기 설치에 사용된 스크립트

### configmap/

- MetalLB, DB ConfigMap 설정

### dev/

- dev 네임스페이스 내 배포 파일

### elk/

- Helm을 통한 ElasticSearch, Kibana 설치를 위한 커스터마이징 한 values.yaml 및 PV
- Fluentd ConfigMap, DaemonSet, RBAC yaml

### service-account/

- 구버전 Helm 사용을 위해 적용한 Tiller RBAC

### storage-class/

- aws-ebs-sc.yaml / AWS ebs Storage Class

### get_helm.sh

- Helm 설치를 위한 스크립트
