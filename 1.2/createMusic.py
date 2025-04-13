import boto3
from botocore.exceptions import ClientError


dynamoDB = boto3.client(
    'dynamodb',
    region_name='us-east-1',
    endpoint_url='http://localhost:8000',
    aws_access_key_id='fake',
    aws_secret_access_key='fake'
)

dynamoDB_resource = boto3.resource(
    'dynamodb',
    region_name='us-east-1',
    endpoint_url='http://localhost:8000',
    aws_access_key_id='fake',
    aws_secret_access_key='fake'
)

table_name = "music"


try:
    dynamoDB.delete_table(TableName=table_name)
    print(f"Deleting table {table_name}... Please wait.")
    waiter = dynamoDB.get_waiter('table_not_exists')
    waiter.wait(TableName=table_name)
    print("Table deleted successfully.")
except ClientError as e:
    if e.response['Error']['Code'] == 'ResourceNotFoundException':
        print("Table does not exist. Creating a new one.")
    else:
        print(f"Error deleting table: {e}")


try:
    response = dynamoDB.create_table(
        TableName=table_name,
        KeySchema=[
            {"AttributeName": "title", "KeyType": "HASH"},  # Partition key
            {"AttributeName": "artist", "KeyType": "RANGE"},  # Sort key
        ],
        AttributeDefinitions=[
            {"AttributeName": "title", "AttributeType": "S"},
            {"AttributeName": "artist", "AttributeType": "S"},
            {"AttributeName": "year", "AttributeType": "N"},
            {"AttributeName": "album", "AttributeType": "S"},
        ],
        ProvisionedThroughput={
            "ReadCapacityUnits": 1,
            "WriteCapacityUnits": 1
        },
        GlobalSecondaryIndexes=[
            {
                "IndexName": "YearAlbumIndex",
                "KeySchema": [
                    {"AttributeName": "year", "KeyType": "HASH"},
                    {"AttributeName": "album", "KeyType": "RANGE"},
                ],
                "Projection": {
                    "ProjectionType": "ALL"
                },
                "ProvisionedThroughput": {
                    "ReadCapacityUnits": 1,
                    "WriteCapacityUnits": 1
                }
            }
        ]
    )

    print("Creating table... Please wait.")
    waiter = dynamoDB.get_waiter("table_exists")
    waiter.wait(TableName=table_name)
    print(f"Table status: {response['TableDescription']['TableStatus']}")

except ClientError as e:
    print(f"Error creating table: {e}")