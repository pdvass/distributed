// package distributed.JSONFileSystem;

// import static org.junit.Assert.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.io.File;
// import java.io.FileNotFoundException;
// import java.util.Scanner;

// import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.Order;
// import org.junit.jupiter.api.TestMethodOrder;


// /**
//  * @author pdvass
//  */
// @TestMethodOrder(OrderAnnotation.class)
// public class JSONDirManagerTest {
//     private final String path = "src/main/java/distributed/data/";

//     @Test
//     @Order(1)
//     public void testCreateHotel(){
//         JSONDirManager manager = new JSONDirManager();
//         manager.addHotel("Test Hotel", "Athens", 5.0f, 143);
//         File f = new File(this.path + "TestHotel.json");
//         assertTrue(f.exists());
//         assertTrue(f.isFile());
//     }
    
//     @Test
//     @Order(2)
//     public void testHotelContents() throws FileNotFoundException{
//         final String expected = "{\"TestHotel\":{\"rooms\":[],\"name\":\"TestHotel\",\"stars\":5.0,\"nOfReviews\":143,\"region\":\"Athens\"}}";
//         File f = new File(this.path + "TestHotel.json");
//         Scanner reader = new Scanner(f);
//         String data = reader.nextLine();
//         assertEquals(expected, data);
//         reader.close();
//     }

//     @Test
//     @Order(3)
//     public void testAddRoom() throws FileNotFoundException{
//         final String expected = "{\"TestHotel\":{\"rooms\":[{\"endDate\":\"30\\/04\\/2024\"," +
//                     "\"id\":\"TestHotelRoom1\",\"startDate\":\"21\\/04\\/2024\"}],\"name\":\"TestHotel\",\"stars\":5.0,\"nOfReviews\":143,\"region\":\"Athens\"}}";
//         JSONDirManager manager = new JSONDirManager();
//         manager.addRoom("Test Hotel", "21/04/2024", "30/04/2024");
//         File f = new File(this.path + "TestHotel.json");
//         Scanner reader = new Scanner(f);
//         String data = reader.nextLine();
//         assertEquals(expected, data);
//         reader.close();
//     }

//     @Test
//     @Order(4)
//     public void testRemoveRoom() throws FileNotFoundException {
//         JSONDirManager manager = new JSONDirManager();
//         manager.removeRoom("Test Hotel", 1);
//         this.testHotelContents();
//     }

//     @Test
//     @Order(5)
//     public void testAddMultipleRooms() throws FileNotFoundException {
//         final String expected = "{\"TestHotel\":{\"rooms\":[{\"endDate\":\"30\\/04\\/2024\","+
//                     "\"id\":\"TestHotelRoom1\",\"startDate\":\"21\\/04\\/2024\"}"+
//                     ",{\"endDate\":\"07\\/05\\/2024\",\"id\":\"TestHotelRoom2\",\"startDate\":\"05\\/05\\/2024\"}]"+
//                     ",\"name\":\"TestHotel\",\"stars\":5.0,\"nOfReviews\":143,\"region\":\"Athens\"}}";
//         JSONDirManager manager = new JSONDirManager();
//         manager.addRoom("Test Hotel", "21/04/2024", "30/04/2024");
//         manager.addRoom("Test Hotel", "05/05/2024", "07/05/2024");
//         File f = new File(this.path + "TestHotel.json");
//         Scanner reader = new Scanner(f);
//         String data = reader.nextLine();
//         reader.close();
//         assertEquals(expected, data);
//     }

//     @Test
//     @Order(6)
//     public void testRemoveMultipleRooms() throws FileNotFoundException{
//         final String expected = "{\"TestHotel\":{\"rooms\":[{\"endDate\":\"07\\/05\\/2024\","+
//                 "\"id\":\"TestHotelRoom2\",\"startDate\":\"05\\/05\\/2024\"}],\"name\":\"TestHotel\",\"stars\":5.0,\"nOfReviews\":143,\"region\":\"Athens\"}}";
//         JSONDirManager manager = new JSONDirManager();
//         manager.removeRoom("Test Hotel", 1);
//         File f = new File(this.path + "TestHotel.json");
//         Scanner reader = new Scanner(f);
//         String data = reader.nextLine();
//         reader.close();
//         assertEquals(expected, data);
//         manager.removeRoom("Test Hotel", 2);
//         this.testHotelContents();
//     }

    // @Test
    // @Order(7)
    // public void testRemoveHotel(){
    //     JSONDirManager manager = new JSONDirManager();
    //     manager.removeHotel("Test Hotel");
    //     File f = new File(this.path + "TestHotel.json");
    //     assertFalse(f.exists());
    // }
// }
