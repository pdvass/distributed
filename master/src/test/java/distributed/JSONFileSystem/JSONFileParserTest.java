<<<<<<< HEAD:master/src/test/java/distributed/Parser/JSONFileParserTest.java
// package distributed.Parser;
=======
package distributed.JSONFileSystem;
>>>>>>> 93ee1b50459f269eba4ff2aac4eb61416dca33b9:master/src/test/java/distributed/JSONFileSystem/JSONFileParserTest.java

// import static org.junit.Assert.assertTrue;
// import static org.junit.Assert.assertThrows;
// import static org.junit.Assert.fail;

// import java.io.FileNotFoundException;

// import org.json.simple.JSONObject;
// import org.json.simple.parser.JSONParser;
// import org.json.simple.parser.ParseException;
// import org.junit.Test;

// public class JSONFileParserTest {

<<<<<<< HEAD:master/src/test/java/distributed/Parser/JSONFileParserTest.java
//     @Test
//     public void testParseValidity(){
//         // Contents of dummy data file in a String.
//         String rightAnswer = """
//             {
//                 "hotels": {
//                     "Pergamos": {
//                         "name": "Pergamos",
//                         "rooms": {
//                             "room1": {
//                                 "id": "PergamosRoom1",
//                                 "startDate": "11/04/2024",
//                                 "endDate": "30/04/2024"
//                             }
//                         },
//                         "region": "Metaksourgio"
//                     }
//                 }
//             }
//                 """;
        
//         JSONFileParser parser = new JSONFileParser("src/main/java/distributed/data/dummyData.json");
//         JSONObject data = null;
//         try{
//             data = parser.parseFile();
//         } catch (Exception e){
//             fail("Exception should not be thrown while reading the file.");
//         }
=======
    private final String path = "src/main/java/distributed/data/";

    @Test
    public void testParseValidity(){
        // Contents of dummy data file in a String.
        String rightAnswer = """
            {
                "Pergamos": {
                    "name": "Pergamos",
                    "rooms": {
                        "room1": {
                            "id": "PergamosRoom1",
                            "startDate": "11/04/2024",
                            "endDate": "30/04/2024"
                        }
                    },
                    "region": "Metaksourgio"
                }
            }
                """;
        
        JSONFileParser parser = new JSONFileParser(this.path + "dummyData.json");
        JSONObject data = null;
        try{
            data = parser.parseFile();
        } catch (Exception e){
            fail("Exception should not be thrown while reading the file.");
        }
>>>>>>> 93ee1b50459f269eba4ff2aac4eb61416dca33b9:master/src/test/java/distributed/JSONFileSystem/JSONFileParserTest.java

//         // Parsing the String into the same format as the files contents.
//         JSONParser JSONparser = new JSONParser();
//         JSONObject rightAnswerJSON = null;

//         try {
//             rightAnswerJSON = (JSONObject) JSONparser.parse(rightAnswer);
//         } catch (ParseException e) {
//             System.out.println(e.getMessage());
//         }

//         // Converting both the right answer and the data of the file
//         // into the same format Strings
//         String dataString = data.toString();
//         String rightAnswerString = rightAnswerJSON.toString();

//         assertTrue(dataString.equals(rightAnswerString));
//     }

<<<<<<< HEAD:master/src/test/java/distributed/Parser/JSONFileParserTest.java
//     @Test
//     public void testExceptionFromParseFile(){
//         JSONFileParser parser = new JSONFileParser("dummyData.json");
//         assertThrows(FileNotFoundException.class, () -> parser.parseFile());
//     }
=======
    @Test
    public void testExceptionFromParseFile(){
        JSONFileParser parser = new JSONFileParser("nonExistentData.json");
        assertThrows(FileNotFoundException.class, () -> parser.parseFile());
    }
>>>>>>> 93ee1b50459f269eba4ff2aac4eb61416dca33b9:master/src/test/java/distributed/JSONFileSystem/JSONFileParserTest.java
    
// }
