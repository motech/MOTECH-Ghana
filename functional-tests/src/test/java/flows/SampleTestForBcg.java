package flows;

import org.sikuli.script.*;
import org.testng.annotations.Test;
import util.TestConfiguration;

/**
 * Created by IntelliJ IDEA.
 * User: rupeshd
 * Date: 3/7/11
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class SampleTestForBcg {

    @Test
    public void SampleTest(){
        Screen screen = new Screen();
        try {
            Match trusteeName = screen.find("resources/expected_img/bcg_newAlex.png");
            trusteeName.highlight();
            System.out.println(trusteeName);
            Region trusteeRow = trusteeName.left();

            //System.out.println(trusteeRow);
            //System.out.println(trusteeRow.text());
            Match TRegion = trusteeRow.find("resources/expected_img/bcg_drop.png");
            //TRegion.highlight();
            //System.out.println(TRegion.text());
            trusteeRow.click("resources/expected_img/bcg_drop.png",0);
            //trusteeRow.click("resources/expected_img/bcg_drop.png", 0);
            Match textBox = screen.find("resources/expected_img/bcg_TextBox.png");
            textBox.highlight();
            textBox.type("resources/expected_img/bcg_TextBox.png", "abcd", 0);
            textBox.click("resources/expected_img/bcg_OK.png", 0);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            if(trusteeRow.exists("resources/expected_img/btn_T_img.png")!= null)
                System.out.println("Img found");
            else
                System.out.println("Img Not Found");
            trusteeRow.find("resources/expected_img/btn_T_img.png").highlight();

        } catch (FindFailed e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    };

}
