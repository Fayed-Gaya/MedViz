package database;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

public class Test {
	public static void main(String[] args) {
		String projectId = "medviz-384013";
		/*
	      Map<String, String> env = System.getenv();
	        for (String envName : env.keySet()) {
	            System.out.format("%s=%s%n", envName, env.get(envName));
	        }
	        */
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
	}

}
