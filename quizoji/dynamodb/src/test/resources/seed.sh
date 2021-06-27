#!/usr/bin/env sh

set -x

aws --version
aws --endpoint-url "${DYNAMODB_URL}" dynamodb delete-table --table-name quizoji || true
aws --endpoint-url "${DYNAMODB_URL}" dynamodb create-table --cli-input-json file://quizoji/dynamodb/src/test/resources/quizoji.table.json
aws --endpoint-url "${DYNAMODB_URL}" dynamodb batch-write-item --request-items file://quizoji/dynamodb/src/test/resources/quizoji.items.json
