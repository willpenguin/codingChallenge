import static spark.Spark.*;

/**
 * Must be running for get, post, and delete to work.
 * Receives information from browser and calls corresponding
 * function.
 * 
 * @author William Warren
 * @version 1
 * @Date 11/7/2018
 *References: https://www.baeldung.com/spark-framework-rest-api,
 *http://sparkjava.com/tutorials/maven-setup
 */
public class Driver {
	
	/**
	 * Main entry point.
	 * @param args: none needed for startup
	 */
	public static void main(String[] args) {
		AddressBookSystem system = new AddressBookSystem();
		//user can change port here.
		system.establishConnection("127.0.0.1");
		
		//example: http://localhost:4567/user
		get("/user", (req, res) -> "Hello user");
        
        
		//example: http://localhost:4567/user/Billy
        get("/user/:name", (req,res)->{
            return "Hello, "+ req.params(":name");
        });
        
        
      //example:  http://localhost:4567/contact?pageSize=5&page=0&query={harrisonburg}
        get("/contact", (req, res) -> {

        	if(req.queryParams().size() > 0) {
        		//assuming all params are entered and valid
        		return system.getContact(Integer.parseInt(req.queryParams("pageSize")), 
        				Integer.parseInt(req.queryParams("page")), req.queryParams("query"));
        	}
        	else {
        		 return system.list(); //list all
        	}
        	
        });
        
        
        
        put("/contact", (req, res) -> {
        	if(req.params().size() != 7) {
        		return "Invalid number of AddressBookEntry arguments. 7 expected: fName, lName," + 
        	           "streetAddress, city, state, zip, phoneNumber";
        	}
        	else {
        		system.put(req.queryParams("fName"), req.queryParams("lName"), req.queryParams("streetAddress")
        				,req.queryParams("city"), req.queryParams("state"), 
        				req.queryParams("zip"), req.queryParams("phoneNumber"));
        		return "entry updated";
        	}
        	
        });
        
        delete("/contact/:name", (req, res) ->{
        	system.delete(req.params(":name"));
        	return "Delete sucessful";
        });
        
        post("/contact", (req, res) -> {
        	if(req.params().size() != 7) {
        		return "Invalid number of AddressBookEntry arguments. 7 expected: fName, lName," + 
        	           "streetAddress, city, state, zip, phoneNumber";
        	}
        	else {
        		system.post(req.queryParams("fName"), req.queryParams("lName"), req.queryParams("streetAddress")
        				,req.queryParams("city"), req.queryParams("state"), 
        				req.queryParams("zip"), req.queryParams("phoneNumber"));
        		return "entry added";
        	}       	
        });
	}
}
