package distributed.JSONFileSystem;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;



@TestMethodOrder(OrderAnnotation.class)
public class JSONDirManagerTest {
    private final String path = "src/main/java/distributed/data/";

    @Test
    @Order(1)
    public void testCreateHotel(){
        JSONDirManager manager = new JSONDirManager();
        manager.addHotel("Test Hotel", this.path);
        File f = new File(this.path + "TestHotel.json");
        assertTrue(f.exists());
        assertTrue(f.isFile());;
    }
    
}
