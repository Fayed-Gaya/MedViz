package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Main implements ActionListener{
	// Main Page
	JFrame frame = new JFrame();
	
	// Login Page Variables
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
	
	Main(){
		
		// Initialize Login Page
		paintLoginPage();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(420, 420);
		frame.setLayout(null);
		frame.setVisible(true);
		
		paintLoginPage();
		
	}
	
	private void paintLoginPage(){
		frame.remove(signupButton);
		frame.remove(loginPageButton);
		
		userIDLabel.setText("userID:");
		userIDLabel.setBounds(50, 100, 75, 25);
		userPasswordLabel.setText("password:");
		userPasswordLabel.setBounds(50, 150, 75, 25);
		messageLabel.setBounds(125, 250, 250, 35);
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
	
	@Override
	public void actionPerformed(ActionEvent e) {	
		
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
		
		// Logins in user
		if(e.getSource()==loginButton) {
			
			String userID  = userIDField.getText();
			String password = String.valueOf(userPasswordField.getPassword());
			
			// If user name detected in database
			if (userID.equals("a")) {
				if(password.equals("a")) {
					messageLabel.setForeground(Color.green);
					messageLabel.setText("Login successful");
					frame.dispose();
					WelcomePage welcomePage = new WelcomePage(userID);
					
					
				}
				// Incorrect Password
				else {
					messageLabel.setForeground(Color.red);
					messageLabel.setText("Wrong Password");
				}
			}
			// User does not exist
			else {
				messageLabel.setForeground(Color.red);
				messageLabel.setText("User does not exist");
			}
		}
		
		// 
		if(e.getSource()==signupButton) {
			
			String userID  = userIDField.getText();
			String password = String.valueOf(userPasswordField.getPassword());
			
			// Check to see if user exists
			if (userID.equals("a")) {
				messageLabel.setForeground(Color.red);
				messageLabel.setText("User exists");
			}
			else {
				// Create User
				messageLabel.setForeground(Color.green);
				messageLabel.setText("User Created");
			}
			// If user does not exist create user
		
		}
		
		
	}
	

	public static void main(String[] args) {
		Main gui = new Main();
		
//		IDandPasswords idandPasswords = new IDandPasswords();
//		
//		
//		
//		LoginPage loginPage = new LoginPage(idandPasswords.getLoginInfo());
	}
} 