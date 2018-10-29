package dataParser;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import java.sql.Types;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CustomerAnalyser implements FileAnalyser, InsertCustomerAnalyserToBase
{
	private Connection base;

	public CustomerAnalyser(Connection base)
	{
		this.base = base;
	}
	
	
	public static FileAnalyser create(Connection base)
	{
		return new CustomerAnalyser(base);
	}
	
	
	public void insertFileIntoBase(String fileName)
	{
		try
		{
			if (fileName.contains(".xml"))
				this.xmlAnalyse(fileName);
			else
				this.csvAnalyse(fileName);					

		}
		catch (FileNotFoundException e)
		{
			System.out.println("Podany plik nie istnieje: "+fileName);
		} 
		catch (IOException e)
		{
			System.out.println("Nieprawidlowe dane w pliku: "+fileName);
		} 
		catch (SQLException e)
		{
			System.out.println("Blad zapisu do bazy danych dla pliku: "+fileName);
		}	
	}
	
	
	public void insertRecordIntoBase(String name, String surname, String age, String city, List<Contact> contacts)
	{
		try
		{
			long id_customer = this.insertCustomerIntoBase(name,surname,age,city);
			this.insertContactsIntoBase(id_customer,contacts);
			
		} catch (SQLException e)
		{
			System.out.println("Nieudany zapis do bazy");
			e.printStackTrace();
		}
	}
	
	
	protected void xmlAnalyse(String fileName) throws IOException, SQLException
	{
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser builder = factory.newSAXParser();
			
			DefaultHandler xmlHandler = new CustomerXmlHandler(this);
			InputStreamReader stream = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8);
			
			builder.parse(new InputSource(stream), xmlHandler);

		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
	}
	
	
	protected void csvAnalyse(String fileName) throws IOException, SQLException
	{
		InputStreamReader stream = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8);
		BufferedReader file = new BufferedReader(stream);
		
		String line;
		List<Contact> contacts = new LinkedList<Contact>();
		
		while ((line = file.readLine()) != null)
		{
			Scanner scanner = new Scanner(line);
			scanner.useDelimiter(",");
			
			String name = scanner.next();
			String surname = scanner.next();
			String age = scanner.next();
			String city = scanner.next();
			
			while (scanner.hasNext() == true)
			{
				String value = scanner.next();
				Contact contact = new Contact(value, this.getContactType(value));
				
				contacts.add(contact);
			}
			
			this.insertRecordIntoBase(name, surname, age, city, contacts);
			
			contacts.clear();
			scanner.close();
		}
		
		file.close();
	}
	
	
	protected long insertCustomerIntoBase(String name, String surname, String age, String city) throws SQLException
	{
		String sqlQuery = "INSERT INTO \"CUSTOMERS\" (\"NAME\", \"SURNAME\", \"AGE\") VALUES (?, ?, ?)";
		PreparedStatement statement = this.base.prepareStatement(sqlQuery,Statement.RETURN_GENERATED_KEYS);
		
		statement.setString(1, name);
		statement.setString(2, surname);
				
		if(age == null || age.isEmpty())
			statement.setNull(3, Types.INTEGER);
		else
			statement.setInt(3, Integer.parseInt(age));
		
		statement.execute();
		
		ResultSet result = statement.getGeneratedKeys();
		result.next();
		
		return result.getLong(1);
	}
	
	
	protected void insertContactsIntoBase(long idCustomer, List<Contact> contacts) throws SQLException
	{
		for (Contact contact : contacts)
		{
			int type = contact.getType();
			
			String sqlQuery = "INSERT INTO \"CONTACTS\" (\"ID_CUSTOMER\", \"TYPE\", \"CONTACT\") VALUES (?, ?, ?)";
			PreparedStatement statement = this.base.prepareStatement(sqlQuery);
			statement.setLong(1, idCustomer);
			statement.setInt(2, type);
			statement.setString(3, contact.getValue());
			
			statement.execute();
		}
	}
	
	
	protected int getContactType(String contact)
	{
		int type = Contact.TYPE_UNKNOWN;
		
		if (contact.contains("@"))
			type = Contact.TYPE_EMAIL;
		else if (contact.trim().replace("-","").matches("[0-9]{9}"))
			type = Contact.TYPE_PHONE;
		else if (contact.matches("[a-zA-Z0-9]+"))
			type = Contact.TYPE_JABBER;
		
		return type;
	}
}

