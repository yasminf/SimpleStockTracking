/**
 * CICS-525 - Java Socket Programming
 * 
 * @authors Yasmin
 * @version 1.0 June 27, 2014
 * 
 */
package rmi;
import java.rmi.*;

public interface RMIMethods extends Remote 
{
	
	public boolean isStockInDB(String stockname) throws RemoteException;

	public int updateStockPrice(String ticker, double price) throws RemoteException;

	public double getStockPrice(String ticker) throws RemoteException;
}