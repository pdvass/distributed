package distributed;

import distributed.Estate.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author panagou
*/

@TestMethodOrder(OrderAnnotation.class)
public class BookkeeperTest {

    @Test
    @Order(1)
    public void testCreateWorkers() {
        Bookkeeper bookkeeper = new Bookkeeper();
        bookkeeper.createWorkers();
        Map<String, Worker> workers = bookkeeper.getWorkers();

        assertEquals(3, workers.size()); 
        assertTrue(workers.containsKey("Worker 1"));
        assertTrue(workers.containsKey("Worker 2"));
        assertTrue(workers.containsKey("Worker 3"));
    }

    @Test
    @Order(2)
    public void testSendData() {
        Bookkeeper bookkeeper = new Bookkeeper();
        Worker worker = new Worker("Worker 1");
        bookkeeper.addWorker("Worker 1", worker);

        bookkeeper.sendData(worker);

    }

    @Test
    @Order(3)
    public void testDistributingRooms() {
        Bookkeeper bookkeeper = new Bookkeeper();

        Room room1 = new Room("Room 1", "01/01/2024", "05/01/2024");
        Room room2 = new Room("Room 2", "06/01/2024", "10/01/2024");
        Room room3 = new Room("Room 3", "11/01/2024", "15/01/2024");

        bookkeeper.addRoom("Worker 1", 1, room1);
        bookkeeper.addRoom("Worker 1", 2, room2);
        bookkeeper.addRoom("Worker 1", 3, room3);

        bookkeeper.addRoom("Worker 2", 4, room1);
        bookkeeper.addRoom("Worker 2", 5, room2);
        bookkeeper.addRoom("Worker 2", 6, room3);

        bookkeeper.addRoom("Worker 3", 7, room1);
        bookkeeper.addRoom("Worker 3", 8, room2);
        bookkeeper.addRoom("Worker 3", 9, room3);

        Worker lostWorker = bookkeeper.getWorker("Worker 1");
        bookkeeper.distributingRooms(lostWorker);

        Map<String, Worker> workers = bookkeeper.getWorkers();
        assertEquals(3, workers.size());

        Worker worker2 = bookkeeper.getWorker("Worker 2");
        assertTrue(worker2.hasRoom(1)); 
        assertTrue(worker2.hasRoom(2)); 
        assertTrue(worker2.hasRoom(3)); 

        Worker worker3 = bookkeeper.getWorker("Worker 3");
        assertTrue(worker3.hasRoom(4)); 
        assertTrue(worker3.hasRoom(5)); 
        assertTrue(worker3.hasRoom(6)); 

        // assertFalse(bookkeeper.getWorker("Worker 1").hasRooms());
    }
}