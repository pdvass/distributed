# JSON Structure

Since Comments are not allowed in JSON, we will decribe its structure here:

```json
{
    // Hotel is identified by name. The name of
    // the file is the same as the hotel's.
    "Pergamos": {
        "name": "Pergamos",
        // Rooms are described by an array so that
        // it can be parsed easier by the library.
        "rooms":[ 
            {
                // Actual name of the room
                "room1": {
                    // This is going to be hashed.
                    // It is the name of the hotel and the actual name of the room concatenated
                    "id": "PergamosRoom1",
                    // Date range of the room's availability
                    // It is not impacted by bookings
                    // Each room has its own DateRange even if it is the same for all
                    "startDate": "11/04/2024",
                    "endDate": "30/04/2024"
                }
            }
        ],
        "region": "Metaksourgio"
    }
}
```
