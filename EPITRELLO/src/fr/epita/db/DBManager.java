package fr.epita.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class DBManager {
	
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/test";  
	static final String USER = "sa"; 
	static final String PASS = ""; 
	private Statement stmt;
	private Connection connection;
	private Set<String> users;
	   
	public Set<String> getUsers() {
		return users;
	}

	private static DBManager dbManager;
	private DBManager() {
		users = new HashSet<>();
	}
	
	public static DBManager getInstance() {
		if(dbManager == null) {
			dbManager = new DBManager();
		}
		return dbManager;
	}
	
	private Connection getConnection() throws SQLException, IOException {
		
		 	Properties prop=new Properties();
		 	FileInputStream ip;
			try {
				ip = new FileInputStream("conf.properties");
				prop.load(ip);
			} catch (FileNotFoundException e) {
				System.out.println("Error occured in reading configuration file: "+e.getLocalizedMessage());
				return null;
			}
			String jdbcUrl = prop.getProperty("jdbc.url");
			String user = prop.getProperty("jdbc.user");
			String password = prop.getProperty("jdbc.password");
			return DriverManager.getConnection(jdbcUrl, user, password);
		}
	
	public void initialize() {
		try {
			Class.forName(JDBC_DRIVER); 
			connection = getConnection();
			if(connection == null ) {
				connection = DriverManager.getConnection(DB_URL,USER,PASS);
			}
		} catch(Exception ex) {
			System.err.println("Error occured in db initialization getConnection: "+ex.getLocalizedMessage());
		}
		String sql;
		try {
			stmt = connection.createStatement();
			sql =  "CREATE TABLE   users (user VARCHAR(255) not NULL UNIQUE)";  
			stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			ResultSet rs = null;
			try {
			sql = "SELECT user FROM users"; 
			rs = stmt.executeQuery(sql); 
			while(rs.next()) { 
				users.add(rs.getString("user"));
			}
			} catch(SQLException exx) {
				System.err.println("Error occured in db initialization fetch users: "+exx.getLocalizedMessage());
			} finally {
				try {
					if(rs!=null) {
						rs.close();
					}
				} catch (SQLException e) {
					System.err.println("Error occured in closing rs: "+e.getLocalizedMessage());				}
			}
		}

	}
	
	public boolean addUser(String user) {
		String sql = "INSERT INTO users " + "VALUES('"+user+"')"; 
        try {
			stmt.executeUpdate(sql);
			return true;
		} catch (SQLException ex) {
			System.err.println("Error occured in db addUser function: "+ex.getLocalizedMessage());
			return false;
		} 
	}
	
	public void releaseResources() {
		try { 
            if(connection!=null) connection.close(); 
         } catch(SQLException ex) { 
 			System.err.println("Error occured in db releaseResources function: "+ex.getLocalizedMessage());
         } 
	}

}
