package backend;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.io.BufferedReader;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;

public class PatientDirReader {

	public static void main(String[] args) {
		String dirPath = "./EHRs/";		
		Path dir = Paths.get(dirPath);
		
		//open Firestore connection
		String projectId = "medviz-384013";

		Firestore db = null;
		FirestoreOptions firestoreOptions = null;
		try {
			firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
			    .setProjectId(projectId)
			    .setCredentials(GoogleCredentials.getApplicationDefault())
			    .build();
		} catch (IOException e) {
			e.printStackTrace();
		}
			db = firestoreOptions.getService();
		
		//read in all patient JSONs from EHRs directory, parse info and create patient objects to insert into DB
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json")) {
		    for (Path p : stream) {
				BufferedReader br = null;
				String line;
				StringBuilder sb = new StringBuilder();
				Patient patient;
				//read in JSON file, convert to string to initialize JSONObject
				try {
					br = Files.newBufferedReader(p);
					while((line=br.readLine()) != null) {
						sb.append(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			
				//parse patient info
				JSONObject jsonObj = new JSONObject(sb.toString());
				JSONObject resource = jsonObj.getJSONArray("entry").getJSONObject(0).getJSONObject("resource");
				JSONObject name = resource.getJSONArray("name").getJSONObject(0);
				String given = name.getJSONArray("given").get(0).toString();
				String family = name.getString("family");
				
				JSONObject address = resource.getJSONArray("address").getJSONObject(0);
				String city = address.getString("city");
				String state = address.getString("state");
				String country = address.getString("country");

				JSONObject telecom = resource.getJSONArray("telecom").getJSONObject(0);
				String phone = telecom.getString("value");
				
				patient = new Patient(given, family, city, state, country, phone);
				
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

		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
