#!/usr/bin/env sh

set -x

aws --version
aws --endpoint-url "${DYNAMODB_URL}" dynamodb delete-table --table-name monies || true
aws --endpoint-url "${DYNAMODB_URL}" dynamodb create-table --cli-input-json file://monies/dynamodb/src/test/resources/monies.table.json
aws --endpoint-url "${DYNAMODB_URL}" dynamodb batch-write-item --request-items file://monies/dynamodb/src/test/resources/monies.items.json
