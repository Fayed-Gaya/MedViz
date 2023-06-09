package backend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

public class Server extends JFrame implements Runnable{
	
	// Server GUI Setup
	private JTextArea serverLog;
	private ServerSocket serverSocket;
	private ArrayList<HandleAClient> clientList = new ArrayList<HandleAClient>();
	private int clientNumber = 0;
	
	// FireStore set up
	private Firestore db = null;
	private final String PROJECTID = "medviz-384013";

	public Server() {
		// Server GUI setup
		super("Med Viz Server");
		
		this.setSize(400, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Create the menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> System.exit(0));
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
		
		// Create server log
		serverLog = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(serverLog);
		JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		serverLog.setEditable(false); // disable user input
		this.add(scrollPane);
		
		//initialize DB connection
		FirestoreOptions firestoreOptions = null;
		try {
			serverLog.append("Connecting to Firestore..." + '\n');
			firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
			    .setProjectId(PROJECTID)
			    .setCredentials(GoogleCredentials.getApplicationDefault())
			    .build();
			serverLog.append("Connected to Firestore... " + '\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.db = firestoreOptions.getService();
		
		// Initialize Window
		this.serverSocket = null;
		this.setVisible(true);
		Thread main = new Thread(this);
		main.start();

	}
	
	public void makeAdmin() {
		String hashedPassword = BCrypt.hashpw("admin", BCrypt.gensalt());
		DocumentReference docRef = db.collection("users").document("admin");
		Map<String, Object> userData = new HashMap<>();
		userData.put("username", "admin");
		userData.put("password", hashedPassword);
		userData.put("admin", true);
		ApiFuture<WriteResult> result = docRef.set(userData);
		try {
			System.out.println("Update time : " + result.get().getUpdateTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public String processRequest(String req) {
		//takes request as a string, casts to JSON and parses before calling CRUDA method, returns response as string
		String response = null;
		JSONObject request = new JSONObject(req);
		switch(request.getString("type")) {
		case("s"):
			response = signup(request.getString("username"), request.getString("pw"));
			return response;
		case("l"):
			response = login(request.getString("username"), request.getString("pw"));
			return response;
		case("c"):
			response = create(request.getString("fName"), request.getString("lName"), request.getString("city"),
					request.getString("state"), request.getString("country"), request.getString("phone"),
					request.getString("condition"), request.getString("DOB"));
			return response;
		case("r"):
			response = read(request.getString("field"), request.getString("val"), request.getString("op"));
			return response;
		case("u"):
			response = update(request.getString("fName"), request.getString("lName"), request.getString("field"), request.getString("val"));
			return response;
		case("d"):
			response = delete(request.getString("fName"), request.getString("lName"));
			return response;
		case("a"):
			response = aggregate(request.getInt("low"), request.getInt("high"), request.getString("condition"));
			return response;
		default:
			System.err.println("Invalid JSON request");
			return null;	
		}
	}
	
	public String signup(String username, Object pw) {
		serverLog.append("\nAttempting sign-up...");
		System.out.println("Server: Attempting sign-up...");
		String response = null;
		//check if user already exists
		ApiFuture<QuerySnapshot> query = null;
		CollectionReference users = db.collection("users");
		query = users.whereEqualTo("username", username).get();
		QuerySnapshot document = null;
		try {
			document = query.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if(!document.isEmpty()) {
			serverLog.append("\nUser already exists...");
			System.out.println("User already exists:");
			response =  "Username Exists";
		}
		else {
			serverLog.append("\nCreating User:...");
			String hashedPassword = BCrypt.hashpw((String)pw, BCrypt.gensalt());
			DocumentReference docRef = db.collection("users").document(username);
			Map<String, Object> userData = new HashMap<>();
			userData.put("username", username);
			userData.put("password", hashedPassword);
			userData.put("admin", false);
			ApiFuture<WriteResult> result = docRef.set(userData);
			try {
				System.out.println("Server: Update time : " + result.get().getUpdateTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			response = "{username: " + username + ", verified: true, " + "admin: false }";
			serverLog.append("\nUser " + username + " created.");
			
		}

		return response;
	}
	
	public String login(String username, Object pw) {
		serverLog.append("\nAttempting Login...");
		String response = null;
		
		ApiFuture<QuerySnapshot> query = null;
		CollectionReference users = db.collection("users");
		query = users.whereEqualTo("username", username).get();
	
		List<QueryDocumentSnapshot> user = null;
		try {
			user = query.get().getDocuments();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if(user.isEmpty()) {
			serverLog.append("\nUser does not exit...");
			return "{verified: false}";
		}
		
		if(BCrypt.checkpw((String) pw, user.get(0).getString("password"))) {			
			serverLog.append("\nLogin successful...");
			response = "{username: " + user.get(0).getString("username") + ", verified: true, " + "admin: " + user.get(0).getBoolean("admin") + " }";
		}
		else {
			serverLog.append("\nIncorrect Password...");
			response = "{verified: false}";
		}
		
		
		return response;
	}

	public String create(Patient patient) {
		serverLog.append("\nAttempting Patient Creation...");
		
		//check if patient record already exists
		ApiFuture<QuerySnapshot> query = null;
		CollectionReference patients = db.collection("patients");
		query = patients.whereEqualTo("fName", patient.getfName()).whereEqualTo("lName", patient.getlName()).get();
		QuerySnapshot document = null;
		try {
			document = query.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		// Patient Exists
		if(!document.isEmpty()) {
			serverLog.append("\nPatient already exists...");
			System.out.println("Record already exists:");
			try {
				for (DocumentSnapshot doc : query.get().getDocuments()) {
					System.err.println(doc.getId());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			return "Exists";
		}
		
		//write new record to DB
		serverLog.append("\nPatient created...");
		String docName = patient.getfName() + "_" + patient.getlName();
		ApiFuture<WriteResult> future = db.collection("patients").document(docName).set(patient.getPatientMap());
		try {
			System.out.println("Update time : " + future.get().getUpdateTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return "Created";
	}

	public String create(JSONObject patient) {
		Patient p = null;
		try {
			p = new Patient(patient);
			return create(p);
		}
		catch(JSONException e){
			System.err.println("Invalid JSON: " + e.getMessage());
			return null;
		}
	}

	public String create(String fName, String lName, String city, String state,
					String country, String phone, String condition, String DOB) {
		
		phone = phone.substring(1);
		
		Patient patient = new Patient(fName, lName, city, state, country,
					phone, DOB, condition);
		return create(patient);
	}

	public String read(String field, String val, String op) {
		serverLog.append("\nAttempting Read...");
		CollectionReference patients = db.collection("patients");
		Query query = null;
		ApiFuture<QuerySnapshot> querySnapshot = null;

		//read op variable to decide how to handle query
		switch(op) {
		case "eq":
			serverLog.append("\nEquals to search...");
			query = patients.whereEqualTo(field, val);
			querySnapshot = query.get();
			break;
		case "lt":
			serverLog.append("\nLess than search...");
			query = patients.whereLessThan(field, val);
			querySnapshot = query.get();
			break;
		case "leq":
			serverLog.append("\nLess than or equals to search...");
			query = patients.whereLessThanOrEqualTo(field, val);
			querySnapshot = query.get();
			break;
		case "gt":
			serverLog.append("\nGreater than search...");
			query = patients.whereGreaterThan(field, val);
			querySnapshot = query.get();
			break;
		case "geq":
			serverLog.append("\nGreater than or equal to search...");
			query = patients.whereGreaterThanOrEqualTo(field, val);
			querySnapshot = query.get();
			break;
		case "ne":
			serverLog.append("\nNot equals to search...");
			query = patients.whereNotEqualTo(field, val);
			querySnapshot = query.get();
			break;
		default:
			System.out.println("Invalid query operator");
			return null;
		}
		StringBuilder JSONArrayString = new StringBuilder("[");
		//create patient object from each query result, add to queryRes JSONArrayString
		try {
			for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
				String fName = doc.getString("fName");
				String lName = doc.getString("lName");
				String city = doc.getString("city");
				String state = doc.getString("state");
				String country = doc.getString("country");
				String phone = doc.getString("phone");
				String DOB = doc.getString("DOB");
				Patient curPatient = new Patient(fName, lName, city, state, country, phone, DOB);
				JSONArrayString.append(curPatient);
				JSONArrayString.append(", ");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}	
		
		JSONArrayString.append("]");
		
		serverLog.append("\nSearch succsesful. Returning results...");
		return JSONArrayString.toString();
	}
	
	public String update(String fName, String lName, String field, String val) {
		serverLog.append("\nAttempting patient update...");
		//check if patient record exists
		ApiFuture<QuerySnapshot> query = null;
		CollectionReference patients = db.collection("patients");
		query = patients.whereEqualTo("fName", fName).whereEqualTo("lName", lName).get();
		QuerySnapshot document = null;
		try {
			document = query.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if(document.isEmpty()) {
			serverLog.append("\nPatient does not exist...");
			System.out.println("Server: Record does not exist");
			return "DNE";
		}
		
		DocumentReference docRef = db.collection("patients").document(fName + "_" + lName);
		ApiFuture<WriteResult> future = docRef.update(field, val);
		WriteResult result = null;
		try {
			result = future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		serverLog.append("\nPatient Updated...");
		return fName + " " + lName + "'s " + field + " updated to " + val;
	}
	
	public String delete(String fName, String lName) {
		serverLog.append("\nAttempting patient deletion...");
		//check if patient record exists
		ApiFuture<QuerySnapshot> query = null;
		CollectionReference patients = db.collection("patients");
		query = patients.whereEqualTo("fName", fName).whereEqualTo("lName", lName).get();
		QuerySnapshot document = null;
		try {
			document = query.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if(document.isEmpty()) {
			serverLog.append("\nPatient does not exist...");
			System.out.println("Server: Record does not exist");
			return "Deletion failed";
		}
		
		//if it does, delete it
		ApiFuture<WriteResult> writeResult = db.collection("patients").document(fName + "_" + lName).delete();
		try {
			System.out.println("Update time : " + writeResult.get().getUpdateTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
		serverLog.append("\nPatient deleted");
		return fName + " " + lName + " deleted";
	}
	
	public String aggregate(Integer low, Integer high, String condition) {
		serverLog.append("\nAttempting aggregate search...");
		CollectionReference patients = db.collection("patients");
		Query query = patients.whereGreaterThan("DOB", low.toString()).whereLessThan("DOB", high.toString());
		ApiFuture<QuerySnapshot> querySnapshot = query.get();
		Map<String, Integer> resMap = new HashMap<>();
		
		try {
			for(DocumentSnapshot doc: querySnapshot.get().getDocuments()) {
				String year = doc.getString("DOB").substring(0, 4);
				if(doc.getString("condition").equals(condition)) {
					if(resMap.containsKey(year)) {
						int count = resMap.get(year);
						count++;
						resMap.put(year, count);
					}
					else {
						resMap.put(year, 1);
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		StringBuilder response = new StringBuilder("{ ");		
		for(Integer i = low; i < high; i++) {
			response.append(i);
			response.append(": ");
			if(resMap.containsKey(i.toString())) {
				response.append(resMap.get(i.toString()));
			}
			else {
				response.append("0");
			}
			response.append(", ");
		}
		response.append("}");
		serverLog.append("\nAggregate search succsesful. Returning results...");
		return response.toString();
	}
	
	class HandleAClient implements Runnable{
		private Socket socket; // Connected socket
		private int clientNum;
		
		public HandleAClient(Socket socket, int clientNum) {
			this.socket = socket;
			this.clientNum = clientNum;
		}
		
		public void sendMessage(String message) {
			try {
				DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
				outputToClient.writeUTF(message);
				outputToClient.flush();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			DataInputStream inputFromClient = null;
			DataOutputStream outputToClient = null;
			try {
				// Create input and output streams
				inputFromClient = new DataInputStream(socket.getInputStream());
				outputToClient = new DataOutputStream(socket.getOutputStream());
				
				while (true) {
					try {
					// Receive message from client
					String inMessage = inputFromClient.readUTF();
					serverLog.append("\nMessage Received from Client: " + clientNum);
					//System.out.println("Server: Server received message: " + inMessage);
					String response = processRequest(inMessage);
					//System.out.println("Server: " + response);
					outputToClient.writeUTF(response);
					serverLog.append("\nMessage sent to Client: " + clientNum + "\n");
					}
					catch(IOException e) {
						try {
							inputFromClient.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		// Starts the server and prints to the log
		try {
			this.serverSocket = new ServerSocket(9898); // Create a server socket and attach it to port 9898
			
			// Log the server start
			serverLog.append("\nMedViz server started at " + new Date() +  '\n' + "Listening on port 9898." + '\n');
			
			// Listens for data from clients
			while (true) {
				Socket listenerSocket;
				listenerSocket = serverSocket.accept(); // Accept pending connections from clients
				clientNumber++;// Increment client number
				
				serverLog.append("\nA new client has connected..." + '\n');
				// Log client's host name and IP address
				InetAddress inetAddress = listenerSocket.getInetAddress();
				serverLog.append("Client host name: " + inetAddress.getHostName() + "\n");
				serverLog.append("Client IP: " + inetAddress.getHostAddress() + "\n");
				
				// Create and start a new thread for the connection
				HandleAClient clientHandler = new HandleAClient(listenerSocket, clientNumber);
				Thread clientThread = new Thread(clientHandler);
				clientThread.start();
		        clientList.add(clientHandler);
				
			}
		}
		catch (IOException failedToStart) {
			failedToStart.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		Server server = new Server();
		/*
		String response = server.processRequest("{type: a, low: 1970, high: 2020, condition: brain damage}");
		System.out.println(response);
		*/
		/*
		String response = server.processRequest("{type: s, username: user2, pw: user2}");
		System.out.println(response);	
		response = server.processRequest("{ type: c, fName: Paul, lName: Verhoeven, city: nyc, state: ohio, country: usa, phone: 555-555-5555, condition: diabetes, DOB: 1999-01-01 }");
		System.out.println(response);
		response = server.processRequest("{type: r, field: fName, val: Paul, op: eq}");
		System.out.println(response);
		response = server.processRequest("{type: u, fName: Paul, lName: Verhoeven, field: country, val: ohio}");
		System.out.println(response);
		response = server.processRequest("{type: d, fName: Paul, lName: Verhoeven}");
		System.out.println(response);
		*/
		/*
		String res = server.read("lName", "Hauss", "eq");
		JSONArray ja = new JSONArray(res);
		for(Object jo: ja) {
			System.out.println(((JSONObject) jo).getString("lName"));
		}
		*/
		/*
		Patient pat = new Patient("New", "Patient", "avon", "ct", "usa",
				"555-555-5555", "1989-07-18", "diabetes"
				);
		JSONObject patJSON = pat.getPatientJSON();
		String res = server.create(patJSON);
		System.out.println(res);
		
		res = server.update("New", "Patient", "city", "ohio");
		System.out.println(res);
		
		String response = server.delete("New", "Patient");
		System.out.println(response);
		*/
	}

}