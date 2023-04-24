package database;
import java.io.IOException;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.HashMap;
import java.util.List;


public class Test {
	public static void main(String[] args) {
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
			
		// asynchronously retrieve all users
		ApiFuture<QuerySnapshot> query = db.collection("users").get();
		// ...
		// query.get() blocks on response
		QuerySnapshot querySnapshot = null;
		try {
			querySnapshot = query.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
		for (QueryDocumentSnapshot document : documents) {
			 System.out.println("User: " + document.getId());
			 System.out.println("First: " + document.getString("first"));
			 if (document.contains("middle")) {
			   System.out.println("Middle: " + document.getString("middle"));
			 }
			 System.out.println("Last: " + document.getString("last"));
			 System.out.println("Born: " + document.getLong("born"));
		}
	}

}
