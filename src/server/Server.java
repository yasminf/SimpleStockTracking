/**
 * CICS-525 - Java Socket Programming
 * 
 * @authors Yasmin
 * @version 1.0 June 27, 2014
 * 
 */
package server;


import java.io.PrintWriter;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Server 
{
	private static final int RMIPORT = 3653;
	private static Registry registry;
	private static org.hsqldb.server.Server server;

	/*
	 * creates a new RMI registry
	 */
	public static void startRegistry() throws RemoteException 
	{
		registry = java.rmi.registry.LocateRegistry.createRegistry(RMIPORT);
	}

	/*
	 * Register a Remote interface with the RMI registry
	 */
	public static void registerObject(String name, Remote remoteObj)
			throws RemoteException, AlreadyBoundException 
	{
		registry.bind(name, remoteObj);
	}

	/*
	 * start a local HSQLDB database and check if the tables already exist
	 */
	public static void startDatabase() 
	{
		server = new org.hsqldb.server.Server();
		server.setAddress("localhost");
		server.setDatabaseName(0, "DB");
		server.setDatabasePath(0, "file:./DB/db");
		server.setPort(9152);
		// set true for debugging
		server.setTrace(false);
		server.setLogWriter(new PrintWriter(System.out));
		server.start();
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace(System.out);
		}
		try {
			// check, if tables exists, if not create them
			Connection con = getConnection();
			DatabaseMetaData md = con.getMetaData();
			ResultSet rs = md.getTables(null, null, "USER", null);
			if (!rs.next()) 
			{
				createTables(con);
				populateTestData(con);
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}
		
	}
	static void createTables(Connection con)
	{
		System.out.println("Empty Database. Creating tables...");

		try
		{
			Statement stmt = con.createStatement();
			stmt.executeUpdate(
					"CREATE TABLE stock(price       DOUBLE, " +
					"                   shares      INTEGER, " +
					"                   ticker_name CHAR(20) , " +
					"                   row_id      INTEGER  NOT NULL IDENTITY, " +
					"                   CONSTRAINT  UK_stock_ticker UNIQUE (ticker_name) " +
					"                  ) ");
			stmt.close();

			// commit the transaction
			con.commit();
			System.out.println("Stock table created!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	} // createTables

	static void populateTestData(Connection con)
	{
		System.out.println("Empty Tables. Adding test data...");

		try
		{
			Statement stmt = con.createStatement();
			stmt.executeUpdate(
					"INSERT INTO     stock(shares, " +
					"                       price, " +
					"                       ticker_name " +
					"                      )" +
					"                values(1000, " +
					"                       1.0, " +
					"                       'GOOG' " +
					"                      )");
			stmt.executeUpdate(
					"INSERT INTO     stock(shares, " +
					"                       price, " +
					"                       ticker_name " +
					"                      )" +
					"                values(1000, " +
					"                       1.0, " +
					"                       'MSFT' " +
					"                      )");
			// commit the transaction
			con.commit();
			stmt.close();
			System.out.println("Test data added!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	} // populateTestData

	/*
	 * getter for the connection to the HSQLDB database
	 */
	public static Connection getConnection() 
	{
		Connection con = null;
		try 
		{
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9152/DB", "SA", "");
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return con;
	}

	public static void main(String[] args) throws Exception 
	{
		startRegistry();
		registerObject("methods", new RMIMethodsImpl());

		startDatabase();
	}
}