//package chat;

import java.net.*;	//import proper libraries
import java.io.*;	//import the io capability
import java.util.*;	//import utilities (possibly for an ArrayList of PERSON OBJECTS???)
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class chatServer extends Thread	//use threads for this application
{
    private ServerSocket serverSocket;	//socket for the server to send messages through
   	ArrayList<clientHandler> clientList;	//list of running clients	
	protected Socket socketForTalkingToClient;	//socket for talking to the client (as the name implies)	
	public class user	//not really necessary anymore I don't think
	{
		SocketAddress address;
		String name;
	}
	public chatServer()	//empty constructor to satisfy the compiler
	{
		serverSocket = null;
		socketForTalkingToClient = null;
	}
    public chatServer(Socket clientSok) throws IOException	//the actual constructor used
    {
		socketForTalkingToClient = clientSok;
      //serverSocket.setSoTimeout(60000);	//timeout after one minute of waiting
	    System.out.println("I, the server, am waiting for client on port " + socketForTalkingToClient.getLocalPort() + "...");	//message for waiting			
		start();
    }
			JFrame serverFrame = new JFrame("Press the Big Button to quit");
			JButton button = new JButton("Press me to quit the server");
	public class MouseClickHandler extends MouseAdapter implements ActionListener 	//handle button presses
	{
		public void actionPerformed (ActionEvent e) 
		{
						
			if(e.getSource() == button)	//if we want to quit
			{
				System.exit(0);
			}
		}
	}
	public synchronized void acceptConnections()	//the server and client have a special connection
	{
		try	//so while the program is running....
		{

			serverFrame.getContentPane().add(button);
				button.addActionListener(new MouseClickHandler());
			serverFrame.setSize(300,300);
			serverFrame.setVisible(true);

			while(true)
			{
					System.out.println("Waiting for connection");
					clientList.add(new clientHandler(serverSocket.accept()));	//...accept connections. 

			}

		}
		catch(IOException ee)
        {
			ee.printStackTrace();
        }
	}

	public class clientHandler extends Thread	//subclass to "take care of" our clients
	{
		String name;
		Socket messageTube;
		DataInputStream inFromClient;
		DataOutputStream outToClient;
		
		
		public clientHandler(Socket clientSok) throws IOException
		{
			//System.out.println("Got here");
			
			messageTube = clientSok;
		  //serverSocket.setSoTimeout(60000);	//timeout after one minute of waiting
		  
					  System.out.println("I, the server, am waiting for client on port " +
				messageTube.getLocalPort() + "...");	//message for waiting
			name = "";	
			start();
		}
		
	   public synchronized void run()	//running thread
	   {
	   
		synchronized(clientHandler.this)
		{
			 try
			 {		
				System.out.println("I, the server, just connected to "+ messageTube.getLocalPort());	//print details of client connection		
				user aUser = new user();
				aUser.address = messageTube.getRemoteSocketAddress();			
				outToClient = new DataOutputStream(messageTube.getOutputStream());	//output stream of data, goes TO the client
				inFromClient = new DataInputStream(messageTube.getInputStream());	//read input from client				
				int messageCounter = 0;			
				int running  = 1;
			
				while(running == 1)
				{
					DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
					//get current date time with Date()
					Date date = new Date();
					
 
					//get current date time with Calendar()
					Calendar cal = Calendar.getInstance();
					String theDateAndTime = dateFormat.format(cal.getTime());
				
					String msg = "";
					String badName = "";
					try
					{	//catch when a user isn't there anymore, and we don't hear back from them	
						msg = inFromClient.readUTF();	//treat the read message as a string
					}
					catch(Exception e)
					{
						badName = name;
						msg = badName + " has disconnected";
						clientList.remove(this);					
					}
						//System.out.println("Client says: '"+msg+"'");		//print messages
						
					if(messageCounter == 0){	//the first received message from the client will be the username
						aUser.name = msg;
						name = msg;
						messageCounter ++;
						//outToClient.writeUTF("\n"+aUser.name + " has joined the chatroom!\n");
						
						for(clientHandler cH : clientList)
						{
							//System.out.println("At a client");
							cH.outToClient.writeUTF("\n"+"["+theDateAndTime+"]  "+aUser.name + " has joined the chatroom!\n");
						}
					}
					else
					{
						String ms = msg.replaceAll("\\s+","");
						if(!(ms.equals(""))){
							//outToClient.writeUTF(aUser.name + ":	" +msg);	//write the received message in this form to the client
							if(msg.length() > 7 && msg.substring(0,7).equalsIgnoreCase("sendto_"))
							{
								String recipients = msg.substring(7,msg.indexOf(":"));
								String[] receivers = recipients.split(" ");							
								ArrayList<String> receiversList = new ArrayList<String>();								
								for(int i = 0; i < receivers.length; i++)
								{
									receiversList.add(receivers[i]);
								}
								
								
								for(clientHandler cH : clientList)
								{
								
									String formattedMSG = msg.substring(msg.indexOf(':')+1,msg.length());
									
									if(receiversList.contains(cH.name) || cH.name.equals(aUser.name))
										cH.outToClient.writeUTF("[PRIVATE]"+"["+theDateAndTime+"]  "+aUser.name  + ":  "+ formattedMSG);
								}
							}
							else
							{
								for(clientHandler cH : clientList)
								{
									//System.out.println("At a client");
									if(badName.equals(""))
										cH.outToClient.writeUTF("["+theDateAndTime+"]  "+aUser.name  + ":  "+ msg);
									else
										cH.outToClient.writeUTF("\n"+"["+theDateAndTime+"]  "+msg+"\n");
								}
							}
						}
						else
						{
							//outToClient.writeUTF("");
							
							String listOfClients = "CLIENTLIST_";
							for(clientHandler cH : clientList)
							{
								listOfClients += (cH.name + "@");
							}
							outToClient.writeUTF(listOfClients);
							
						}
					}
				if(!(badName.equals(""))) stop();
			}

				messageTube.close();	//close server after this

			 }
		 catch(IOException e)	//catch io exceptions, which happen a lot, so we don't print the stacktrace
			 {
				e.printStackTrace();
				//break;
			 }
			 
			}
	   }
	}
	
   public static void main(String [] args)
   {
      int port = 54321;
      //ServerSocket serverSocket = null;
	  chatServer cs = new chatServer();
	  cs.clientList = new ArrayList<clientHandler>();
	  
	  Icon portImage = new ImageIcon ("port.png");

	JOptionPane portFrame = new JOptionPane();
	JTextField getPortField = new JTextField(6);
	
	JPanel myPanel = new JPanel();
	     
	    
	      myPanel.add(new JLabel("Port:"));
	      myPanel.add(getPortField);
	      myPanel.add(Box.createVerticalStrut(50)); 
	      portFrame.add(myPanel);
	      int result = portFrame.showConfirmDialog(null, myPanel,
	       "Please Enter Connection Details", JOptionPane.OK_CANCEL_OPTION,0,portImage);

	if (result == JOptionPane.OK_OPTION) {

      try
      {
		System.out.println("Connection socket created");
		
		cs.serverSocket = new ServerSocket(Integer.valueOf(getPortField.getText()));
		cs.acceptConnections();
		
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
	finally
	{
		try{
				cs.serverSocket.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	}
   }
}
