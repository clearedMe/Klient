package dataParser;

import java.util.List;

public interface InsertCustomerAnalyserToBase
{
	public void insertRecordIntoBase(String name, String surname, String age, String city, List<Contact> contacts);
}
