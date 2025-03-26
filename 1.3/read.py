import json
import boto3
region_name = "us-east-1"
dynamoDB = boto3.client("dynamodb", region_name=region_name)
table = "music"
with open("1.3/2025a1.json") as data:
    jsonobj = json.load(data)
songs = jsonobj.get("songs", [])
for song in songs:
    item = ""
    for key, value in song.items():
        item +=(f"{key}: {value}")+"\n"
    print(item)
    print(song["title"])
    table.put_item(
        Item = {
            'title': song["title"],
            'artist': song["artist"],
            'year': song["year"],
            'album': song["album"],
            'img_url': song["img_url"]
        },
    )