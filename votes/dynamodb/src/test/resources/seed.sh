#!/usr/bin/env sh

set -x

aws --version
aws --endpoint-url "${DYNAMODB_URL}" dynamodb delete-table --table-name votes || true
aws --endpoint-url "${DYNAMODB_URL}" dynamodb create-table --cli-input-json file://votes/dynamodb/src/test/resources/votes.table.json
aws --endpoint-url "${DYNAMODB_URL}" dynamodb batch-write-item --request-items file://votes/dynamodb/src/test/resources/votes.items.json
