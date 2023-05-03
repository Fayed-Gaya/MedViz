package backend;

import java.io.IOException;
import java.util.ArrayList;
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

	public void processRequest(String req) {
		/*
		 * switch statement based on type handle c/r/u/d/a separately
		 * type c:
		 * 		check numFields, parses JSON and passes argument to appropriate create method
		 * type r:
		 * type u:
		 * type d:
		 */

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

	public ArrayList<Patient> readOneField(String field, String val, String op) {
		ArrayList<Patient> queryRes = new ArrayList<>();
		CollectionReference patients = db.collection("patients");
		Query query = null;
		ApiFuture<QuerySnapshot> querySnapshot = null;

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
				queryRes.add(curPatient);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}	

		return queryRes;
	}

	public static void main(String[] args) {
		ArrayList<Patient> queryRes = new ArrayList<>();
		Server server = new Server();
		Patient dave = new Patient("Dave", "Hauss", "avon", "ct", "usa",
				"555-555-5555", "1989-07-18", "diabetes"
				);
		JSONObject daveJSON = dave.getPatientJSON();
		//server.create(daveJSON);
		queryRes = server.readOneField("DOB", "1980-10", "geq");
		for(Patient patient: queryRes) {
			System.out.println(patient.getDOB());
		}
	}

}