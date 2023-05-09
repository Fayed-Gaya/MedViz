package backend;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.AggregateQuery;
import com.google.cloud.firestore.AggregateQuerySnapshot;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firestore.v1.Document;

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
		
			
		/*
		CollectionReference collection = db.collection("patients");
		ApiFuture<QuerySnapshot> querySnapshot = collection.orderBy("condition").get();
		QuerySnapshot snapshot = null;
		try {
			snapshot = querySnapshot.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if(snapshot.isEmpty()) {
			System.err.println("Record does not exist");
		}
		int anemia = 0;
		int prediabetes = 0;
		int sinusitis = 0;
		int fracture = 0;
		int cardiac_arrest = 0;
		int bronchitis = 0;
		int sprain = 0;
		int hypertension = 0;
		int brain_damage = 0;
		int diabetes = 0;
		for(DocumentSnapshot doc: snapshot) {
			switch((String)doc.get("condition")) {
			case("anemia"):
				anemia++;
				break;
			case("diabetes"):
				diabetes++;
				break;
			case("prediabetes"):
				prediabetes++;
				break;
			case("sinusitis"):
				sinusitis++;
				break;
			case("fracture"):
				fracture++;
				break;
			case("cardiac arrest"):
				cardiac_arrest++;
				break;
			case("bronchitis"):
				bronchitis++;
				break;
			case("sprain"):
				sprain++;
				break;
			case("hypertension"):
				hypertension++;
				break;
			case("brain damage"):
				brain_damage++;
				break;
			}
		}
		System.out.println("anemia: " + anemia +", diabetes: " + diabetes + ", prediabetes: " + prediabetes + "\nsinusitis: "
				+ sinusitis + ", fracture: " + fracture + ", cardiac arrest: " + cardiac_arrest + "\nbronchitis: "
				+ bronchitis + ", sprain: " + sprain + ", hypertension: " + hypertension + ", brain damage: " + brain_damage);

	*/
	}
	

}
