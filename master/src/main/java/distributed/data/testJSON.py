import json
from pprint import pprint

if __name__ == "__main__":
    with open("HotelVictory.json", "r") as f:
        data = json.load(f)
        pprint(data)