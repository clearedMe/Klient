package dataParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Application
{
	public static void main(String[] args)
	{
		args = new String[]{
				"jdbc:postgresql://127.0.0.1:5432/postgres",
				"postgres",
				"admin",
				"dane/dane-osoby.txt",
				"dane/dane-osoby.xml"
		};
		
		if (args.length >= 5)
		{
			try
			{
				//jdbc:postgresql://host:port/database
				String url = args[0];
				Properties props = new Properties();
				
				props.setProperty("user", args[1]);
				props.setProperty("password", args[2]);
				props.setProperty("ssl", "false");
			
				Connection base = DriverManager.getConnection(url, props);

				//System.out.println("Connected database successfully..."+ base.toString());
				FileAnalyser analizator = CustomerAnalyser.create(base);
				
				for (int i = 4; i < args.length; i++)
				{
					String fileName = args[i];
					analizator.insertFileIntoBase(fileName);
				}

				base.close();

			}
			catch (SQLException e)
			{
				System.out.println("Nieudane polaczenie z baza! (sprawdz czy url, nazwa uzytkownika i haslo sa prawidlowe)");
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Nieprawidlowa liczba parametrow!");
			System.out.println("Prawidlowe wywolanie: -database_url -username -password [dane-osoby.txt] [dane-osoby.xml]");
		}	
	}
}
		
		
		
		
		
		
		
		
		
		
