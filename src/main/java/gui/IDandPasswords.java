package gui;

import java.util.HashMap;

public class IDandPasswords {
	
	HashMap<String, String> logininfo = new HashMap<String, String>();
	
	IDandPasswords(){
		logininfo.put("user1", "pass");
	}
	
	protected HashMap getLoginInfo(){
		return logininfo;
	}
}