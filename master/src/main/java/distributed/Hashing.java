package distributed;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {

    private String key;

    public Hashing(String key){
        this.key = key;
    }

    public String hash(){

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(key.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();

            for (byte hashByte : hashBytes){

                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1){
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }
}