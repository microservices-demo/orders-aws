#!/usr/bin/env bash

if [[ "$(uname)" == "Darwin" ]]; then
    DOCKER_CMD=docker
    DOCKER_UID_ARGS=""
else
    if [[ -n "$DOCKER_CONTAINER_ROOT" ]]; then
        DOCKER_UID_ARGS=""
    else
        DOCKER_UID_ARGS="-u $UID"
    fi
    if (groups | grep -E '(^| )docker($| )') >/dev/null; then
        DOCKER_CMD=docker
    else
        DOCKER_CMD="sudo docker"
    fi
fi

case $1 in
  run)
  shift
  exec $DOCKER_CMD run $DOCKER_UID_ARGS "$@"
  ;;
  *)
  exec $DOCKER_CMD "$@"
  ;;
esac
