#!/bin/bash
awslocal dynamodb create-table \
  --table-name dynamo_db_session_data \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST
