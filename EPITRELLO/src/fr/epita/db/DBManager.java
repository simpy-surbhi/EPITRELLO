package fr.epita.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
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

	static private DBManager dbManager;
	private DBManager() {
		// TODO Auto-generated constructor stub
		users = new HashSet<String>();
	}
	
	public static DBManager getInstance() {
		if(dbManager == null) {
			dbManager = new DBManager();
		}
		return dbManager;
	}
	
	public void initialize() {
		try {
			Class.forName(JDBC_DRIVER); 
			connection = DriverManager.getConnection(DB_URL,USER,PASS);
		} catch(SQLException ex) {
			System.out.println("Error occured in db initialization getConnection: "+ex.getLocalizedMessage());
		}catch(ClassNotFoundException ex) {
			System.out.println("Error occured in db initialization getConnection: "+ex.getLocalizedMessage());
		}
		String sql;
		try {
			stmt = connection.createStatement();
			sql =  "CREATE TABLE   users (user VARCHAR(255) not NULL UNIQUE)";  
			stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			try {
			sql = "SELECT user FROM users"; 
			ResultSet rs = connection.createStatement().executeQuery(sql); 
			while(rs.next()) { 
				users.add(rs.getString("user"));
			}
			} catch(SQLException exx) {
				System.out.println("Error occured in db initialization fetch users: "+exx.getLocalizedMessage());
			}
		}

	}
	
	public boolean addUser(String user) {
		String sql = "INSERT INTO users " + "VALUES('"+user+"')"; 
        try {
			stmt.executeUpdate(sql);
			System.out.println("Inserted records into the table..."); 
			return true;
		} catch (SQLException ex) {
			System.out.println("Error occured in db addUser function: "+ex.getLocalizedMessage());
			return false;
		} 
	}
	
	public void releaseResources() {
		try { 
            if(connection!=null) connection.close(); 
         } catch(SQLException ex) { 
 			System.out.println("Error occured in db releaseResources function: "+ex.getLocalizedMessage());
         } 
	}

}
