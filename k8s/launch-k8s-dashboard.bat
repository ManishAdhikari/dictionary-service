SET k8sDashboardNamespace=kubernetes-dashboard
echo "Opening kubernetes dashboard.."
kubectl proxy -n %k8sDashboardNamespace%