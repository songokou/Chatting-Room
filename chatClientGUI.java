//package chat;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.Math.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class chatClientGUI extends chatClient
{
	String currentUser;
	int sendToAllButtonClicked;	//flag for the send button being pressed
	int specificSendButtonClicked;
	private Color color = Color.LIGHT_GRAY;
	String messageToSend;	//holder for the message entered into the text field

	JFrame frame;	//frame for the gui

	JMenuBar topBar = new JMenuBar();	//top bar for the menu

	JMenu fileMenu = new JMenu("File");	//our two menus
	
	JMenu customizeMenu = new JMenu("Customize");	//customize option
	

	JMenu usersMenu = new JMenu("Who's Online?");

	JMenuItem showUsersButton = new JMenuItem("Who's Online?");
	
	JMenuItem colorCustom = new JMenuItem("Colors");//chose color
	
	JMenuItem pickColor = new JMenuItem("Colors");

	ArrayList<JMenuItem> userMenuNames = new ArrayList<JMenuItem>();

	ArrayList<String> listOfUsersOnline = new ArrayList<String>();

	JMenu helpMenu = new JMenu("Help");
	
	
	JMenuItem quitButton = new JMenuItem("Quit");	//menu items for our menus
	JMenuItem aboutButton = new JMenuItem("About");

	JOptionPane aboutDialog = new JOptionPane();	//option panes for help-> and file->quit
	JOptionPane usersDialog = new JOptionPane();
	JOptionPane confirmation = new JOptionPane();

	JPanel gui;	//jpanel for our items on the gui

	JPanel messagesPanel;	//sub-panels for our text box, button, field etc.
	JPanel textPanel;
	JPanel sendPanel;	

	JTextArea messagesField;	//text area for printing out the messages
	JTextArea onlineUsersField;
	
	
	JTextField field;	//text field for getting user input
	JTextField recipientsField;

	JButton sendMessageToAllButton = new JButton("Send To All");	//button to send a message
	JButton specificSendMessageButton = new JButton("Send To: ");
	
	JButton clearMessagesButton = new JButton("Clear Message Log");

		String aboutText = "Awesome Software Programmed by Anthony Manetti, Ze Li, Qian Wang, and Plaimanus Lueondee";
		String usersText = "";
		//this string is what appears on the help->about dialog



	/* Method that is called when a user selects file->quit.  Asks for confirmation in a dialog */
	public void getConfirmation(){
		int selectedOption = confirmation.showConfirmDialog(null, 
                                  "Are You Sure ?", 
                                  "Are you sure?", 
                                  confirmation.YES_NO_OPTION); 

		if (selectedOption == confirmation.YES_OPTION) {
    			quit();
		}
	}

	//quit the program
	public void quit(){
		System.exit(0);
	}


	//reveal the about dialog
	public void showAboutFrame()
	{
		aboutDialog.showMessageDialog(frame, aboutText);
	}

	public void showOnlineUsers()
	{
		usersDialog.showMessageDialog(frame, usersText);
	}

	//print a message into the gui
	public void printMessage(String s)
	{
		if(s.length() > 11 && s.substring(0,11).equalsIgnoreCase("clientlist_"))
		{
			System.out.println("foo");
		}
		else{
		s += "\n";
		messagesField.append(s);}
	}



	/* Initialize our gui and add the required elements*/
	public void createFrame(String s)
	{
		frame = new JFrame("Awesome Computer Chat Client (You are " + s + ")");	//set the frame
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		gui = new JPanel(new BorderLayout(3,3));	//create our main panel in a border layout for three items
		gui.setBackground(new Color(255,100,100));		//background color is green because it's my favorit color c:
		gui.setBorder(new EmptyBorder(10,10,10,10));	//put a border around all the items so it looks nice
		
		
		
		messagesPanel = new JPanel();	//new panel
		messagesPanel.setPreferredSize(new Dimension(900,340));	//set the dimensions of this panel
		gui.add(messagesPanel, BorderLayout.NORTH);	//add it to the main panel


		
		ImageIcon chatter =new ImageIcon("chatter.png");
	    JLabel chatpic = new JLabel();
	    chatpic.setIcon(chatter);
	    ImageIcon mg = new ImageIcon ("chat2.png");
		JLabel msg = new JLabel(mg);
		
	    
		textPanel = new JPanel();		//...and doing the same for our other panels...
	    textPanel.add(chatpic);
	    textPanel.add(msg);
		textPanel.setPreferredSize(new Dimension(300,70));
		
		gui.add(textPanel, BorderLayout.CENTER);

		sendPanel = new JPanel();		//...we now have panels for our content
		sendPanel.setPreferredSize(new Dimension(300,100));
		gui.add(sendPanel, BorderLayout.SOUTH);		



		field = new JTextField(20);	//create a text field for getting chat messages

		field.addActionListener(new ActionListener(){	//action listener for enter key pressing
			public void actionPerformed(ActionEvent e){
			if(recipientsField.getText().equals(""))
				sendToAllButtonClicked = 1;
			else	
				specificSendButtonClicked = 1;
			}});
			
		recipientsField = new JTextField(30);
		
		recipientsField.addActionListener(new ActionListener(){	//action listener for enter key pressing
			public void actionPerformed(ActionEvent e){
			if(!(recipientsField.getText().equals("")) && !(field.getText().equals("")))
				specificSendButtonClicked = 1;
			}});
		

		String initialString = "Message Log\n==============\n";	//initialize our messages area with this string
		
		
		messagesField = new JTextArea(initialString, 17, 48);	//create the text area
		onlineUsersField = new JTextArea("Users Online\n============\n",17,20);
		
	
		messagesField.setEditable(false);	//set it to non-editable
		onlineUsersField.setEditable(false);
		

		JScrollPane fieldScroller = new JScrollPane(messagesField, 
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);	//trying to get scrolling done

		JScrollPane usersScroller = new JScrollPane(onlineUsersField, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	
			
		topBar.add(fileMenu);	//add our menus to the toolbar, and add the items.  Don't forgot to add action listeners!

			fileMenu.add(showUsersButton);
			showUsersButton.addActionListener(new MouseClickHandler());

			fileMenu.add(quitButton);
			quitButton.addActionListener(new MouseClickHandler());
			
	   topBar.add(customizeMenu);
	       	customizeMenu.add(pickColor);
	       	
	      pickColor.addActionListener(
	    		  new ActionListener(){
	    			  public void actionPerformed (ActionEvent event){
	    				  
	    				  color = JColorChooser.showDialog(null, "choose a color",color );
	    				  if (color == null)
	    					  color = Color.ORANGE;
	    				  
	    				 gui.setBackground(color);
	    					  
	    			  }
	    		  }
             
		);
		//topBar.add(usersMenu);	
			
		topBar.add(helpMenu);
			helpMenu.add(aboutButton);
			aboutButton.addActionListener(new MouseClickHandler());
			
		//topBar.add(showUsersButton);
			//showUsersButton.addActionListener(new MouseClickHandler());

		frame.setJMenuBar(topBar);		//set the menu bar

		

		messagesPanel.add(fieldScroller);	//add the corresponding text areas/buttons to the panels
		messagesPanel.add(usersScroller);

		messagesPanel.add(clearMessagesButton);
		clearMessagesButton.addActionListener(new MouseClickHandler());
		
		
		textPanel.add(new JLabel("Message Goes Here >"));
		textPanel.add(field);
		
		JLabel pv = new JLabel(new ImageIcon("private.png"));
		sendPanel.add(sendMessageToAllButton);
		sendPanel.add(specificSendMessageButton);
		sendPanel.add(recipientsField);
		sendPanel.add(pv);
		
		sendMessageToAllButton.addActionListener(new MouseClickHandler());;	//add action listener to the button
		specificSendMessageButton.addActionListener(new MouseClickHandler());;	//add action listener to the button
		
		frame.add(gui);	//add the big panel to the frame

		//frame.setSize(854,480);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
	}

	public String retreiveMessage()
	{	//retreive a message if there is one to retreive
		if(sendToAllButtonClicked == 1)
		{
			specificSendButtonClicked = 0;
			String res = field.getText();	//get the entered message

			field.setText("");	//set the text to blank

			sendToAllButtonClicked = 0;	//set our state back to 0

			return res;	//return string
		}
		else if(specificSendButtonClicked == 1)
		{
			String res = field.getText();

			field.setText("");
			
			String recipients = ("SENDTO_" + recipientsField.getText());
			
			
			String newRes = (recipients + ":" + res);
			
			specificSendButtonClicked = 0;
			return newRes;
		}
		else return null;	//return null
	}


	public class MouseClickHandler extends MouseAdapter implements ActionListener 	//handle button presses
	{
		public void actionPerformed (ActionEvent e) 
		{
						
			if(e.getSource() == quitButton)	//if we want to quit
			{
				getConfirmation();
			}
			
			if(e.getSource() == aboutButton)	//if we want to see the help->about item
			{
				showAboutFrame();
			}
			
			if(e.getSource() == sendMessageToAllButton)	//if we want to send a message with teh button
			{	
				sendToAllButtonClicked = 1;
				
			}
			
			if(e.getSource() == specificSendMessageButton)
			{
				if(!(field.getText().equals("")))
					specificSendButtonClicked = 1;
			}

			if(e.getSource() == clearMessagesButton)
			{
				messagesField.setText("Message Log\n==============\n");
			}

			if(e.getSource() == showUsersButton)
			{

				
				showOnlineUsers();
			}
		}
		
		
	}
	
	
	
	/*public ActionListener taskPerformer = new ActionListener() { //handler for the cuckoo clock
		public void actionPerformed(ActionEvent e) {	//action performed
			if(timeAccumulated++ > 0) {
				realTimer.setText(String.valueOf(timeAccumulated));	//time accumulates and the timer counts UP
			}
		}
	};*/

}
