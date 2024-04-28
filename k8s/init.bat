::SET clusterType=k8s/singleNodeClusterConfig.yaml
SET clusterType=k8s/multiNodeClusterConfig.yaml
SET clusterName=my-k8s-kind-cluster
SET appNamespace=dictionary-service-ns
SET appImage=dictionary-service:latest
SET mongoImage=mongo:latest
SET kongNamespace=kong
SET kongIngressCtrlHelmReleaseName=kong-ingrs
SET k8sDashboardNamespace=kubernetes-dashboard
SET calicoTigeraOperatorNamespace=tigera-operator

kind create cluster -n %clusterName% --config %clusterType%
for /F %%v in ('kubectl config current-context') do @SET "currContext=%%v"
echo "Current context is = %currContext%"

kubectl create ns %appNamespace%
kubectl create ns %kongNamespace%
kubectl create ns %k8sDashboardNamespace%

kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml -n %k8sDashboardNamespace%

kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.27.3/manifests/tigera-operator.yaml -n %calicoTigeraOperatorNamespace%
kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.27.3/manifests/custom-resources.yaml -n %calicoTigeraOperatorNamespace%
echo Calico network policy is installing..
timeout 60

helm repo add kong https://charts.konghq.com
helm repo update
helm install %kongIngressCtrlHelmReleaseName% kong/kong -n kong --set ingressController.installCRDs=false
kubectl patch deployment kong-ingrs-kong -n kong --patch-file k8s/patch-kong-deployment.json
kubectl patch service %kongIngressCtrlHelmReleaseName%-kong-proxy -n kong --patch-file k8s/patch-kong-service.json
echo Kong ingress controller is installing..
timeout 30

helm repo add metrics-server https://kubernetes-sigs.github.io/metrics-server/
helm repo update
helm upgrade --install --set args='{--kubelet-insecure-tls}' metrics-server metrics-server/metrics-server --namespace kube-system
echo Metrics server is installing..
timeout 30

kubectl config set-context %currContext% --namespace %appNamespace%
kubectl config use-context %currContext% --namespace %appNamespace%

podman build -t docker.io/library/%appImage% -f Dockerfile
podman save docker.io/library/%appImage% -o target/%appImage%.tar
kind load image-archive target/%appImage%.tar --name %clusterName%
podman pull docker.io/library/%mongoImage%
podman save docker.io/library/%mongoImage% -o target/%mongoImage%.tar
kind load image-archive target/%mongoImage%.tar --name %clusterName%

helm install dictionary-rel dictionary-service-helm-chart
