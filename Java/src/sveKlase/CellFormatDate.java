package sveKlase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CellFormatDate extends CellFormat {

	private boolean isLeapYear(int year)
	{
		return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
	}
	private boolean isValidDate(int day, int month, int year)
	{
		if (year < 0 || month < 1 || month > 12)//valud values
	        return false;

	    int daysInMonth[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };//valid number of days in each month 
	    int maxDay = daysInMonth[month - 1];

	    if (month == 2 && isLeapYear(year))//if its leap year february has 29 days
	        maxDay = 29;

	    return (day >= 1 && day <= maxDay);
	}
	@Override
	public boolean appropriate(String value) {
		Pattern pattern = Pattern.compile("^(\\d{2})\\.(\\d{2})\\.(\\d{4})\\.$");
		Matcher matcher = pattern.matcher(value);

		if (matcher.find()) {
		    int day = Integer.parseInt(matcher.group(1));
		    int month = Integer.parseInt(matcher.group(2));
		    int year = Integer.parseInt(matcher.group(3));
		    return isValidDate(day, month, year);
		}
		
		return false;
	}

	@Override
	public String getFormat() {
		return "date";
	}

	@Override
	public String display(String value) {
		if (value.isEmpty() || appropriate(value)) return value;
		return "ERROR";//greska
	}

}
