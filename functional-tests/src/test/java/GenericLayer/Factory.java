package GenericLayer;

import java.io.IOException;
import java.util.Properties;

public class Factory {

    private static Properties properties = new Properties();
    private static String repository = "repository";
    private static String repositoryValue;

    public static searchRepository getDriver(){
       try{
        properties.load(ClassLoader.getSystemResourceAsStream("SampleConfigFile.properties"));
        }catch(IOException io){
        throw new RuntimeException(io);
    }
        repositoryValue = properties.getProperty(repository);
        if(repositoryValue.equalsIgnoreCase("WEB")){
            searchRepository searchRepository =new WebClass();
            return searchRepository;
        }else if (repositoryValue.equalsIgnoreCase("DB")){
            searchRepository searchRepository = new DatabaseClass();
            return searchRepository;
        }
        return null;
    }

}
