#!/bin/bash

set -e


PARAMS=""

SERVER=""

while (( "$#" )); do
  case "$1" in
    -s|--server)
      if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
        SERVER=$2
        shift 2
      else
        echo "Error: Argument for $1 is missing" >&2
        exit 1
      fi
      ;;
    -*) # unsupported flags
      echo "Error: Unsupported flag $1" >&2
      exit 1
      ;;
    *) # preserve positional arguments
      PARAMS="$PARAMS $1"
      shift
      ;;
  esac
done

# set positional arguments in their proper place
eval set -- "$PARAMS"

if [ -z "$SERVER" ]
then
  echo "Server is undefined"
  exit 1
fi


echo "Building as $artifactory_user and $portainer_user"

IMAGE="$(./mvnw help:evaluate -Dexpression=image.name -q -DforceStdout)"
echo "Building $IMAGE"
docker build . --build-arg jenkins_auth="$jenkins_auth" --build-arg artifactory_user="$artifactory_user" -t "$IMAGE"

echo "Pushing $IMAGE"
docker image push "$IMAGE"
TOKEN=$(http POST huta16-d:9000/api/auth Username="$portainer_user" Password="$portainer_auth" --ignore-stdin  | jq .jwt -r)

APP_NAME=$(./mvnw help:evaluate -Dexpression=project.name -q -DforceStdout | sed "s/ /_/g")
echo "Cleaning up old versions of $APP_NAME"
CONTAINERS=$(http GET huta16-d:9000/api/endpoints/1/docker/containers/json?filters=\{\"name\":\{\""/$APP_NAME"\":true\}\} "Authorization: Bearer $TOKEN" --ignore-stdin  -b)
echo "Found running containers $CONTAINERS"

if [[ ! -z $(echo $CONTAINERS | jq .[]? ) ]]
then
  IDS=$(echo $CONTAINERS | jq -r '.[] | .Id')
  for id in $IDS
  do
    echo "Stopping $id"
    if http POST huta16-d:9000/api/endpoints/1/docker/containers/"$id"/stop "Authorization: Bearer $TOKEN" --check-status --ignore-stdin &> /dev/null
    then
      STOP_STATUS=$(http POST huta16-d:9000/api/endpoints/1/docker/containers/"$id"/wait "Authorization: Bearer $TOKEN" --check-status --ignore-stdin -b)
      echo "Stopped $id with status $STOP_STATUS"
      echo "Removing $id"
      if http DELETE huta16-d:9000/api/endpoints/1/docker/containers/"$id" "Authorization: Bearer $TOKEN" --check-status --ignore-stdin &> /dev/null
      then
        echo "Removed $id"
      else
          case $? in
              4) echo "Cannot remove $id" ;;
              5) echo 'HTTP 5xx Server Error!' ;;
              *) echo 'Other Error!' ;;
          esac
      fi
    else
        case $? in
            3) echo "$id already stopped" ;;
            4) echo "$id does not exist" ;;
            5) echo 'HTTP 5xx Server Error!' ;;
            *) echo 'Other Error!' ;;
        esac
    fi
  done
else
  echo "No containers found $CONTAINERS"
fi

echo "Creating $IMAGE"
CREATE_RESP=$(http -f POST huta16-d:9000/api/endpoints/1/docker/images/create "Authorization: Bearer $TOKEN" "fromImage=$IMAGE" --ignore-stdin -b)

if [[ -z  "$CREATE_RESP" ]]
then
  echo "No response when trying to create the image for $IMAGE"
else
  IFS=$'\n'
  for st in $CREATE_RESP
  do
    echo $st | jq .status -r
  done
fi

echo "Starting container $APP_NAME"

CONTAINER_CREATE=$(http POST huta16-d:9000/api/endpoints/1/docker/containers/create "Authorization: Bearer $TOKEN" \
name=="$APP_NAME" \
Image="$IMAGE" \
HostConfig:='{ "PortBindings": { "8080/tcp": [{ "HostPort": "8090" }] }, "RestartPolicy": {"Name":"always" } }' \
ExposedPorts:='{ "8080/tcp": {} }' \
Env:='["SPRING_PROFILES_ACTIVE=dev"]' \
 --ignore-stdin -b)

echo $CONTAINER_CREATE

if [[ -z $(echo $CONTAINER_CREATE | jq .Id ) ]]
then
  echo "Could not create container $(jq -r .message)"
  echo "Could not create container $(jq -r .Warnings)"
else
  NEW_APP=$(echo $CONTAINER_CREATE | jq -r .Id)
  if http POST huta16-d:9000/api/endpoints/1/docker/containers/"$NEW_APP"/start "Authorization: Bearer $TOKEN" --check-status --ignore-stdin &> /dev/null
  then
    echo "Starting $APP_NAME - $id"
  else
      case $? in
        3) echo "$APP_NAME already started";;
        4) echo "No such container $id" ;;
        5) echo 'HTTP 5xx Server Error!' ;;
        *) echo 'Other Error!' ;;
      esac
  fi
fi