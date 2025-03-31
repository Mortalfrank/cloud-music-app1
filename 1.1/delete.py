import boto3

dynamoDB = boto3.client('dynamodb', 
                       region_name='us-east-1',
                       endpoint_url='http://localhost:8001') 
table_name = 'students'

# Scan the table to get all items
response = dynamoDB.scan(TableName=table_name)

# Loop through the items and delete each one
for item in response['Items']:
    email = item['email']['S']
    dynamoDB.delete_item(
        TableName=table_name,
        Key={'email': {'S': email}}
    )

    print(f"Deleted item with email: {email}")
