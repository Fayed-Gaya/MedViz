package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;


public class Main extends JFrame implements ActionListener, Runnable{
	
	// Server connection setup
		private static int nextClientID = 1;
		private int clientID;
		
		DataOutputStream toServer = null;
		DataInputStream fromServer = null;
		private Socket socket;
		private boolean connectionStatus = false;
	
	// Admin Flag
	private boolean adminFlag = false;
		
		
	// GUI setup
	JFrame frame = new JFrame();
	
	JButton loginButton = new JButton("Login");
	JButton resetButton = new JButton("Reset");
	JButton signupButton = new JButton("Sign Up");
	JButton signupPageButton = new JButton("Sign Up Page");
	JButton loginPageButton = new JButton("Login Page");
	JTextField userIDField = new JTextField();
	JPasswordField userPasswordField = new JPasswordField();
	JLabel userIDLabel = new JLabel();
	JLabel userPasswordLabel = new JLabel();
	JLabel messageLabel = new JLabel();
	
	// Query Page setup
	JButton queryPageButton = new JButton("Main Page");
	JButton logoutButton = new JButton("Logout");
	
	JButton createPatientPageButton = new JButton("Create Patient Page");
	JButton createPatientButton = new JButton("Create Patient");
	
	JButton vizualizerPageButton = new JButton("MedViz Page");
	JButton vizButton = new JButton("Search");
	
	JButton updatePatientPageButton = new JButton("Update Patient Page");
	JButton updatePatientButton = new JButton("Update Patient");
	
	JButton deletePatientPageButton = new JButton("Delete Patient Page");
	JButton deletePatientButton = new JButton("Delete Patient");
	
	// Patient Creation page setup
	JLabel nameLabel = new JLabel("Name:");
	JTextField nameField = new JTextField();
	
	JLabel lastNameLabel = new JLabel("Last Name:");
	JTextField lastNameField = new JTextField();
	
	String[] countries = {"USA", "Canada"};
	JLabel countryLabel = new JLabel("Country:");
    JComboBox<String> countryDropdown = new JComboBox<>(countries);
    
    JLabel stateLabel = new JLabel("State:");
	JTextField stateField = new JTextField();
	
	JLabel cityLabel = new JLabel("City:");
	JTextField cityField = new JTextField();
	
	JLabel phoneLabel = new JLabel("Phone:");
	JTextField phoneField = new JTextField();
	
	JLabel DOBLabel = new JLabel("DOB (YYYY-MM-DD):");
	JTextField DOBField = new JTextField();
	
	String[] conditions = {"prediabetes", "anemia", "sinusitis", "fracture", "cardiac arrest", "bronchitis", "sprain", "hypertension", "brain damage", "diabetes"};
	JLabel conditionLabel = new JLabel("Condition:");
    JComboBox<String> conditionDropdown = new JComboBox<>(conditions);
    
    JLabel fieldsLabel = new JLabel("Field:");
    JLabel fieldsValueLabel = new JLabel("New Value:");
    String[] fields = {"fName", "lName", "country", "state", "city", "phone", "DOB", "condition"};
    JComboBox<String> fieldsDropdown = new JComboBox<>(fields);
    JTextField fieldsValueField = new JTextField();
    
    // MedViz Page Setup
    JLabel lookupLabel = new JLabel("Lookup Field:");
    JLabel valueLabel = new JLabel("Value:");
    JLabel searchConditionLabel = new JLabel("Search Condition:");
    
    String[] fields2 = {"fName", "lName", "country", "state", "city", "condition"};
    JComboBox<String> fieldsDropdown2 = new JComboBox<>(fields2);
    
    JTextField valueField = new JTextField();
    String[] searchConditions = {"eq", "lt", "leq", "gt", "geq", "ne"};
    JComboBox<String> searchConditionDropdown = new JComboBox<>(searchConditions);
    
    
    JLabel startYearLabel = new JLabel("Start Year:");
    JLabel endYearLabel = new JLabel("End Year:");
    
    JTextField startYearField = new JTextField();
    JTextField endYearField = new JTextField();
    
    
    JButton vizButton2 = new JButton("Aggregate Search");
    
    // Visualization page setup
    private JTable resTable;
    Vector<String> myVector = new Vector<String>();
    JScrollPane scrollPane =  new JScrollPane(resTable);
    
    // aggregate search setup
    ChartPanel chartPanel = new ChartPanel(null);
    
	
	Main(){
		super("Med Viz");
		// Initialize client ID
		this.clientID = nextClientID++;
		
		// Create Menu bar
		JMenuBar menuBar = new JMenuBar(); // Create menu bar
		JMenu menu = new JMenu("File"); // Create file menu
		JMenuItem connectItem = new JMenuItem("Reconnect");
		connectItem.addActionListener(new openConnection());
		menu.add(connectItem);
		JMenuItem exitItem = new JMenuItem("Exit"); 
		exitItem.addActionListener((e) -> System.exit(0));
		menu.add(exitItem);
		menuBar.add(menu); // Add File menu to menu bar
		frame.setJMenuBar(menuBar);
		
		// Build results table
		String[] columnNames = {"First Name", "Last Name", "City", "State", "Country", "Phone", "Condition", "DOB"};
		myVector.addAll(Arrays.asList(columnNames));
		
		DefaultTableModel model = new DefaultTableModel(myVector, 0);
	    resTable = new JTable(model);
		
		// Initialize Login Page
		paintLoginPage();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(420, 420);
		frame.setLayout(null);
		frame.setVisible(true);
		
		Thread t = new Thread(this);
		t.start();
	}
	
	private void paintLoginPage(){
		frame.remove(signupButton);
		frame.remove(loginPageButton);
		
		userIDLabel.setText("userID:");
		userIDLabel.setBounds(50, 100, 75, 25);
		userPasswordLabel.setText("password:");
		userPasswordLabel.setBounds(50, 150, 75, 25);
		messageLabel.setBounds(10, 250, 410, 35);
		messageLabel.setFont(new Font(null, Font.ITALIC, 25));
		
		userIDField.setBounds(125, 100, 200, 25);
		userPasswordField.setBounds(125, 150, 200, 25);
		
		loginButton.setBounds(60, 200, 100, 25);
		loginButton.addActionListener(this);
		
		resetButton.setBounds(160, 200, 100, 25);
		resetButton.addActionListener(this);
		
		signupPageButton.setBounds(260, 200, 140, 25);
		signupPageButton.addActionListener(this);
		
		messageLabel.setForeground(Color.blue);
		messageLabel.setText("Login Page");
		
		frame.add(userIDLabel);
		frame.add(userPasswordLabel);
		frame.add(messageLabel);
		frame.add(userIDField);
		frame.add(userPasswordField);
		frame.add(loginButton);
		frame.add(resetButton);
		frame.add(signupPageButton);
		
		frame.repaint();
	}
	
	private void paintSignupPage() {
		frame.remove(loginButton);
		frame.remove(signupPageButton);
		
		userIDLabel.setBounds(50, 100, 75, 25);
		userIDLabel.setText("New User");
		userPasswordLabel.setBounds(50, 150, 75, 25);
		userPasswordLabel.setText("New Pass:");
		
		userIDField.setBounds(125, 100, 200, 25);
		userPasswordField.setBounds(125, 150, 200, 25);
		
		signupButton.setBounds(80, 200, 100, 25);
		signupButton.addActionListener(this);
		
		resetButton.setBounds(180, 200, 100, 25);
		resetButton.addActionListener(this);
		
		loginPageButton.setBounds(280, 200, 100, 25);
		loginPageButton.addActionListener(this);
		
		messageLabel.setBounds(125, 250, 250, 35);
		messageLabel.setFont(new Font(null, Font.ITALIC, 25));
		
		messageLabel.setForeground(Color.orange);
		messageLabel.setText("Sign Up Page");
		
		frame.add(userIDLabel);
		frame.add(userPasswordLabel);
		frame.add(messageLabel);
		frame.add(userIDField);
		frame.add(userPasswordField);
		frame.add(loginPageButton);
		frame.add(signupButton);
		
		frame.repaint();
		
	}
	
	private void paintQueryPage() {
		frame.setSize(420, 420);
		frame.setLayout(null);
		// Remove login page components
		frame.remove(loginButton);
		frame.remove(signupPageButton);
		frame.remove(resetButton);
		frame.remove(userIDLabel);
		frame.remove(userPasswordLabel);
		frame.remove(userIDField);
		frame.remove(userPasswordField);
		frame.remove(queryPageButton);
		frame.remove(nameLabel);
		frame.remove(nameField);
		frame.remove(lastNameLabel);
		frame.remove(lastNameField);
		frame.remove(countryLabel);
		frame.remove(countryDropdown);
		frame.remove(stateLabel);
		frame.remove(stateField);
		frame.remove(cityField);
		frame.remove(cityLabel);
		frame.remove(DOBField);
		frame.remove(DOBLabel);
		frame.remove(conditionDropdown);
		frame.remove(conditionLabel);
		frame.remove(phoneLabel);
		frame.remove(phoneField);
		frame.remove(createPatientButton);
		frame.remove(deletePatientButton);
		frame.remove(fieldsDropdown);
		frame.remove(fieldsLabel);
		frame.remove(fieldsValueField);
		frame.remove(fieldsValueLabel);
		frame.remove(updatePatientButton);
		frame.remove(vizButton);
		frame.remove(lookupLabel);
		frame.remove(fieldsDropdown);
		frame.remove(fieldsDropdown2);
		frame.remove(valueLabel);
		frame.remove(valueField);
		frame.remove(searchConditionLabel);
		frame.remove(searchConditionDropdown);
		frame.remove(startYearLabel);
		frame.remove(startYearField);
		frame.remove(endYearLabel);
		frame.remove(endYearField);
		frame.remove(conditionLabel);
		frame.remove(conditionDropdown);
		frame.remove(vizButton2);
		frame.remove(resTable);
		frame.remove(queryPageButton);
		frame.remove(scrollPane);
		frame.remove(chartPanel);
		
		// Configure message label
		messageLabel.setText("Hello " + userIDField.getText());
		messageLabel.setForeground(Color.blue);
		
		// Configure CRUD Buttons
		createPatientPageButton.setBounds(120, 50, 170, 25);
		createPatientPageButton.addActionListener(this);
		
		vizualizerPageButton.setBounds(120, 100, 170, 25);
		vizualizerPageButton.addActionListener(this);
		
		updatePatientPageButton.setBounds(120, 150, 170, 25);
		updatePatientPageButton.addActionListener(this);
		
		deletePatientPageButton.setBounds(120, 200, 170, 25);
		deletePatientPageButton.addActionListener(this);
		
		// Logout button
		logoutButton.setBounds(5, 330, 100, 25);
		logoutButton.addActionListener(this);
		
		// Add query page components
		
		frame.add(vizualizerPageButton);
		
		if (adminFlag == true) {
			frame.add(updatePatientPageButton);
			frame.add(deletePatientPageButton);
			frame.add(createPatientPageButton);
		}

		frame.add(logoutButton);
		
		// Repaint
		frame.repaint();
		
	}
	
	private void paintCreatePatientPage() {
		// Remove query page components
		frame.remove(createPatientPageButton);
		frame.remove(vizualizerPageButton);
		frame.remove(updatePatientPageButton);
		frame.remove(deletePatientPageButton);
		frame.remove(logoutButton);
		
		// Configure fields
		// name, last name, city, state, country, phone, condition, DOB
		nameLabel.setBounds(10, 10, 100, 25);
		nameField.setBounds(50, 10, 100, 25);
		
		lastNameLabel.setBounds(10, 45, 100, 25);
		lastNameField.setBounds(80, 45, 100, 25);
		
		countryLabel.setBounds(10, 75, 100, 25);
		countryDropdown.setBounds(65, 75, 100, 25);
		
		stateLabel.setBounds(10, 105, 100, 25);
		stateField.setBounds(65, 105, 100, 25);
		
		cityLabel.setBounds(10, 140, 100, 25);
		cityField.setBounds(65, 140, 100, 25);
		
		phoneLabel.setBounds(190, 50, 100, 25);
		phoneField.setBounds(285, 50, 100, 25);
		
		DOBLabel.setBounds(170, 10, 150, 25);
		DOBField.setBounds(310, 10, 100, 25);
		
		conditionLabel.setBounds(190, 90, 100, 25);
		conditionDropdown.setBounds(270, 90, 100, 25);
		
		
		// Configure message label
		messageLabel.setForeground(Color.blue);
		messageLabel.setText("Patient Creation");
		
		// Configure buttons
		queryPageButton.setBounds(270, 200, 100, 25);
		queryPageButton.addActionListener(this);
		
		createPatientButton.setBounds(130, 200, 130, 25);
		createPatientButton.addActionListener(this);
		
		// Add patient page components
		frame.add(queryPageButton);
		frame.add(createPatientButton);
		frame.add(nameLabel);
		frame.add(nameField);
		frame.add(lastNameField);
		frame.add(lastNameLabel);
		frame.add(countryLabel);
		frame.add(countryDropdown);
		frame.add(cityLabel);
		frame.add(stateLabel);
		frame.add(stateField);
		frame.add(cityField);
		frame.add(phoneField);
		frame.add(phoneLabel);
		frame.add(DOBField);
		frame.add(DOBLabel);
		frame.add(conditionDropdown);
		frame.add(conditionLabel);
		
		// Repaint
		frame.repaint();
		
	}
	
	private void paintMedVizPage() {
		frame.setSize(575, 420);
		
		// Remove query page components
		frame.remove(createPatientPageButton);
		frame.remove(vizualizerPageButton);
		frame.remove(updatePatientPageButton);
		frame.remove(deletePatientPageButton);
		frame.remove(logoutButton);
		
		// Configure fields
		lookupLabel.setBounds(5, 5, 100, 25);
		fieldsDropdown2.setBounds(90, 5, 100, 25);
		
		valueLabel.setBounds(200, 5, 100, 25);
		valueField.setBounds(240, 5, 80, 25);
		
		searchConditionLabel.setBounds(325, 5, 120, 25);
		searchConditionDropdown.setBounds(445, 5, 100, 25);
		
		startYearLabel.setBounds(5, 100, 100, 25);
		startYearField.setBounds(70, 100, 100, 25);
		
		endYearLabel.setBounds(170, 100, 100, 25);
		endYearField.setBounds(230, 100, 100, 25);
		
		conditionLabel.setBounds(340, 100, 100, 25);
		conditionDropdown.setBounds(405, 100, 150, 25);
		
		// Configure message label
		messageLabel.setForeground(Color.blue);
		messageLabel.setText("MedViz");
		
		// Configure buttons
		queryPageButton.setBounds(300, 300, 100, 25);
		queryPageButton.addActionListener(this);
		
		vizButton.setBounds(225, 57, 100, 25);
		vizButton.addActionListener(this);
		
		vizButton2.setBounds(213, 140, 160, 25);
		vizButton2.addActionListener(this);
		
		// Add patient page components
		frame.add(queryPageButton);
		frame.add(vizButton);
		frame.add(lookupLabel);
		frame.add(fieldsDropdown2);
		frame.add(valueLabel);
		frame.add(valueField);
		frame.add(searchConditionLabel);
		frame.add(searchConditionDropdown);
		frame.add(startYearLabel);
		frame.add(startYearField);
		frame.add(endYearLabel);
		frame.add(endYearField);
		frame.add(conditionLabel);
		frame.add(conditionDropdown);
		frame.add(vizButton2);
		frame.add(messageLabel);
		
		
		// Repaint
		frame.repaint();
		
	}
	
	private void paintSearchResults() {
		frame.setLayout(new BorderLayout());
		
		frame.setSize(800, 800);
		// remove medViz Components
		frame.remove(queryPageButton);
		frame.remove(vizButton);
		frame.remove(lookupLabel);
		frame.remove(fieldsDropdown2);
		frame.remove(valueLabel);
		frame.remove(valueField);
		frame.remove(searchConditionLabel);
		frame.remove(searchConditionDropdown);
		frame.remove(startYearLabel);
		frame.remove(startYearField);
		frame.remove(endYearLabel);
		frame.remove(endYearField);
		frame.remove(conditionLabel);
		frame.remove(conditionDropdown);
		frame.remove(vizButton2);
		frame.remove(messageLabel);
	    
		// Configure resTabel
		resTable.setBounds(0, 0, 800, 725);
		
	    // Create the scroll pane and add the table to it
		scrollPane = new JScrollPane(resTable);
	    
		// Configure buttons
		queryPageButton.setBounds(400, 730, 100, 25);
		queryPageButton.addActionListener(this);
		
	    // Add new components
		frame.add(queryPageButton, BorderLayout.SOUTH);
		frame.add(scrollPane, BorderLayout.CENTER);
	    
		// Repaint
		frame.repaint();
	}
	
	private void paintAggregateSearchResults() {
		frame.setLayout(new BorderLayout());
		frame.setSize(800, 800);
		// remove medViz Components
		frame.remove(queryPageButton);
		frame.remove(vizButton);
		frame.remove(lookupLabel);
		frame.remove(fieldsDropdown2);
		frame.remove(valueLabel);
		frame.remove(valueField);
		frame.remove(searchConditionLabel);
		frame.remove(searchConditionDropdown);
		frame.remove(startYearLabel);
		frame.remove(startYearField);
		frame.remove(endYearLabel);
		frame.remove(endYearField);
		frame.remove(conditionLabel);
		frame.remove(conditionDropdown);
		frame.remove(vizButton2);
		frame.remove(messageLabel);


	    
		// Configure buttons
		queryPageButton.setBounds(400, 730, 100, 25);
		queryPageButton.addActionListener(this);
		
	    // Add new components
		frame.add(chartPanel, BorderLayout.CENTER);
		frame.add(queryPageButton, BorderLayout.SOUTH);
	    
		// Repaint
		frame.repaint();
		
		
	}
	
	public void populateTable(String jsonArrayString) throws JSONException {
		    // Clear the existing data in the table
		    DefaultTableModel model = (DefaultTableModel)resTable.getModel();
		    model.setRowCount(0);

		    // Parse the JSON array string and add the rows to the table
		    JSONArray jsonArray = new JSONArray(jsonArrayString);
		    for (int i = 0; i < jsonArray.length(); i++) {
		      JSONObject jsonObject = jsonArray.getJSONObject(i);
		      String[] row = {
		        jsonObject.getString("fName"),
		        jsonObject.getString("lName"),
		        jsonObject.getString("city"),
		        jsonObject.getString("state"),
		        jsonObject.getString("country"),
		        jsonObject.getString("phone"),
		        jsonObject.getString("condition"),
		        jsonObject.get("DOB").toString()
		      };
		      model.addRow(row);
		    }
		  }

	public void createBarGraph(String data, String title) {
	    // Parse the key-value pairs from the string into a TreeMap
	    TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
	    String[] pairs = data.split(",");
	    for (String pair : pairs) {
	        String[] parts = pair.split(":");
	        int year = Integer.parseInt(parts[0].trim());
	        int count = Integer.parseInt(parts[1].trim());
	        map.put(year, count);
	    }

	    // Create a dataset from the TreeMap
	    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	    for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
	        dataset.addValue(entry.getValue(), "Count", entry.getKey());
	    }

	    // Create the chart
	    JFreeChart chart = ChartFactory.createBarChart(title, "Year", "Count",
	            dataset, PlotOrientation.VERTICAL, false, true, false);

	    // Set the chart background color
	    chart.setBackgroundPaint(Color.WHITE);

	    // Get the plot of the chart
	    CategoryPlot plot = chart.getCategoryPlot();

	    // Set the background color of the plot
	    plot.setBackgroundPaint(Color.WHITE);

	    // Set the range axis label font
	    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	    rangeAxis.setLabelFont(rangeAxis.getLabelFont().deriveFont(16f));

	    // Set the domain axis label font
	    CategoryAxis domainAxis = plot.getDomainAxis();
	    domainAxis.setLabelFont(domainAxis.getLabelFont().deriveFont(16f));

	    // Set the bar renderer
	    BarRenderer renderer = (BarRenderer) plot.getRenderer();
	    renderer.setDrawBarOutline(false);
	    renderer.setBaseItemLabelGenerator(null);
	    renderer.setBaseItemLabelsVisible(true);

	    // Create a new frame to display the chart
	    JFrame frame = new JFrame(title);
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	    // Create a chart panel and add it to the frame
	    chartPanel = new ChartPanel(chart);
	    chartPanel.setPreferredSize(new java.awt.Dimension(1000, 500));
	    
	}
	
	private void paintUpdatePatientPage() {
		// Remove query page components
		frame.remove(createPatientPageButton);
		frame.remove(vizualizerPageButton);
		frame.remove(updatePatientPageButton);
		frame.remove(deletePatientPageButton);
		frame.remove(logoutButton);
		
		// Configure fields
		queryPageButton.setBounds(270, 200, 100, 25);
		queryPageButton.addActionListener(this);
		
		nameLabel.setBounds(10, 10, 100, 25);
		nameField.setBounds(50, 10, 100, 25);
		
		lastNameLabel.setBounds(10, 45, 100, 25);
		lastNameField.setBounds(80, 45, 100, 25);
		
		fieldsLabel.setBounds(10, 90, 150, 25);
		fieldsDropdown.setBounds(45, 90, 100, 25);
		fieldsValueLabel.setBounds(145, 90, 100, 25);
		fieldsValueField.setBounds(220, 90, 100, 25);
		
		
		// Configure message label
		messageLabel.setForeground(Color.blue);
		messageLabel.setText("Update Patients");
		
		// Configure buttons
		queryPageButton.setBounds(270, 200, 100, 25);
		queryPageButton.addActionListener(this);
		
		updatePatientButton.setBounds(10, 155, 135, 25);
		updatePatientButton.addActionListener(this);
		
		// Add patient page components
		frame.add(queryPageButton);
		frame.add(nameLabel);
		frame.add(nameField);
		frame.add(lastNameField);
		frame.add(lastNameLabel);
		frame.add(fieldsLabel);
		frame.add(fieldsDropdown);
		frame.add(fieldsValueField);
		frame.add(fieldsValueLabel);
		frame.add(updatePatientButton);
		
		// Repaint
		frame.repaint();
		
	}
	
	
	private void paintDeletePatientPage() {
		// Remove query page components
		frame.remove(createPatientPageButton);
		frame.remove(vizualizerPageButton);
		frame.remove(updatePatientPageButton);
		frame.remove(deletePatientPageButton);
		frame.remove(logoutButton);
		
		// Configure fields
		queryPageButton.setBounds(270, 200, 100, 25);
		queryPageButton.addActionListener(this);
		
		
		nameLabel.setBounds(10, 10, 100, 25);
		nameField.setBounds(50, 10, 100, 25);
		
		lastNameLabel.setBounds(10, 45, 100, 25);
		lastNameField.setBounds(80, 45, 100, 25);
		
		// Configure message label
		messageLabel.setForeground(Color.blue);
		messageLabel.setText("Delete Patients");
		
		// Configure buttons
		deletePatientButton.setBounds(10, 100, 150, 25);
		deletePatientButton.addActionListener(this);
		
		// Add patient page components
		frame.add(queryPageButton);
		frame.add(deletePatientButton);
		frame.add(nameLabel);
		frame.add(nameField);
		frame.add(lastNameField);
		frame.add(lastNameLabel);
		
		// Repaint
		frame.repaint();
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {	
		
		// Paints sign-up page
		if(e.getSource()==logoutButton) {
			System.exit(0);
		}
		
		// Resets text entry
		if(e.getSource()==resetButton) {
			userIDField.setText("");
			userPasswordField.setText("");
		}
		
		// Paints sign-up page
		if(e.getSource()==signupPageButton) {
			paintSignupPage();
		}
		
		// Paints login page
		if(e.getSource()==loginPageButton) {
			paintLoginPage();
		}
		
		// Paints login page
		if(e.getSource()==queryPageButton) {
			paintQueryPage();
		}
		
		// Paints create patient page
		if(e.getSource()==createPatientPageButton) {
			paintCreatePatientPage();
		}

		// Paints MedViz page
		if(e.getSource()==vizualizerPageButton) {
			paintMedVizPage();
		}

		// Paints update patient page
		if(e.getSource()==updatePatientPageButton) {
			paintUpdatePatientPage();
		}

		// Paints delete page
		if(e.getSource()==deletePatientPageButton) {
			paintDeletePatientPage();
		}
		
		// Logins in user
		if(e.getSource()==loginButton) {
			System.out.println("Client: Attempting login");
			
			String userID  = userIDField.getText();
			String password = String.valueOf(userPasswordField.getPassword());
			
			if (userID.isEmpty() || password.isEmpty()) {
				System.out.println("Client: Empty Credentials");
				messageLabel.setForeground(Color.red);
				messageLabel.setText("Enter User and Pass!");
			} else {
				//format and send userID and pw to server, for now just prints out received message
				try {
					toServer.writeUTF("{ type: l, username: " + userID + ", pw: " + password + " }");
					String inMessage = fromServer.readUTF();
					System.out.println("Client:" + inMessage);
					
					JSONObject responseJSON = new JSONObject(inMessage);
					
					if (responseJSON.get("verified").toString().equals("true")) {
						if (responseJSON.get("admin").toString().equals("true")) {
							adminFlag = true;
						}
						System.out.println("Login Successful");
						
						messageLabel.setForeground(Color.green);
						messageLabel.setText("Client: Login successful");
						paintQueryPage();
					}
					else {
						System.out.println("Client: Login Failed");
						messageLabel.setForeground(Color.red);
						messageLabel.setText("Login Failed");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			
			}
		}
		
		// Signs up user
		if(e.getSource()==signupButton) {
			System.out.println("Client: Attempting sign-up");
			
			// Collect new user credentials
			String newUserID  = userIDField.getText();
			String newpassword = String.valueOf(userPasswordField.getPassword());
			
			if (newUserID.isEmpty() || newpassword.isEmpty()) {
				System.out.println("Client: Empty Credentials");
				messageLabel.setForeground(Color.red);
				messageLabel.setText("Enter User and Pass!");
			} else {	
				// Send credentials to server
				try {
					toServer.writeUTF("{ type: s, username: " + newUserID + ", pw: " + newpassword + " }");
					String inMessage = fromServer.readUTF();
					System.out.println("Client:" + inMessage);
					
					// User already exists
					if (inMessage.equals("Username Exists")) {
						messageLabel.setForeground(Color.red);
						messageLabel.setText("Username Taken");
					}
					// Creates new user
					else {
						System.out.println("Client: New user - " + newUserID + " created");
						messageLabel.setForeground(Color.green);
						messageLabel.setText("User Created");
						
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		
			}
		}
		
		// Creates new patient
		if(e.getSource()==createPatientButton) {
			System.out.println("Client: Attempting patient Creation");
			
			// Collect new user credentials
			String fname  = nameField.getText();
			String lname = lastNameField.getText();
			String country = countryDropdown.getSelectedItem().toString();
			String city = cityField.getText();
			String state = stateField.getText();
			String phone = phoneField.getText();
			String condition = conditionDropdown.getSelectedItem().toString();
			String DOB = DOBField.getText();
			
			// Could add an if else here to do input validation
			// Send patient creation data to server
			try {
				toServer.writeUTF("{ type: c, fName: " + fname + ", lName: " + lname + ", city: " + city + ", state: " + state + ", country: " + country + ", phone: " + "a" + phone + ", condition: " + condition + ", DOB: " + DOB + " }");
				String inMessage = fromServer.readUTF();
				System.out.println("Client:" + inMessage);
				
				// User already exists
				if (inMessage.equals("Exists")) {
					messageLabel.setForeground(Color.red);
					messageLabel.setText("Patient Exists");
				}
				// Creates new user
				else {
					System.out.println("Client: Created Patient - " + fname);
					messageLabel.setForeground(Color.green);
					messageLabel.setText("Patient Created");
					
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		// Deletes patient
		if(e.getSource()==deletePatientButton) {
			System.out.println("Client: Attempting patient deletion");
			
			// Collect user credentials
			String fname  = nameField.getText();
			String lname = lastNameField.getText();
			
			// Could add an if else here to do input validation
			// Send patient creation data to server
			try {
				toServer.writeUTF("{ type: d, fName: " + fname + ", lName: " + lname + " }");
				String inMessage = fromServer.readUTF();
				System.out.println("Client:" + inMessage);
				
				// User already exists
				if (inMessage.equals("Deletion failed")) {
					messageLabel.setForeground(Color.red);
					messageLabel.setText("Patient does not exist");
				}
				// Creates new user
				else {
					System.out.println("Client: patient deleted - " + fname);
					messageLabel.setForeground(Color.green);
					messageLabel.setText("Patient deleted");
					
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		// Updates patient
		if(e.getSource()==updatePatientButton) {
			System.out.println("Client: Attempting patient update");
			
			// Collect user credentials
			String fname  = nameField.getText();
			String lname = lastNameField.getText();
			String field = fieldsDropdown.getSelectedItem().toString();
			String val = fieldsValueField.getText();
			
			if (fname.isEmpty() || lname.isEmpty() || field.isEmpty() || val.isEmpty()) {
				System.out.println("Client: Empty Values");
				messageLabel.setForeground(Color.red);
				messageLabel.setText("Enter all fields");
			}
			else {
				try {
					toServer.writeUTF("{ type: u, fName: " + fname + ", lName: " + lname + ", field: " + field + ", val: " + val + " }");
					String inMessage = fromServer.readUTF();
					System.out.println("Client:" + inMessage);
					
					// User does not exist
					if (inMessage.equals("DNE")) {
						messageLabel.setForeground(Color.red);
						messageLabel.setText("Patient does not exist");
					}
					// Patient Updated
					else {
						messageLabel.setForeground(Color.green);
						messageLabel.setText("Patient updated");
						
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		}
		
		// Execute read
		if(e.getSource()==vizButton) {
			
			String field = fieldsDropdown2.getSelectedItem().toString();
			String value = valueField.getText();
			String searchCon = searchConditionDropdown.getSelectedItem().toString();
			
			if (field.isEmpty() || value.isEmpty() || searchCon.isEmpty()) {
				System.out.println("Client: viz1 Empty values entered.");
				messageLabel.setForeground(Color.red);
				messageLabel.setText("Enter all fields for search");
			}
			else {
				try {
					toServer.writeUTF("{ type: r, field: " + field + ", val: " + value + ", op: " + searchCon + " }");
					String inMessage = fromServer.readUTF();
					
					// No records or incorrect entry
					if (inMessage.equals("[]") || inMessage.equals(" []")) {
						messageLabel.setForeground(Color.red);
						messageLabel.setText("No Results");
					}
					// Patient Updated
					else {
						System.out.println("Client: " + inMessage);
						messageLabel.setForeground(Color.green);
						messageLabel.setText("Results found");
						
						populateTable(inMessage);
						paintSearchResults();
						
						
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		}
		
		// Execute aggregate
		if(e.getSource()==vizButton2) {
			
			String startYear = startYearField.getText();
			String endYear = endYearField.getText();
			String con = conditionDropdown.getSelectedItem().toString();

			
			if (startYear.isEmpty() || endYear.isEmpty() || con.isEmpty()) {
				System.out.println("Client: viz2 Empty values entered.");
				messageLabel.setForeground(Color.red);
				messageLabel.setText("Enter all fields for aggregate search");
			}
			else {
				try {
					toServer.writeUTF("{ type: a, low: " + startYear + ", high: " + endYear + ", condition: " + con + " }");
					String inMessage = fromServer.readUTF();
					
					// No records or incorrect entry
					if (inMessage.equals("[]") || inMessage.equals(" []")) {
						messageLabel.setForeground(Color.red);
						messageLabel.setText("No Results");
					}
					// Patient Updated
					else {
						System.out.println("Client: " + inMessage);
						messageLabel.setForeground(Color.green);
						messageLabel.setText("Results found");
						
						String graphString = inMessage.substring(2);
						graphString = graphString.substring(0, graphString.length() - 3);
						
						createBarGraph(graphString, con);
						paintAggregateSearchResults();
						
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		}

	}
	
	class openConnection implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				socket = new Socket("localhost", 9898);
//				chatHistory.append("\nReconnected to server.");
				
				connectionStatus = true;
				
			}
			catch (IOException error1) {
				error1.printStackTrace();
			}
		}
		
	}
	
	public void run() {
		
		try {
			socket = new Socket("localhost", 9898);
			System.out.println("Client: Connected to server.");
			
			// Create an output stream to write to the server
			toServer = new DataOutputStream(socket.getOutputStream());
			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());
			
			connectionStatus = true;
			
			while (connectionStatus) {
				
			}
		} 
		catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		Main gui = new Main();
	}
} 