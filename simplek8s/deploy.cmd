docker build -t stephengrider/multi-client:latest -t stephengrider/multi-client:$SHA -f ./client/Dockerfile ./client
docker build -t stephengrider/multi-server:latest -t stephengrider/multi-server:$SHA -f ./server/Dockerfile ./server
docker build -t stephengrider/multi-worker:latest -t stephengrider/multi-worker:$SHA -f ./worker/Dockerfile ./worker

docker push stephengrider/multi-client:latest
docker push stephengrider/multi-server:latest
docker push stephengrider/multi-worker:latest


docker push stephengrider/multi-client:$SHA   ::SHA is an env var in travis.yaml,
docker push stephengrider/multi-server:$SHA   ::    it is the git commit hash      
docker push stephengrider/multi-worker:$SHA

kubectl apply -f k8s
kubectl set image deployments/server-deployment server=stephengrider/multi-server:$SHA
kubectl set image deployments/client-deployment client=stephengrider/multi-client:$SHA
kubectl set image deployments/worker-deployment worker=stephengrider/multi-worker:$SHA