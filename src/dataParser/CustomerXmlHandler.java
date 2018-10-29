package dataParser;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CustomerXmlHandler extends DefaultHandler
{
	private InsertCustomerAnalyserToBase baseInterface;
	
	private String name;
	private String surname;
	private String age;
	private String city;
	private List<Contact> contacts;
	
	private String text;
	private boolean contactsAnalyse;
	
	public CustomerXmlHandler(InsertCustomerAnalyserToBase nbaseInterface)
	{
		this.baseInterface = nbaseInterface;
		
		this.age = null;
		this.contactsAnalyse = false;
		this.contacts = new LinkedList<Contact>();
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		if (qName.equals("contacts"))
			this.contactsAnalyse = true;
	}

	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equals("person"))
		{
			this.baseInterface.insertRecordIntoBase(name, surname, age, city, contacts);
			
			this.age = null;
			contacts.clear();
		}
		else if (qName.equals("contacts"))
		{
			this.contactsAnalyse = false;
		}
		
		if (contactsAnalyse == true)
		{
			if (qName.equals("phone"))
				this.contacts.add(new Contact(this.text, Contact.TYPE_PHONE)); 
			else if (qName.equals("email"))
				this.contacts.add(new Contact(this.text, Contact.TYPE_EMAIL));
			else if (qName.equals("jabber"))
				this.contacts.add(new Contact(this.text, Contact.TYPE_JABBER));
			else
				this.contacts.add(new Contact(this.text, Contact.TYPE_UNKNOWN));				
		}
		else
		{
			if (qName.equals("name"))
				this.name = this.text;
			else if (qName.equals("surname"))
				this.surname = this.text;
			else if (qName.equals("age"))
				this.age = this.text;
			else if (qName.equals("city"))
				this.city = this.text;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		this.text = String.copyValueOf(ch, start, length).trim();
	}
}
