#!/bin/bash
set -e
appName=$1
port=$2
# Site service doesn't currently write logs to a volume but it may in the future,
# so will leave the volSuffix param in place to match other similar deploy scripts.
volSuffix=$3
buildEnv=$4
repo=$5

if [[ "$VERSION" == "current" ]]; then
    version="$( ./cicdutils/pom_version.sh )"
    cp src/main/resources/application-dev.properties src/main/resources/application.properties
    mvn clean dependency:copy -P docker
else
    version=$VERSION
    mkdir target
    sv=$( ./cicdutils/get_version.sh newsite/new-site-api -o target/siteapp.jar --version $VERSION )
fi

name="$( ./cicdutils/pom_artifact.sh )" 
export DOCKER_CONFIG=(HostConfig:="{ \"PortBindings\": { \"8080/tcp\": [ { \"HostPort\": \"${port}\" } ] }, \"NetworkMode\": \"reverseproxy-nw\", \"RestartPolicy\": {\"Name\":\"always\" } }" )  
./cicdutils/docker_build_deploy.sh -i "${name}${volSuffix}:${version}" -a "$appName" -s "$APP_SERVER" -t "$TEAM" -d "$DOCKER_CONFIG"   

if [[ "$buildEnv" == "prod" ]]; then
    ./cicdutils/wait_for_tomcat.sh $APP_HOST $port site/api/v1/islands
    ./cicdutils/notify_teams.sh $teams_webhook "Site API ${version} deployed to production"
    ./cicdutils/notify_zendesk_springboot.sh
    ./cicdutils/remove_old_tomcat.sh $port $APP_PORTS
fi