package backend;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class Patient {
	private static final String[] CONDITIONS = {
			"prediabetes", "anemia", "sinusitis", "fracture", "cardiac arrest",
			"bronchitis", "sprain", "hypertension", "brain damage", "diabetes"
			};

	public Patient(String fName, String lName, String city, String state,
					String country, String phone) {
		Random rand = new Random();
		int conditionIndex = rand.nextInt(10);

		this.setfName(fName);
		this.setlName(lName);
		this.setCity(city);
		this.setState(state);
		this.setCountry(country);
		this.setPhone(phone);
		this.setCondition(CONDITIONS[conditionIndex]);
	}

	public Patient(JSONObject patient) throws JSONException{
			this.setfName(patient.getString("fName"));
			this.setlName(patient.getString("lName"));
			this.setCity(patient.getString("city"));
			this.setState(patient.getString("state"));
			this.setCountry(patient.getString("country"));
			this.setPhone(patient.getString("phone"));
			this.setCondition(patient.getString("condition"));
	}

	public JSONObject getPatientJSON() {
		return new JSONObject(this.toString());
	}

	public Map<String, String> getPatientMap(){
		Map<String, String> patientMap = new HashMap<>();
		patientMap.put("fName", getfName());
		patientMap.put("lName", getlName());
		patientMap.put("city", getCity());
		patientMap.put("state", getState());
		patientMap.put("country", getCountry());
		patientMap.put("phone", getPhone());
		patientMap.put("condition", getCondition());

		return patientMap;
	}

	@Override
	public String toString() {
		return "{ fName: " + fName + " , lName: " + lName + " , city: " + city + " , state: " +
				state + "," + " country: " + country + ", phone: " + phone +
				", condition: " + condition + " }";
	}

	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getlName() {
		return lName;
	}
	public void setlName(String lName) {
		this.lName = lName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	private String fName;
	private String lName;
	private String city;
	private String state;
	private String country;
	private String phone;
	private String condition;
	
	public static void main(String[] args) {
		
		Patient p = new Patient("Hye44", "Turner526", "Saugus", "Massachusetts", "US", "555-533-6976");
		System.out.println(p);
		JSONObject pJson = p.getPatientJSON();
		System.out.println(pJson.getString("condition"));
		Patient pCopy = new Patient(pJson);
		System.out.println(pCopy);
		System.out.println(pCopy.getPatientMap().get("fName"));

	}
}
