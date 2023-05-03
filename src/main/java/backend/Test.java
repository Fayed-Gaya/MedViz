package backend;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.AggregateQuerySnapshot;
import com.google.cloud.firestore.CollectionReference;
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
		FileInputStream refreshToken = null;
		String path = "/Users/dave/Desktop/NYU_Tandon/Spring 2023/Java/Final Project/dev key/medviz-384013-519ee7657744.json";
		try {
			refreshToken = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Firestore db = null;
		FirestoreOptions firestoreOptions = null;
		try {
			firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
			    .setProjectId(projectId)
			    .setCredentials(GoogleCredentials.fromStream(refreshToken))
			    .build();
		} catch (IOException e) {
			e.printStackTrace();
		}
			db = firestoreOptions.getService();
		
		CollectionReference collection = db.collection("patients");
		AggregateQuerySnapshot snapshot = null;
		try {
			snapshot = collection.count().get().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("Count: " + snapshot.getCount());

	}

}
