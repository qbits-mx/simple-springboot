package mx.qbits.publisher.access.api.utils;

import java.security.SecureRandom;
import org.mindrot.jbcrypt.BCrypt;
import mx.qbits.publisher.access.api.model.domain.Response;

public class Generator {
    public static String genHashed(String clearPass) {
        return BCrypt.hashpw(clearPass, BCrypt.gensalt());
    }
    
    public static Response generaClave() {
        Response response = new Response();
        
        String gen = getRandomString(8);
        String hashed = genHashed(gen);
        long ts = System.currentTimeMillis()/1000;
        long olderThan = ts - 604800;
        
        /*
        to be stored in the 'password' field if 
        map.get("timestamp") is bigger than 'lastaccess' 
        field by 60*60*24*7 = 604800 seconds or more and
        'username' is like studentXX
        */
        response.setHashed(hashed);
        response.setOlderThan(olderThan+"");
        response.setGen(gen);
        return response;
    }
    
    public static String getRandomString(int len) {
        char[] result = new char[len];
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        for(int i =0; i<len; i++) {
            SecureRandom random = new SecureRandom();
            int num = random.nextInt(base.length());
            result[i] = base.charAt(num);
        }
        return new String(result);
    }
}
