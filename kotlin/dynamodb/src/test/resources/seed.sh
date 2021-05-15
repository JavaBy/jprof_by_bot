#!/usr/bin/env sh

set -x

aws --version
aws --endpoint-url "${DYNAMODB_URL}" dynamodb delete-table --table-name kotlin-mentions || true
aws --endpoint-url "${DYNAMODB_URL}" dynamodb create-table --cli-input-json file://kotlin/dynamodb/src/test/resources/kotlin-mentions.table.json
aws --endpoint-url "${DYNAMODB_URL}" dynamodb batch-write-item --request-items file://kotlin/dynamodb/src/test/resources/kotlin-mentions.items.json
