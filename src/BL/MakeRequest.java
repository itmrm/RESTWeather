package BL;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MakeRequest {
	
	// Download the data from the UWeather site.  
	public String getResult(String address) throws MalformedURLException {
		try {
			URL url = new URL(address);
			InputStream in = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while (-1!=(n=in.read(buf))) {
			    out.write(buf, 0, n); 
			}
			out.close();
			in.close();
			
			return out.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "Error.";
		}
	}
	
	// Parse the files of history requests. (weather from before some days)
	private Forecast[] historytoNormalForm(JSONObject json) throws ParseException {
		Forecast[] forecasts = new Forecast[1];
		json = (JSONObject) json.get("history");
		JSONArray jsonArr = (JSONArray) json.get("dailysummary");
		json = (JSONObject) jsonArr.get(0);
		forecasts[0] = new Forecast();
		forecasts[0] = setHistoryDate(forecasts[0],(JSONObject) json.get("date"));
		forecasts[0].setMax_temp(Integer.parseInt(json.get("maxtempm").toString()));
		return forecasts;
	}
	
	// Parse the files.
	public Forecast[] toNormalForm(String jsonString, int days) {
		JSONParser parser = new JSONParser();
		Forecast[] forecasts = null;
		try {
			Object obj = parser.parse(jsonString);
			JSONObject jObj = (JSONObject) obj;
			if(days < 0)
				return historytoNormalForm(jObj);
			forecasts = new Forecast[days];
			jObj = (JSONObject) jObj.get("forecast");
			JSONObject jObjTxt = (JSONObject) jObj.get("txt_forecast");
			JSONObject jObjSmpl = (JSONObject) jObj.get("simpleforecast");
			JSONArray jArrTxt = (JSONArray) jObjTxt.get("forecastday");
			JSONArray jArrSmpl = (JSONArray) jObjSmpl.get("forecastday");
			
			int j = 0;
			for (int i = 0; i < days; i++) {
				forecasts[i] = new Forecast();
				jObj = (JSONObject) jArrSmpl.get(i);
				forecasts[i] = setDate(forecasts[i], (JSONObject) jObj.get("date"));
				forecasts[i] = setMaxTemp(forecasts[i], (JSONObject) jObj.get("high"));
				JSONObject jTxt1 = (JSONObject) jArrTxt.get(j);
				JSONObject jTxt2 = (JSONObject) jArrTxt.get(j+1);
				forecasts[i] = setText(forecasts[i], jTxt1 , jTxt2);
				j += 2;
			}
			
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (ArrayIndexOutOfBoundsException e1) {
			System.out.println(e1.getMessage());
			return null;
		}
		return forecasts;
	}
	
	// Parse the Date JSONObject and put the relevant data in the Forecast object.
	private Forecast setDate(Forecast f, JSONObject jsonOb) {
		f.setDay(Integer.parseInt(jsonOb.get("day").toString()));
		f.setMonth(Integer.parseInt(jsonOb.get("month").toString()));
		f.setYear(Integer.parseInt(jsonOb.get("year").toString()));
		return f;
	}
	
	// For history weather.
	// Parse the Date JSONObject and put the relevant data in the Forecast object.
	private Forecast setHistoryDate(Forecast f, JSONObject jsonOb) {
		f.setDay(Integer.parseInt(jsonOb.get("mday").toString()));
		f.setMonth(Integer.parseInt(jsonOb.get("mon").toString()));
		f.setYear(Integer.parseInt(jsonOb.get("year").toString()));
		return f;
	}
	
	// Parse the JSONObject and put the max_temp data in the Forecast object.
	private Forecast setMaxTemp(Forecast f, JSONObject jsonOb) {
		f.setMax_temp(Integer.parseInt(jsonOb.get("celsius").toString()));
		return f;
	}
	
	// Parse the JSONObject and put the text data in the Forecast object.
	private Forecast setText(Forecast f, JSONObject json1, JSONObject json2) {
		String text =  "Day: " + json1.get("fcttext_metric").toString() 
				+ "\nNight: " + json2.get("fcttext_metric").toString();
		f.setText(text);
		return f;
	}
	

}
