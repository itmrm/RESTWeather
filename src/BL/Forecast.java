package BL;

import com.sun.jersey.server.impl.model.parameter.multivalued.StringReaderFactory;

public class Forecast {
	
	private int day;
	private int month;
	private int year;
	private String text;
	private int max_temp;
	
	public Forecast() {
		text = "";
	}
	
	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public int getMax_temp() {
		return max_temp;
	}
	
	public void setMax_temp(int max_temp) {
		this.max_temp = max_temp;
	}
	
	public String toString() {
		return String.format("Date: %0$04d-%0$02d-%0$02d.", year, month, day)
				+ "\n" + text + "\n" + "Max_temp: " + max_temp + ".\n";
	}
	
}
