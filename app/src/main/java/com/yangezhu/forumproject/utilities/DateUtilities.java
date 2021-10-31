package com.yangezhu.forumproject.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtilities {
    public static String getCurrentTime(){
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String current_time = formatter.format(date);
        return current_time;
    }
}
