import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
/**
 * Test suite
 * @author William Warren
 *
 */
class testSuite {

	@Test
	void testValidEntries() {
		AddressBookSystem system = new AddressBookSystem();
		system.establishConnection("127.0.0.1");
		
		system.post("timmy","Flay", "royal", "Beach", "VA", "23454", "1122334455");
		system.post("billy", "Bob", "black", "grasslands", "CA", "12345", "2233445566");
		system.client.close();
		
		//test with simple query
		system.establishConnection("127.0.0.1");
		assertEquals(system.getContact(10, 0, "grasslands").getHits().getTotalHits(), 1);
		system.client.close();
		
		//remove entries from elasticsearch and also test delete
		AddressBookSystem system1 = new AddressBookSystem();
		system1.establishConnection("127.0.0.1");
		
		system1.delete("timmyflay");
		system1.delete("billybob");
		system1.client.close();
	}
	
	@Test
	void testAddressBookInvalidPhone() {
		boolean pass = false;
		try {
			AddressBookEntry entry = new AddressBookEntry("timmy","Flay", "royal", "Beach", "VA", "23454", "1122334455999");
		}
		catch(IllegalArgumentException e) {
			pass = true;
		}
		assertTrue(pass);
	}
	
	@Test
	void testAddressBookInvalidZip() {
		boolean pass = false;
		try {
			AddressBookEntry entry = new AddressBookEntry("timmy","Flay", "royal", "Beach", "VA", "234545", "1122334455");
		}
		catch(IllegalArgumentException e) {
			pass = true;
		}
		assertTrue(pass);
	}
	


}
