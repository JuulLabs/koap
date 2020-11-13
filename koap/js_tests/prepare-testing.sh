#!/bin/bash

cd ../..

./gradlew -Pnpm.publish.repository.github.authToken=fakingit -Pnpm.publish.version=0.0.3-test24 assemble

cd -

npm uninstall @juullabs/koap

npm install file://../build/publications/npm/js

npm run test