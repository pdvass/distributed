package distributed;
/* The Worker class mainly needed for keeping information about the Room objects which
 * correspond to each worker.
 * 
 * @author panagou
 * @see Room
 */

public class Worker {

    private String name;
    private boolean isAlive;
    private Map<Integer, Room> rooms = new HashMap<Integer, Room>();

    public Worker(String workerName) {
        this.name = workerName;
        this.isAlive = true;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Room getRoom(int roomId) {
        return rooms.get(roomId);
    }

    public void addRoom(int roomId, Room room) {
        rooms.put(roomId, room);
    }

    public void removeRoom(int roomId) {
        rooms.remove(roomId);
    }

    public boolean hasRoom(int roomId) {
        return (this.rooms).containsKey(roomId);
    }

    public Map<Integer, Room> returnRooms() {
        return this.rooms;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public void sendData(Object data) {
        System.out.println(name + " is sending data: " + data);
    }

}
