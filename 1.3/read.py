import json
import boto3
from botocore.exceptions import ClientError


dynamoDB = boto3.resource(
    'dynamodb',
    region_name='us-east-1',
    endpoint_url='http://localhost:8000',
    aws_access_key_id='fake',
    aws_secret_access_key='fake'
)

table = dynamoDB.Table("music")


try:
    with open("2025a1.json") as data:
        jsonobj = json.load(data)
except FileNotFoundError:
    print("Error: File '2025a1.json' not found.")
    exit(1)

songs = jsonobj.get("songs", [])

for song in songs:
    try:

        year = int(song["year"])
        table.put_item(
            Item={
                'title': song["title"],
                'artist': song["artist"],
                'year': year,
                'album': song["album"],
                'img_url': song["img_url"]
            }
        )
        print(f"Inserted: {song['title']} by {song['artist']}")
    except ClientError as e:
        print(f"AWS Error inserting {song['title']}: {e}")
    except Exception as e:
        print(f"General Error inserting {song['title']}: {e}")