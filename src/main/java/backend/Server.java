package backend;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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

public class Server {
	private Firestore db = null;
	private final String PROJECTID = "medviz-384013";

	public Server() {
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
	}
	
	public void create(Patient patient) {
		String docName = patient.getfName() + "_" + patient.getlName();
		ApiFuture<WriteResult> future = db.collection("patients").document(docName).set(patient.getPatientMap());
		try {
			System.out.println("Update time : " + future.get().getUpdateTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
	}
	
	public void create(JSONObject patient) {
		Patient p = null;
		try {
			p = new Patient(patient);
			this.create(p);
		}
		catch(JSONException e){
			System.err.println("Invalid JSON: " + e.getMessage());
		}
	}
	
	public void create(String fName, String lName, String city, String state,
					String country, String phone, String condition, String DOB) {
		
		Patient patient = new Patient(fName, lName, city, state, country,
					phone, condition, DOB);
		this.create(patient);
	}
	
	public void read(String arg) {
		CollectionReference patients = db.collection("patients");
		Query query = patients.whereEqualTo("lName", arg);
		ApiFuture<QuerySnapshot> querySnapshot = query.get();
		

		try {
			for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
				System.out.println(document.getId());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		Patient dave = new Patient("Dave", "Hauss", "avon", "ct", "usa", "555-555-5555", "1989-07-18", "diabetes");
		JSONObject daveJSON = dave.getPatientJSON();
		server.create(daveJSON);
		server.read("hauss");
	}

}
