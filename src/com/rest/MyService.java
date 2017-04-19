package com.rest;

import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import BL.Forecast;
import BL.Request;
import DAL.DatabaseConnector;
import DAL.Query;
import BL.MakeRequest;

@Path("/my-service")
public class MyService {
	
	private MakeRequest setRequest;
	private DatabaseConnector dbConnector;
	
	public MyService() {
		setRequest = new MakeRequest();
		dbConnector = DatabaseConnector.getInstance();
	}
	
	// Getting the user request and sort it to the right method.
	@GET
	@QueryParam("/{action}")
	@Produces("text/plain")
	public Response gate(@QueryParam("action") String action,
			@DefaultValue("0") @QueryParam("days") int days,
			@DefaultValue("") @QueryParam("state") String state,
			@DefaultValue("") @QueryParam("city") String city,
			@DefaultValue("")@QueryParam("zipcode") String zipcode) {
		System.out.println(action);
		if (action.equals("getForecast"))
			return getForecast(days, state, city, zipcode);
		else if (action.equals("Run"))
			return Run();
		return null;
	}
	
	// The Run function.
	// Ask for the request that save in the database, and retrieve the info to the user.
	public Response Run() {
		StringBuffer sb = new StringBuffer();
		synchronized (dbConnector) {
			Query[] queries = dbConnector.getRequests();
			if (queries == null || queries.length == 0)
				return Response.status(200).entity("No Requests.").build();
			try {
				int length = queries.length;
				for (int i = 0; i < length; i++) {
					String s = setRequest.getResult(queries[i].query);
					Forecast[] f = setRequest.toNormalForm(s, queries[i].days);
					for (int j = 0; j < f.length; j++)
						sb.append(f[j].toString() + "\n");
					sb.append("\n*************************************************\n");
				}
				dbConnector.clearTable();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (NullPointerException e1) {
				System.out.println(e1.getMessage());
			}
		}
		String output = sb.toString();
		return Response.status(200).entity(output).build();
	}
	
	// The getForecast function.
	// Get the requests from the user, and send them to be handled in the proper methods.
	public Response getForecast(int days, String state, String city, String zipcode) {
		String output = "System message: ";
		try {
			if ((state.isEmpty() || city.isEmpty()) && zipcode.isEmpty()) {
				output += "You need to send more details.";
				return Response.status(200).entity(output).build();
			}
			if (days  == 0) 
				output = "Can't give forecast for 0 days.";
			else if (zipcode.equals(""))
					setForecast(days, state, city);
			else
				setForecast(days, zipcode);
			output += "Your request saved in the system.";
			return Response.status(200).entity(output).build();
		} catch (Exception e) {
			output += "You request didn't properly executed";
			return Response.status(200).entity(output).build(); 
		}
	}
	
	// Handle the creation of the queries who based on state and city,
	// and save them in the database.
	private boolean setForecast(int days, String state, String city) {
		Request request = new Request(days, state, city);
		synchronized (dbConnector) {
			dbConnector.addRequest(request.getDays(), request.getQuery());
		}
		return true;
	}
	
	// Handle the creation of the queries who based on zipcode,
	// and save them in the database.
	private boolean setForecast(int days, String zipcode) {
		Request request = new Request(days, zipcode);
		synchronized (dbConnector) {
			dbConnector.addRequest(request.getDays(), request.getQuery());
		}
		return true;
	}
	
}
