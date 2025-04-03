import json
import boto3

region_name = "us-east-1"
dynamoDB = boto3.resource("dynamodb", region_name=region_name, endpoint_url='http://localhost:8001')

table = dynamoDB.Table("music")

# Open and read the JSON file
with open("2025a1.json") as data:
    jsonobj = json.load(data)

songs = jsonobj.get("songs", [])

# Iterate through each song and insert it into the DynamoDB table
for song in songs:
    # Ensure 'year' is an integer if it's a number
    year = int(song["year"])

    # Print song data for debugging
    print(f"title: {song['title']}")
    print(f"artist: {song['artist']}")
    print(f"year: {year}")
    print(f"album: {song['album']}")
    print(f"img_url: {song['img_url']}")

    # Insert item into DynamoDB
    table.put_item(
        Item={
            'title': song["title"],
            'artist': song["artist"],
            'year': year,  # Ensure year is an integer
            'album': song["album"],
            'img_url': song["img_url"]
        },
    )
    print(f"Inserted {song['title']} by {song['artist']} into the table.")
