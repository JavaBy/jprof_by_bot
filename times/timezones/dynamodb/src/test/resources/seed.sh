#!/usr/bin/env sh

set -x

aws --version
aws --endpoint-url "${DYNAMODB_URL}" dynamodb delete-table --table-name timezones || true
aws --endpoint-url "${DYNAMODB_URL}" dynamodb create-table --cli-input-json file://times/timezones/dynamodb/src/test/resources/timezones.table.json
aws --endpoint-url "${DYNAMODB_URL}" dynamodb batch-write-item --request-items file://times/timezones/dynamodb/src/test/resources/timezones.items.json
