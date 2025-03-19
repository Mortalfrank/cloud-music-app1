import boto3
region_name = "us-east-1"
dynamoDB = boto3.client("dynamodb", region_name=region_name)
table_name = "music"

#delete if exists
try:
    dynamoDB.delete_table(TableName=table_name)
    print(f"Deleting table {table_name}... Please wait.")
    waiter = dynamoDB.get_waiter('table_not_exists')
    waiter.wait(TableName=table_name)
    print("Table deleted successfully.")
except dynamoDB.exceptions.ResourceNotFoundException:
    print("Table does not exist. Creating a new one.")


# Create the table
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
                    {"AttributeName": "year", "KeyType": "HASH"},  # Partition key for GSI
                    {"AttributeName": "album", "KeyType": "RANGE"},  # Sort key for GSI
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

except dynamoDB.exceptions.ResourceInUseException:
    print(f"Table '{table_name}' already exists.")


#TODO
#add one value to table with all required fields (including image url)
