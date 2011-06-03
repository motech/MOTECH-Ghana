package util;

import java.io.IOException;
import java.util.Properties;

public class TestConfiguration {

    private static Properties properties = new Properties();

    private static TestConfiguration instance = new TestConfiguration();
    private String  MIDLET_LOCATION = "midlet.location";
    private String ERROR_LOG_DIRECTORY = "logFile.location";

    public static TestConfiguration instance(){
        return instance;
    }

    private TestConfiguration(){
        try{
            properties.load( ClassLoader.getSystemResourceAsStream("functional-test.properties"));
        }catch(IOException io){
            throw new RuntimeException(io);
        }
    }

    public String value(String key){
        return properties.getProperty(key);
    }

    public String midletLocation() {
        return value(MIDLET_LOCATION);
    }

    public String errorLogLocation(){
        return value(ERROR_LOG_DIRECTORY);
    }


}
