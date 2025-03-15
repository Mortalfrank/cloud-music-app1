import boto3
region_name = "us-east-1"
dynamoDB = boto3.client('dynamodb', region_name=region_name)
table_name = "students"

try:
    dynamoDB.delete_table(TableName=table_name)
    print(f"Deleting table {table_name}... Please wait.")
    waiter = dynamoDB.get_waiter('table_not_exists')
    waiter.wait(TableName=table_name)
    print("Table deleted successfully.")
except dynamoDB.exceptions.ResourceNotFoundException:
    print("Table does not exist. Creating a new one.")


table = dynamoDB.create_table(
    TableName = 'students',
    KeySchema=[
        {
            'AttributeName': 'email',
            'KeyType': 'HASH' #partition key
        },
    ],
    AttributeDefinitions=[
        {
            'AttributeName': 'email',
            'AttributeType': 'S',  # Email attribute for partition key
        },
    ],
    ProvisionedThroughput ={
        'ReadCapacityUnits': 1,
        'WriteCapacityUnits': 1
    },
)
print(f"Table status: {table['TableDescription']['TableStatus']}")