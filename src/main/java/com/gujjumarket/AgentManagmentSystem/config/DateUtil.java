package com.gujjumarket.AgentManagmentSystem.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	 public static Date stringToDate(String dateString, String format) throws ParseException {
	        SimpleDateFormat formatter = new SimpleDateFormat(format);
	        return formatter.parse(dateString);
	    }
	 public static String dateToString(Date date, String format) throws ParseException {
	        SimpleDateFormat formatter = new SimpleDateFormat(format);
	        return formatter.format(date);
	    }
}
