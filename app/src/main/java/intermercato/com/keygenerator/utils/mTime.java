package intermercato.com.keygenerator.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class mTime {

    public static String getCurrentTime(String tf){
        DateFormat dateFormat = new SimpleDateFormat(tf);
        Calendar cal = Calendar.getInstance();
        return  dateFormat.format(cal.getTime());
    }
}
