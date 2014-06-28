/**
 * CICS-525 - Java Socket Programming
 * 
 * @authors Yasmin
 * @version 1.0 June 27, 2014
 * 
 */
package client1;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rmi.RMIMethods;

public class Client1 {
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
			System.out.println("1: Update stock price");
			System.out.println("0: Quit");

			BufferedReader bufferRead = new BufferedReader(
					new InputStreamReader(System.in));
			try 
			{
				String input = bufferRead.readLine().trim();

				if (input.equals("1")) 
				{
					updateStock();
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

	private static void updateStock() throws IOException
	{
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));

		//get ticker_symbol
		System.out.print("Enter the ticker name: ");		
		String ticker = bufferRead.readLine();

		//get new price		
		System.out.print("Enter the new price: ");
		Double stock_price = Double.parseDouble(bufferRead.readLine());
		
		if (remoteApi.updateStockPrice(ticker.trim().toUpperCase(), stock_price) < 0) 
		{
			System.out.println("Sorry, stock is invalid");
		} 
		else 
		{
			System.out.println("Stock price updated succesfully");
		}
	}

	public static void main(String[] args) throws Exception 
	{
		registry = LocateRegistry.getRegistry(HOST, RMIPORT);
		remoteApi = (RMIMethods) registry.lookup("methods");
		showMenu();
	}
}