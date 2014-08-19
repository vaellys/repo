package org.mas.codehaus.finder.common;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateUtils {

	public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat SHORT_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	public static String format(Timestamp timestamp) {
		return DATE_TIME_FORMAT.format(timestamp);
	}

	public static String format(Date date) {
		return DATE_FORMAT.format(date);
	}

	public static String format(java.util.Date timestamp) {
		return DATE_TIME_FORMAT.format(timestamp);
	}

	public static String format(Time time) {
		return TIME_FORMAT.format(time);
	}

	public static Timestamp parseTimestamp(String timestamp) throws ParseException {
		try {
			return new Timestamp(DATE_TIME_FORMAT.parse(timestamp).getTime());
		} catch (ParseException e) {
			return new Timestamp(SHORT_DATE_TIME_FORMAT.parse(timestamp).getTime());
		}
	}

	public static Date parseDate(String timestamp) throws ParseException {
		return new Date(DATE_FORMAT.parse(timestamp).getTime());
	}

	public static void main(String[] args) throws ParseException {
		System.out.println(parseTimestamp("1999-1-2 23:12"));
	}
}
