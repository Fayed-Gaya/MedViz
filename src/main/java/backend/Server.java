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

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Query;
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
			firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
			    .setProjectId(PROJECTID)
			    .setCredentials(GoogleCredentials.getApplicationDefault())
			    .build();
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

	public void processRequest(String req) {
		/*
		 * initialize String response = null
		 * switch statement based on type handle c/r/u/d/a separately, each function returns a string
		 * type c:
		 * 		parse JSON and pass arguments to read method 
		 * type r:
		 * 		check numFields, parses JSON and passes argument to appropriate read method. returns string of patient records (JSONArray of JSONObjects)
		 * type u:
		 * type d:
		 */

	}

	public String create(Patient patient) {
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
		if(!document.isEmpty()) {
			System.err.println("Record already exists:");
			try {
				for (DocumentSnapshot doc : query.get().getDocuments()) {
					System.err.println(doc.getId());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		//write new record to DB
		String docName = patient.getfName() + "_" + patient.getlName();
		ApiFuture<WriteResult> future = db.collection("patients").document(docName).set(patient.getPatientMap());
		try {
			System.out.println("Update time : " + future.get().getUpdateTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return patient.toString();
	}

	public String create(JSONObject patient) {
		Patient p = null;
		try {
			p = new Patient(patient);
			this.create(p);
			return p.toString();
		}
		catch(JSONException e){
			System.err.println("Invalid JSON: " + e.getMessage());
			return null;
		}
	}

	public String create(String fName, String lName, String city, String state,
					String country, String phone, String condition, String DOB) {

		Patient patient = new Patient(fName, lName, city, state, country,
					phone, condition, DOB);
		this.create(patient);
		return patient.toString();
	}

	public String readOneField(String field, String val, String op) {
		CollectionReference patients = db.collection("patients");
		Query query = null;
		ApiFuture<QuerySnapshot> querySnapshot = null;

		//read op variable to decide how to handle query
		switch(op) {
		case "eq":
			query = patients.whereEqualTo(field, val);
			querySnapshot = query.get();
			break;
		case "lt":
			query = patients.whereLessThan(field, val);
			querySnapshot = query.get();
			break;
		case "leq":
			query = patients.whereLessThanOrEqualTo(field, val);
			querySnapshot = query.get();
			break;
		case "gt":
			query = patients.whereGreaterThan(field, val);
			querySnapshot = query.get();
			break;
		case "geq":
			query = patients.whereGreaterThanOrEqualTo(field, val);
			querySnapshot = query.get();
			break;
		case "ne":
			query = patients.whereNotEqualTo(field, val);
			querySnapshot = query.get();
			break;
		default:
			System.err.println("Invalid query operator");
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

		return JSONArrayString.toString();
	}
	
	public String delete(String fName, String lName) {
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
			System.err.println("Record does not exist");
			return null;
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
		
		return fName + " " + lName + " deleted";
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
			// TODO Auto-generated method stub
			try {
				// Create input and output streams
				DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
//				DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
				
				while (true) {
					
					// Receive message from client
					String inMessage = clientNum + ":" + inputFromClient.readUTF();
					System.out.println("Server received message: " + inMessage);
					
//					// Broadcast the message
//					broadcastMessage(inMessage);
//					messageBroadCast = new DataOutputStream(outputToClient);
//					messageBroadCast.writeUTF(inMessage);
					
//					outputToClient.writeUTF(inMessage);
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
			
			// Log the server start time
			serverLog.append("MedViz server started at " + new Date() + '\n');
			System.out.println("Server started. Listening on port 9898.");
			
			// Listens for data from clients
			while (true) {
				Socket listenerSocket;
				listenerSocket = serverSocket.accept(); // Accept pending connections from clients
				clientNumber++;// Increment client number
				
				serverLog.append("\nNew Connection!\nStarting thread for client connection" + clientNumber +" at " + new Date() + '\n');
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
		String res = server.readOneField("lName", "Hauss", "eq");
		JSONArray ja = new JSONArray(res);
		for(Object jo: ja) {
			System.out.println(((JSONObject) jo).getString("lName"));
		}
		*/
		
		
		Patient dave = new Patient("KilHwan", "Name", "avon", "ct", "usa",
				"555-555-5555", "1989-07-18", "diabetes"
				);
		JSONObject daveJSON = dave.getPatientJSON();
		String res = server.create(daveJSON);
		System.out.println(res);
		
		
		String response = server.delete("KilHwan", "Name");
		System.out.println(response);
	}

}