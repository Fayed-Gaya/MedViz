package database;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

import org.json.JSONArray;
import org.json.JSONObject;

public class PatientDriver {

	public static void main(String[] args) {
		String path = "/Users/dave/Desktop/NYU_Tandon/Spring 2023/Java/final_project/MedViz/Letha284_Haag279_b9a32653-9fde-401f-bb32-9932e680c456.json";
		String path2 = "/Users/dave/Desktop/NYU_Tandon/Spring 2023/Java/final_project/MedViz/Hye44_Turner526_2c60c52c-4cb0-4b3b-b367-9343e4d44b59.json";
		FileReader fr = null;
		
		try {
			fr = new FileReader(path2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		String line;
		StringBuilder sb = new StringBuilder();

		try {
			while((line=br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		JSONObject jsonObj = new JSONObject(sb.toString());
		JSONObject resource = jsonObj.getJSONArray("entry").getJSONObject(0).getJSONObject("resource");
		System.out.println(resource);
		System.out.println();
		System.out.println(resource.getJSONArray("name"));
		
		JSONObject name = resource.getJSONArray("name").getJSONObject(0);
		String given = name.getJSONArray("given").get(0).toString();
		System.out.println(given + " " + name.get("family"));
		System.out.println();
		JSONObject address = resource.getJSONArray("address").getJSONObject(0);
		System.out.println(address);
		System.out.println(
				address.get("city") + ", " 
				+ address.get("state") + ", "
				+ address.get("country")
				);
		System.out.println();
		JSONObject telecom = resource.getJSONArray("telecom").getJSONObject(0);
		System.out.println(telecom);
		System.out.println(telecom.get("value"));




	
		
		
	}

}
