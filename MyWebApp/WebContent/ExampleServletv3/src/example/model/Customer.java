package example.model;

/**
 * A simple bean to hold data
 */
public class Customer {
	private String Name, City, Country;//customer "schema"
	
	

	public Customer(String name, String city, String country) {
		Name = name;
		City = city;
		Country = country;
	}

	public String getName() {
		return Name;
	}

	public String getCity() {
		return City;
	}

	public String getCountry() {
		return Country;
	}
	
	
}
