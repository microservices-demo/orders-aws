#!/usr/bin/env bash

#
# Example: GROUP=weaveworksdemos COMMIT=latest ./scripts/build.sh
#

IMAGE=orders-aws

set -evx

SCRIPT_DIR=$(dirname "$0")

if [[ -z "$GROUP" ]] ; then
    echo "Cannot find GROUP env var"
    exit 1
fi

if [[ -z "$COMMIT" ]] ; then
    echo "Cannot find COMMIT env var"
    exit 1
fi

if [[ "$(uname)" == "Darwin" ]]; then
    DOCKER_CMD=docker
else
    DOCKER_CMD="sudo docker"
fi

CODE_DIR=$(cd $SCRIPT_DIR/..; pwd)
echo $CODE_DIR
$DOCKER_CMD run --rm $DOCKER_UID_ARGS \
                -v $CODE_DIR:/usr/src/mymaven \
                -w /usr/src/mymaven \
                maven:3.2-jdk-8 \
                mvn -Dmaven.repo.local=/usr/src/mymaven/.m2 -DskipTests package -U

cp -r $CODE_DIR/docker $CODE_DIR/target/docker/
cp $CODE_DIR/target/uberjar.jar $CODE_DIR/target/docker/${IMAGE}/app.jar

REPO=${GROUP}/${IMAGE}
    $DOCKER_CMD build -t ${REPO}:${COMMIT} $CODE_DIR/target/docker/${IMAGE};
