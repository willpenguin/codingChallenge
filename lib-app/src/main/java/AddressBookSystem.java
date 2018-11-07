import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * Interacts with elasticsearch system to store and retrive data
 * 
 * @author William Warren
 * @version 1
 * @Date 11/7/2018
 * References: https://www.baeldung.com/elasticsearch-java,
 * https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-index.html
 */
public class AddressBookSystem {
	
	Client client = null;
	
	/**
	 * constructor for the system
	 */
	public AddressBookSystem() {
		
	}
	
	/**
	 * 
	 * @param port address to connect to
	 * @return whether or not the connection was successful
	 */
	public boolean establishConnection(String port) {
		try {
			@SuppressWarnings("resource")
			Client cli = new PreBuiltTransportClient(
					  Settings.builder().put("client.transport.sniff", true)
					                    .put("cluster.name","elasticsearch").build()) 
					  .addTransportAddress(new TransportAddress(InetAddress.getByName(port), 9300));
			client = cli;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	/**
	 * returns a list of all records in the elasticsearch system
	 * @return searchHits: a list of the all records in the
	 * elasticsearch system.
	 */
	public List<SearchHit> list() {
		SearchResponse response = client.prepareSearch().execute().actionGet();
		List<SearchHit> searchHits = Arrays.asList(response.getHits().getHits());
		return searchHits;
		
	}

	
	/**
	 * remove and entry from the elasticsearch system
	 * @param name the unique name for an entry (first name concatenated with last name)
	 */
	public void delete(String name){
		DeleteResponse response = client.prepareDelete(name.toLowerCase(), "AddressBookEntry", "1")
				  .get();
		if(response.getResult() != Result.DELETED) {
			throw new IllegalArgumentException(" name does not exist");
		}
	}
	
	/**
	 * Add a contact to the elasticsearch system.
	 * Currently the additionalArgs are mandatory, however,
	 * ideally, the method would be changed in a future pass to allow for them
	 * to be optional ie allow for an entry with just a first name with default
	 * attributes that would be changed at a later time.
	 * @param fName: contacts first name
	 * @param additionalArgs: firstName, lastName, streetAddress,
	 * city, state, zip, phoneNumber.
	 */
	public void post(String fName, Object ... additionalArgs) {
		AddressBookEntry entry;
		String lName = "";
		
		if(additionalArgs.length > 0) {
			//assuming all required args are provided... may be checked for in driver
			entry = new AddressBookEntry(fName, (String)additionalArgs[0], (String)additionalArgs[1], 
					(String)additionalArgs[2], (String)additionalArgs[3], (String)additionalArgs[4], 
					(String)additionalArgs[5]);
			lName = (String)additionalArgs[0]; //gather lName for unique id creation
		}
		else {
			entry = new AddressBookEntry(fName);
		}
		
		
		//ensure the entry does not already exist
		SearchResponse list = client.prepareSearch().execute().actionGet();
		List<SearchHit> searchHits = Arrays.asList(list.getHits().getHits());
		for(SearchHit hit : searchHits) 
		{
			if(hit.getIndex().equals((fName + lName).toLowerCase())) 
			{
				throw new IllegalArgumentException("Entry already exists");
			}
		}
		
		//construct the entry	
		XContentBuilder builder = null;
		try {
			builder = XContentFactory.jsonBuilder()
					  .startObject()
					  .field("firstName", fName)
					  .field("lastName", (String)additionalArgs[0])
					  .field("streetAddress", (String)additionalArgs[1])
					  .field("city", (String)additionalArgs[2])
					  .field("state", (String)additionalArgs[3])
					  .field("zip", (String)additionalArgs[4])
					  .field("phone", (String)additionalArgs[5])
					  .endObject();
		} catch (IOException e) {
			throw new IllegalArgumentException("invalid number of args: post");
		}

				//the unique name for an entry is the concatenation of first and last name.
				//store entry
				IndexResponse response = client.prepareIndex((fName + lName).toLowerCase(), 
						"AddressBookEntry" , "1")
				  .setSource(builder).get();							
	}
	
	
	/**
	 * update an existing entry (requires full entity information).
	 * Would be nice to allow for use of just a first name(fName) ie
	 * make additionalArgs[0](lName) optional. 
	 * @param fName: first name for AddressBookEntry record
	 * @param additionalArgs: firstName, lastName, streetAddress,
	 * city, state, zip, phoneNumber.
	 */
	public void put(String fName, Object ... additionalArgs) {
		AddressBookEntry entry;
		String lName = "";
		boolean contains = false;
		
		if(additionalArgs.length > 0) {
			//assuming all required args are provided. Create new entry entity.
			//an error will be throw by AddressBookEntry if any values are invalid
			entry = new AddressBookEntry(fName, (String)additionalArgs[0], (String)additionalArgs[1], 
					(String)additionalArgs[2], (String)additionalArgs[3], (String)additionalArgs[4], 
					(String)additionalArgs[5]);
			lName = (String)additionalArgs[0];
		}
		else {
			entry = new AddressBookEntry(fName);
		}
		
		//determine if record exists
		SearchResponse list = client.prepareSearch().execute().actionGet();
		List<SearchHit> searchHits = Arrays.asList(list.getHits().getHits());
		for(SearchHit hit : searchHits) {
			if(hit.getIndex().equals((fName + lName).toLowerCase())) {
				contains = true;
			}
		}
		//record does not exist so throw exception
		if(!contains) {
			throw new IllegalArgumentException("Entry does not exist: update failure");
		}
		
		
		//create record for elasticsearch
		XContentBuilder builder = null;
		try {
			builder = XContentFactory.jsonBuilder()
					  .startObject()
					  .field("firstName", fName)
					  .field("lastName", (String)additionalArgs[0])
					  .field("streetAddress", (String)additionalArgs[1])
					  .field("city", (String)additionalArgs[2])
					  .field("state", (String)additionalArgs[3])
					  .field("zip", (String)additionalArgs[4])
					  .field("phone", (String)additionalArgs[5])
					  .endObject();
		} catch (IOException e) {
			throw new IllegalArgumentException("invalid number of args: update");
		}

				//store record in elasticsearch
				IndexResponse response = client.prepareIndex((fName + lName).toLowerCase(),
						"AddressBookEntry" , "1")
				  .setSource(builder).get();
	}
	
	
	/**
	 * returns a record corresponding to the unique id of an entry
	 * @param name the unique id for the entry(fName + lName)
	 * @return record for the entry
	 */
	public GetResponse get(String name) {
		GetResponse response = client.prepareGet(name.toLowerCase(),"AddressBookEntry","1").get();
		
		return response;
	}
	
	
	/**
	 * 
	 * @param pageSize: max number of records to return.
	 * @param page: number or record offset
	 * @param query: string representation of a querySimpleQuery
	 * @return records corresponding to query
	 */
	public SearchResponse getContact(int pageSize, int page, String query) {
		
		
		SearchResponse response = client.prepareSearch()
				  .setTypes()
				  .setFrom(page)
				  .setSize(pageSize)
				  .setSearchType(SearchType.QUERY_THEN_FETCH)
				  .setPostFilter(QueryBuilders.simpleQueryStringQuery(query))
				  .execute()
				  .actionGet();
		
		return response;
	}
	
}
