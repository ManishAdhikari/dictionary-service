::SET clusterType=k8s/singleNodeClusterConfig.yaml
SET clusterType=k8s/multiNodeClusterConfig.yaml
SET clusterName=my-k8s-kind-cluster
SET appNamespace=dictionary-service-ns
SET appImage=dictionary-service
SET appImageTag=latest
SET mongoImage=mongo
SET mongoImageTag=4.0.8

kind create cluster -n %clusterName% --config %clusterType%

echo "======== Kubernetes dashboard is installing.. ========"
helm repo add kubernetes-dashboard https://kubernetes.github.io/dashboard/
helm upgrade --install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard --create-namespace --namespace kubernetes-dashboard
helm install k8s-dashboard-rel ./k8s-dashboard-helm-chart
echo "======== Waiting for kubernetes dashboard to become available.. ======== "
timeout 30

echo "======== Calico network policy is installing.. ========"
helm repo add projectcalico https://docs.tigera.io/calico/charts
helm repo update
helm install calico projectcalico/tigera-operator --version v3.27.3 --create-namespace --namespace tigera-operator
echo "======== Waiting for calico network policy to become available.. ======== "
timeout 60

echo "======== Kong ingress controller is installing.. ========"
helm repo add kong https://charts.konghq.com
helm repo update
helm install kong kong/kong --create-namespace --namespace kong --set ingressController.installCRDs=false
kubectl patch deployment kong-kong -n kong --patch-file k8s/patch-kong-deployment.json
kubectl patch service kong-kong-proxy -n kong --patch-file k8s/patch-kong-service.json
echo "======== Waiting for kong ingress controller to become available.. ======== "
timeout 60

echo "======== Metrics server is installing.. ========"
helm repo add metrics-server https://kubernetes-sigs.github.io/metrics-server/
helm repo update
helm upgrade --install metrics-server metrics-server/metrics-server -n kube-system
kubectl patch deployment metrics-server -n kube-system --patch-file k8s/patch-metrics-server-deployment.json
echo "======== Waiting for metrics server to become available.. ======== "
timeout 30

for /F %%v in ('kubectl config current-context') do @SET "currContext=%%v"
echo "Current context is = %currContext%"
kubectl create ns %appNamespace%
kubectl config set-context %currContext% --namespace %appNamespace%
kubectl config use-context %currContext% --namespace %appNamespace%

podman build -t docker.io/library/%appImage%:%appImageTag% -f Dockerfile
podman save docker.io/library/%appImage%:%appImageTag% -o target/%appImage%-%appImageTag%.tar
kind load image-archive target/%appImage%-%appImageTag%.tar --name %clusterName%
podman pull docker.io/library/%mongoImage%:%mongoImageTag%
podman save docker.io/library/%mongoImage%:%mongoImageTag% -o target/%mongoImage%-%mongoImageTag%.tar
kind load image-archive target/%mongoImage%-%mongoImageTag%.tar --name %clusterName%

echo "======== Installing mongodb helm chart.. ========"
kubectl taint nodes --all node-role.kubernetes.io/control-plane-
helm install mongodb-rel ./mongodb-helm-chart
echo "======== Waiting for mongodb deployment to become available.. ======== "
timeout 30
echo "======== Installing dictionary-service helm chart.. ========"
helm install dictionary-rel ./dictionary-service-helm-chart

::helm uninstall dictionary-rel
::helm uninstall mongodb-rel
