package com.pennant.externalinput.service;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	Calendar calendar1 = Calendar.getInstance();
    int currentDayOfYear = calendar1.get(Calendar.DAY_OF_YEAR);

    int year = calendar1.get(Calendar.YEAR);

    Calendar calendar2 = new GregorianCalendar(year, 11, 31);
    int dayDecember31 = calendar2.get(Calendar.DAY_OF_YEAR);

    int days = dayDecember31 - currentDayOfYear;
    System.out.println(days + " days remain in current year");}

}
