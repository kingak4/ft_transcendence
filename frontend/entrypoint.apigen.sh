#!/bin/sh
# entrypoint.apigen.sh
set -e

echo "Waiting for backend API to be ready..."
while ! curl -s "${BACKEND_URL}/api-docs" > /dev/null; do
  sleep 1
done

echo "Generating API types..."
npm run generate:all:dev

echo "Copying generated types to shared volume..."
cp -r app/types/. /generated-types/