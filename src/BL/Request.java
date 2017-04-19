package BL;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;

import javax.swing.text.DateFormatter;

public class Request {
	
	private final String KEY = "d30cb5dde5979690/";
	private final String SITE = "http://api.wunderground.com/api/";
	private final String[] FEATURES = {"forecast", "forecast10day","history_"};
	private final String TYPE = ".json";
	
	private String query;
	private int days;
	
	public Request(int days, String state, String city) {
		String feature = setDaysAndFeature(days);
		query = getValidAddress(feature, state, city);
	}
	
	public Request(int days, String zipcode) {
		String feature = setDaysAndFeature(days);
		query = getValidAddress(feature, zipcode);
	}
	
	// set feather that required to the sent to the UWeather site.
	private String setDaysAndFeature(int days) {
		if (days > 10)
			days = 10;
		this.days = days;
		String feature;
		if (days > 3) {
			feature = FEATURES[1];
		}
		else if (days <= 3 && days >= 0) {
			feature = FEATURES[0];
		}
		else {
			days = Math.abs(days);
			LocalDate today = LocalDate.now();
			feature = FEATURES[2]
					+ String.format("%04d" ,today.minusDays(days).getYear())
					+ String.format("%02d" ,today.minusDays(days).getMonthValue())
					+ String.format("%02d" ,today.minusDays(days).getDayOfMonth());
		}
		return feature;
	}
	
	// Create the address of the file in the UWeather site.
	private String getValidAddress(String feature, String state, String city) {
		return SITE + KEY + feature + "/q/" + state + "/" + city + TYPE;
	}
	
	// Create the address of the file in the UWeather site, by zipcode.
	private String getValidAddress(String feature, String zipcode) {
		return SITE + KEY + feature + "/q/" + zipcode + TYPE;
	}

	public String getQuery() {
		return query;
	}

	public int getDays() {
		return days;
	}
	
	
}
