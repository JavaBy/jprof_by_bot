#!/usr/bin/env sh

set -x

aws --version
aws --endpoint-url "${DYNAMODB_URL}" dynamodb delete-table --table-name dialog-states || true
aws --endpoint-url "${DYNAMODB_URL}" dynamodb create-table --cli-input-json file://dialogs/dynamodb/src/test/resources/dialog-states.table.json
aws --endpoint-url "${DYNAMODB_URL}" dynamodb batch-write-item --request-items file://dialogs/dynamodb/src/test/resources/dialog-states.items.json
