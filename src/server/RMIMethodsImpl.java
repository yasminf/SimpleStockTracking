/**
 * CICS-525 - Java Socket Programming
 * 
 * @authors Yasmin
 * @version 1.0 June 27, 2014
 * 
 */
package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import rmi.RMIMethods;


public class RMIMethodsImpl extends UnicastRemoteObject implements RMIMethods 
{
	private static final long serialVersionUID = 1L;
	
	public RMIMethodsImpl() throws RemoteException 
	{
		super();
	}

	@Override
	public boolean isStockInDB(String stockname) throws RemoteException 
	{
		boolean result = false;
		Connection con = Server.getConnection();		
		try 
		{
			PreparedStatement ps = con.prepareStatement("SELECT shares FROM stock WHERE ticker_name = ?");
			ps.setString(1, stockname);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				result = true;
			}
			else
			{
				result = false;
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	@Override
	public double getStockPrice(String ticker) throws RemoteException 
	{
		double price = 0;
		Connection con = Server.getConnection();
		try 
		{
			// get the stock price from the database
			PreparedStatement ps = con
					.prepareStatement("SELECT price FROM stock WHERE ticker_name = ? ");
			ps.setString(1, ticker);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				price = rs.getDouble(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return price;
	}

	@Override
	public int updateStockPrice(String ticker, double price) throws RemoteException 
	{
		int result = -1;
		Connection con = Server.getConnection();

		if(isStockInDB(ticker))
		{
			try 
			{
				PreparedStatement ps = con.prepareStatement("UPDATE stock SET price = ? WHERE ticker_name = ?");
				ps.setDouble(1, price);
				ps.setString(2, ticker);
				result = ps.executeUpdate();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = -1;
			}
		}
		else
		{
			try 
			{
				PreparedStatement ps = con.prepareStatement("INSERT INTO stock (shares, price, ticker_name) VALUES (?, ?, ?)");
				ps.setInt(1, 1000);
				ps.setDouble(2, price);
				ps.setString(3, ticker);
				result = ps.executeUpdate();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = -1;
			}
			
		}
		return result;
	}
}