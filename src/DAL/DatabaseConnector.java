package DAL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import BL.Request;

public class DatabaseConnector {
	
	private static DatabaseConnector instance;
	private MysqlDataSource dataSource;
	
	// database connection should be a singleton
	private DatabaseConnector() {
		defineDriver();
		setupDataSource();
	}
	
	// Get the instance.
	public static DatabaseConnector getInstance() {
		if (instance == null) {
			instance = new DatabaseConnector();
		}
		return instance;
	}
	
	
	public void defineDriver() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// Set up The connection details with the database.
	private void setupDataSource() {
		dataSource = new MysqlDataSource();
		dataSource.setURL("jdbc:mysql://localhost/weather");
		dataSource.setPort(3306);
		dataSource.setDatabaseName("weather");
		dataSource.setUser("root");
		dataSource.setPassword("");
	}
	
	// Bring all the requests that saved on the database.
	public Query[] getRequests() {
		ResultSet res;
		Connection connection;
		Statement statement;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();

			ArrayList<Query> queries = new ArrayList<>();
			res = statement.executeQuery("SELECT DAYS, QUERY FROM requests");
			while (res.next()) {
				Query q = new Query();
				q.days = res.getInt("DAYS");
				q.query = res.getString("QUERY");
				queries.add(q);
			}
			res.close();
			statement.close();
			connection.close();
			return queries.toArray(new Query[queries.size()]);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	// Save new request in the database.
	public boolean addRequest(int days, String query) {
		Connection connection;
		Statement statement;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			int rows = 0;
			rows += statement.executeUpdate(String.format(
					"INSERT INTO requests (DAYS, QUERY) VALUES (%d, \"%s\")",
					days, query));
			statement.close();
			connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	// Empty the table.
	public boolean clearTable() {
		Connection connection;
		Statement statement;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			int rows = 0;
			rows += statement.executeUpdate("Truncate table requests");
			statement.close();
			connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
