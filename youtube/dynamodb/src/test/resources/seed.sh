#!/usr/bin/env sh

set -x

aws --version
aws --endpoint-url "${DYNAMODB_URL}" dynamodb delete-table --table-name youtube-channels-whitelist || true
aws --endpoint-url "${DYNAMODB_URL}" dynamodb create-table --cli-input-json file://youtube/dynamodb/src/test/resources/youtube-channels-whitelist.table.json
aws --endpoint-url "${DYNAMODB_URL}" dynamodb batch-write-item --request-items file://youtube/dynamodb/src/test/resources/youtube-channels-whitelist.items.json
