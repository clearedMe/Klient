package dataParser;

public class Contact
{
	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_EMAIL = 1;
	public static final int TYPE_PHONE = 2;
	public static final int TYPE_JABBER = 3;
	
	private String value;
	private int type;
	
	
	public Contact(String value, int type)
	{
		this.value = value;
		this.type = type;
	}
	
	
	public String getValue()
	{
		return this.value;
	}
	
	
	public int getType()
	{
		return this.type;
	}
}
