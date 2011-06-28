package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: rupeshd
 * Date: 2/17/11
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class UtilityClass {

    private static UtilityClass instance = new UtilityClass();

    public static UtilityClass getInstance(){
        return instance;
    }
    public String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String date = sdf.format(Calendar.getInstance().getTime());
        return date;
    }

   public void wait(int time) {
       try {
           Thread.sleep(time);
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
   }
}
