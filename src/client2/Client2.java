/**
 * CICS-525 - Java Socket Programming
 * 
 * @authors Yasmin
 * @version 1.0 June 27, 2014
 * 
 */
package client2;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rmi.RMIMethods;

public class Client2 
{
	private static final String HOST = "localhost";
	private static final int RMIPORT = 3653;
	private static Registry registry;
	private static RMIMethods remoteApi;

	/*
	 * displays the main menu
	 */
	private static void showMenu() 
	{
		while (true) 
		{
			System.out.println("                            ");
			System.out.println("Please choose an option:");
			System.out.println("1: Obtain stock price");
			System.out.println("0: Quit");

			BufferedReader bufferRead = new BufferedReader(
					new InputStreamReader(System.in));
			try 
			{
				String input = bufferRead.readLine().trim();

				// check input against available commands
				if (input.equals("1")) 
				{
					queryStockPrice();
				}
				else if (input.equals("0")) 
				{
					System.out.println("Exiting program.");
					break;
				} 
				else 
				{
					System.out.println("Invalid command! Please try again.");
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}

	}

	private static void queryStockPrice() throws IOException 
	{
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("Please enter the Stock you wish to query: ");
		String stockName = bufferRead.readLine().trim().toUpperCase();

		if (!remoteApi.isStockInDB(stockName))
		{
			System.out.println("The Stock :> " + stockName + " <: is not in the database. Please use Client1 to add the stock before querying it.");
		}
		else
		{
			System.out.println("Stock price is : " + remoteApi.getStockPrice(stockName));
		}
	}

	public static void main(String[] args) throws Exception 
	{
		registry = LocateRegistry.getRegistry(HOST, RMIPORT);
		remoteApi = (RMIMethods) registry.lookup("methods");
		showMenu();
	}
}