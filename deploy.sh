#!/usr/bin/env bash

source .env &&
./gradlew clean shadowJar &&
pushd .deploy/lambda &&
npm install &&
cdk deploy --outputs-file=cdk.out/outputs.json --require-approval=never &&
popd
