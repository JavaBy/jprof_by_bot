#!/usr/bin/env sh

set -x

aws --version
aws --endpoint-url "${DYNAMODB_URL}" dynamodb delete-table --table-name urbanWordsOfTheDay || true
aws --endpoint-url "${DYNAMODB_URL}" dynamodb create-table --cli-input-json file://english/urban-word-of-the-day/dynamodb/src/test/resources/urbanWordsOfTheDay.table.json
aws --endpoint-url "${DYNAMODB_URL}" dynamodb batch-write-item --request-items file://english/urban-word-of-the-day/dynamodb/src/test/resources/urbanWordsOfTheDay.items.json
