#!/usr/bin/env sh

set -x

aws --version
aws --endpoint-url "${DYNAMODB_URL}" dynamodb delete-table --table-name languageRooms || true
aws --endpoint-url "${DYNAMODB_URL}" dynamodb create-table --cli-input-json file://english/language-rooms/dynamodb/src/test/resources/languageRooms.table.json
aws --endpoint-url "${DYNAMODB_URL}" dynamodb batch-write-item --request-items file://english/language-rooms/dynamodb/src/test/resources/languageRooms.items.json
