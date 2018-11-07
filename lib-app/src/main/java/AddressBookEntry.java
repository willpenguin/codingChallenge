/**
 * Represents an AddressBookEntry that is structured similarly to the
 * data contained for each record in the elastic search system.
 * @author William Warren
 * @version 1
 * @Date 11/7/2018
 */
public class AddressBookEntry {
	
	String fName;
	String lName;
	String streetAddress;
	String city;
	String state;
	String zip;
	String phoneNumber;
	
	
	/**
	 * constructor that when called will also ensure
	 * that the arguments are valid.
	 * @param fName first name
	 * @param lName last name
	 * @param streetAddress
	 * @param city
	 * @param state
	 * @param zip
	 * @param phoneNumber
	 */
	public AddressBookEntry(String fName, String lName, String streetAddress, String city, String state, String zip, String phoneNumber) {
		this.fName = fName;
		this.lName = lName;
		setStreetAddress(streetAddress);
		setCity(city);
		setState(state);
		setZip(zip);
		setPhoneNumber(phoneNumber);
	}
	
	/**
	 * Constructor for allowing the AddressBookSystem
	 * to make an address book entry without the additional parameters.
	 * AddressBookEntry is not set up to currently utilize this method
	 * but ideally it would be.
	 * @param fName first name
	 */
	public AddressBookEntry(String fName) {
		this.fName = fName;
	}
	
	/**
	 * Constructor for allowing the AddressBookSystem
	 * to make an address book entry without the additional parameters.
	 * AddressBookEntry is not set up to currently utilize this method
	 * but ideally it would be.
	 * @param fName first name
	 * @param lName last name
	 */
	public AddressBookEntry(String fName, String lName) {
		this.fName = fName;
		this.lName = lName;
	}
	
	/**
	 * validates the zip then sets it.
	 * Throws exception if zip is invalid.
	 * @param zip
	 * @return true if successful
	 */
	public boolean setZip(String zip) {
		char[] zipAsChars = zip.toCharArray();
		if(zipAsChars.length != 5) {
			throw new IllegalArgumentException("zip is invalid. length should be 5");
		}
		this.zip = zip;
		return true;
	}
	
	/**
	 * validates the phone number then sets it.
	 * Throws exception if phone number is invalid.
	 * @param number phone number
	 * @return true if successful
	 */
	public boolean setPhoneNumber(String number) {
		char[] numberAsChars = number.toCharArray();
		if(numberAsChars.length!= 10 && numberAsChars.length!= 11) {
			throw new IllegalArgumentException("phone number is invalid length. should be 10 or 11 was: " + numberAsChars.length );
		}
		this.phoneNumber = number;
		return true;
	}
	
	/**
	 * Currently just sets the state but could
	 * be altered down the line to use a map
	 * to validate state correctness.
	 * @param state
	 * @return true if successful 
	 */
	public boolean setState(String state) {
		this.state = state;
		return true;
	}
	
	/**
	 * Currently just sets the city but could
	 * be altered down the line to use a map to
	 * determine if a city is in the set state.
	 * @param city
	 * @return true if successful
	 */
	public boolean setCity(String city) {
		this.city = city;
		return true;
	}
	
	/**
	 * Currently just sets the address but
	 * could be altered down the line to determine if
	 * an address is valid by using a map to determine
	 * if an address exists within the already set city.
	 * @param address
	 * @return true if successful
	 */
	public boolean setStreetAddress(String address) {
		this.streetAddress = address;
		return true;
	}

}
