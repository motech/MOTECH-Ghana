package GenericLayer;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TempSearchPatientTest {

    public searchRepository searchRepository;

    @BeforeClass
    public void setUp(){
     searchRepository = Factory.getDriver();
    }

    @Test
    public void searchPatient() {
        Assert.assertTrue(searchRepository.doesPatientExist("1234599"));
    }
}
