package database;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

import org.json.JSONObject;

public class PatientDriver {

	public static void main(String[] args) {
		String path = "/Users/dave/Desktop/NYU_Tandon/Spring 2023/Java/final_project/MedViz/Letha284_Haag279_b9a32653-9fde-401f-bb32-9932e680c456.json";
		FileReader fr = null;
		
		try {
			fr = new FileReader(path);
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
		
		System.out.println(sb.toString());
		
		JSONObject jsonObj = new JSONObject(sb.toString());
	}

}
