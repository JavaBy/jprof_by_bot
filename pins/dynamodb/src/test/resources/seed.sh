#!/usr/bin/env sh

set -x

aws --version
aws --endpoint-url "${DYNAMODB_URL}" dynamodb delete-table --table-name pins || true
aws --endpoint-url "${DYNAMODB_URL}" dynamodb create-table --cli-input-json file://pins/dynamodb/src/test/resources/pins.table.json
aws --endpoint-url "${DYNAMODB_URL}" dynamodb batch-write-item --request-items file://pins/dynamodb/src/test/resources/pins.items.json
