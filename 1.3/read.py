import json
with open("1.3/2025a1.json") as data:
    jsonobj = json.load(data)
songs = jsonobj.get("songs", [])
for song in songs:
    for key, value in song.items():
        print(f"{key}: {value}")