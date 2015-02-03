//package chat;

//File Name GreetingClient.java

import java.net.*;	//net libraries
import java.io.*;	//io functions
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class chatClient
{
	ArrayList<String> onlineUsers = new ArrayList<String>();
    private String serverName; //connecting to local host for now
    private int port;	//matches the server's open port (hehehe)
	public Socket clientSocket;	//socket for the client
	String messageToDisplay;	//string for displaying a message
	String userInput = "";
	String userName = "";		//string for user name

	public chatClient(String sN, int p) throws IOException
	{
		clientSocket = new Socket(sN,p);	//initialize the socket to speak through the port, hardcoded to 54321
		serverName = sN;
		port = p;
	}
	
	public chatClient()	//no-parameter constructor is very basic
	{
		clientSocket = null;	
		serverName = "";
		port = 0;
	}
	
	public void openConnectionToServer(String user)
	{
		chatClientGUI gui = new chatClientGUI();
		try
		{
			System.out.println("I," + userName + ", am connecting to " + serverName + " on port " + port);	//print this to the client
			System.out.println("I, the client, just connected to " + clientSocket.getRemoteSocketAddress());	//print message to client

     		OutputStream outToServer = clientSocket.getOutputStream();	//output stream for writing to server
     		DataOutputStream out = new DataOutputStream(outToServer);	//data output stream for writing to server			
			out.writeUTF(userName);
			
						
			gui.createFrame(user);	//show our interactive gui
			
			InputStream inFromServer = clientSocket.getInputStream();	//get message(s) from the server
		 	DataInputStream in = new DataInputStream(inFromServer);	//put those messages in a stream					
			messageToDisplay = in.readUTF();
			gui.printMessage(messageToDisplay);	//and print the server's message to the gui		
			
			int inLoop = 1;
			while(inLoop == 1)
			{				
				/*Scanner reader = new Scanner(System.in);
				System.out.print("\nEnter a string to send to the server: ");
				userInput = reader.nextLine();
				if(userInput.equalsIgnoreCase("q") || userInput.equalsIgnoreCase("quit"))
					break;*/
				String s = gui.retreiveMessage();	//retrieve a message from the gui
				if(s != null)	//if there is a message to retrieve...
				{
					//System.out.println(":"+s+":");	//debugging
					out.writeUTF(s);	//write this message to the server
					messageToDisplay = in.readUTF();	//read the string from the stream (received from server)
		 			//System.out.println("Server says: " + messageToDisplay);	//print back server's messages to the client
					if(!(messageToDisplay.equals("")))
						gui.printMessage(messageToDisplay);	//and print the server's message to the gui
				}
				else
				{
					out.writeUTF("");	//write this message to the server
					messageToDisplay = in.readUTF();	//read the string from the stream (received from server)
		 			//System.out.println("Server says: " + messageToDisplay);	//print back server's messages to the client
					if(messageToDisplay.length() >11 && messageToDisplay.substring(0,11).equalsIgnoreCase("clientlist_"))
					{
						onlineUsers.clear();
						String toFormat = messageToDisplay.substring(11,messageToDisplay.length());
						String[] names = toFormat.split("@");
						
						for(String ess : names)
						{
							if(!(ess.equals("")))
								onlineUsers.add(ess);
						}

						for(String ess : onlineUsers)
						{
							//System.out.println(ess);
						}
						gui.listOfUsersOnline = onlineUsers;
						gui.usersText = "Users Online\n============\n";

						for(String ess : gui.listOfUsersOnline)
						{
							gui.usersText += (clientSocket.getRemoteSocketAddress() + " $" + ess + "\n");
						}
						gui.onlineUsersField.setText(gui.usersText);
					}
					else if(!(messageToDisplay.equals(""))){
						gui.printMessage(messageToDisplay);	//and print the server's message to the gui
					}
				}				
			}
      		clientSocket.close();	//close after we are done
   		}
   		catch(IOException e)
   		{
		gui.frame.setVisible(false);
		gui.frame.dispose();
		JOptionPane lostConnectionPane = new JOptionPane();
		lostConnectionPane.showMessageDialog(null, "Lost Connection (oops)");
      		e.printStackTrace();	//catch exceptions and print the stack trace
  		}
	}

	public static void main(String [] args)
	{	
		try
		{			
			JOptionPane getAddressAndPortFrame = new JOptionPane();
			JPanel myPanel = new JPanel();
			
			
			Icon chatIcon = new ImageIcon("chat.png");
			 
			JTextField getAddrField = new JTextField(20);
			JTextField getPortField = new JTextField(6);
           
			myPanel.add(new JLabel("IP:"));
	        myPanel.add(getAddrField);
   	        myPanel.add(Box.createVerticalStrut(110)); // a spacer
	        myPanel.add(new JLabel("Port:"));
      	    myPanel.add(getPortField);
      	    
			getAddressAndPortFrame.add(myPanel);			
		    int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter Connection Details", 
		    		JOptionPane.OK_CANCEL_OPTION,0,chatIcon);

			if (result == JOptionPane.OK_OPTION) {chatClient cc = new chatClient(getAddrField.getText(),Integer.valueOf(getPortField.getText()));
			
				int goodName = 0;
				while(goodName == 0)				
				{
					
					JOptionPane getNamePane = new JOptionPane();
					JTextField getNameField = new JTextField(20);

					getNamePane.add(getNameField);
				
					cc.userInput = JOptionPane.showInputDialog("Enter your desired username:\n");

					if(cc.userInput.equalsIgnoreCase("q") || cc.userInput.equalsIgnoreCase("quit"))
						System.exit(0);
				
					String uIS = cc.userInput.replaceAll("\\s+","");
					if(!(uIS.equals("")))
					{
						cc.userName = cc.userInput;
						goodName = 1;
					}
				}
				cc.openConnectionToServer(cc.userInput);
			}			
		}
   		catch(IOException e)
  		{
     		e.printStackTrace();
   		}
	}
}

