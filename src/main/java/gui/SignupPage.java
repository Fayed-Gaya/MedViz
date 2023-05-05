package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SignupPage implements ActionListener{
	
	JFrame frame = new JFrame();
	JButton signupButton = new JButton("Sign Up");
	JButton loginPageButton = new JButton("Login Page");
	JTextField userIDField = new JTextField();
	JPasswordField userPasswordField = new JPasswordField();
	JLabel userIDLabel = new JLabel("New user:");
	JLabel userPasswordLabel = new JLabel("New pass:");
	JLabel messageLabel = new JLabel();
	
	HashMap<String, String> logininfo = new HashMap<String, String>();
	
	SignupPage(HashMap<String, String> loginInfoOriginal){
		logininfo = loginInfoOriginal;
		
		userIDLabel.setBounds(50, 100, 75, 25);
		userPasswordLabel.setBounds(50, 150, 75, 25);
		messageLabel.setBounds(125, 250, 250, 35);
		messageLabel.setFont(new Font(null, Font.ITALIC, 25));
		
		userIDField.setBounds(125, 100, 200, 25);
		userPasswordField.setBounds(125, 150, 200, 25);
		
		signupButton.setBounds(125, 200, 100, 25);
		signupButton.addActionListener(this);
		
		loginPageButton.setBounds(225, 200, 100, 25);
		loginPageButton.addActionListener(this);
		
		frame.add(userIDLabel);
		frame.add(userPasswordLabel);
		frame.add(messageLabel);
		frame.add(userIDField);
		frame.add(userPasswordField);
		frame.add(loginPageButton);
		frame.add(signupButton);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(420, 420);
		frame.setLayout(null);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==loginPageButton) {
			frame.dispose();
			LoginPage loginPage = new LoginPage(logininfo);
		}
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
	
}