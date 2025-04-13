import boto3

dynamoDB = boto3.resource('dynamodb') 

table = dynamoDB.Table('login')
passw = '0123456789'
for i in range(0, 10):
    table.put_item(
        Item={
            'email': 's3945643' + str(i) + '@student.rmit.edu.au',
            'user_name': 'ChristopherLamb' + str(i),
            'password': passw[i:i+6] if i + 6 <= len(passw) else passw[i:] + passw[:(i+6) % len(passw)]
        }
    )
    print(f"Added student {i}")