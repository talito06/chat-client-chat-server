package chat_v6;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Color;




/*
 * ChatClient version 4  sends to the server.. and receives from the server
 * Private Messaging //pm:12345-blahblhablahblahblahblahblah
 */
public class ChatClient extends JFrame implements Runnable, ActionListener
{

	private static int clientCount = 0;
	private static ChatServerThread [] clients = new ChatServerThread[50];
	private Socket socket = null;
	BufferedReader console = null;
	private DataOutputStream strOut = null;
	private DataInputStream strIn= null;
	private Thread thread = null;
	private ChatClientThread client = null;
	private String line = "";
	private boolean done = true;
	
	private String _serverName = null;
	private int _serverPort;
	
	private JPanel panel = null;
	
	private JTextArea areaToType;
	private JTextField textField;
	private JButton send;
	private JButton privateMsg;
	private JButton encrypt;
	private JButton connect;
	private JButton disconnect;
	private JButton privEnc;
	

		
	public ChatClient(String serverName, int serverPort)
	{
		setBackground(Color.RED);
		_serverName = serverName;
		_serverPort = serverPort;
		
		try 
		{
			socket = new Socket(serverName, serverPort);//step 1	
			start();
			
			//ChatServer.clientCount++;
			//step 2
			//stop();//step 4
		} catch (UnknownHostException e) 
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		setTitle("Client");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 500, 330);
		panel = new JPanel();
		panel.setBackground(Color.BLUE);
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(null);
		getContentPane().add(panel);


		areaToType = new JTextArea();
		areaToType.setEnabled(false);
		areaToType.setEditable(false);
		areaToType.setBounds(21, 11, 429, 129);
		panel.add(areaToType);

		send = new JButton("messege");
		send.setBackground(Color.RED);
		

		send.setBounds(25, 226, 100, 20);
		panel.add(send);
		send.addActionListener(this);
		
		privateMsg = new JButton("Private msg");
		privateMsg.setBackground(Color.RED);
		privateMsg.setBounds(20, 257, 105, 20);
		panel.add(privateMsg);
		privateMsg.addActionListener(this);
		
		encrypt = new JButton("Encrypt");
		encrypt.setBackground(Color.RED);
		encrypt.setBounds(173, 226, 65, 20);
	      panel.add(encrypt);
		encrypt.addActionListener(this);
		
		privEnc = new JButton("Private Encrypted");
		privEnc.setBackground(Color.RED);
		privEnc.setBounds(145, 257, 120, 20);
		panel.add(privEnc);
		privEnc.addActionListener(this);
		
		connect = new JButton("Connect");
		connect.setBackground(Color.RED);
		connect.setBounds(293, 226, 85, 20);
		panel.add(connect);
		connect.addActionListener(this);
		
		disconnect = new JButton("Disconnect");
		disconnect.setBackground(Color.RED);
		disconnect.setBounds(293, 257, 100, 20);
		panel.add(disconnect);
		disconnect.addActionListener(this);

		textField = new JTextField();
		textField.setBounds(25, 169, 425, 35);
		textField.setColumns(10);
		textField.setEditable(false);
		panel.add(textField);
		
		send.setEnabled(false);
		privateMsg.setEnabled(false);
		encrypt.setEnabled(false);
		disconnect.setEnabled(false);
		privEnc.setEnabled(false);
		connect.setEnabled(true);
	}
	
	
	private static class OneTimePad
	{
		
		private String plnMsg;
		private String encMsg; 
		private String msgKey;
		
		public OneTimePad()
		{
			plnMsg  = "";
			encMsg = "";
			msgKey  = "";
		}
		
		public OneTimePad(String msg)
		{
			plnMsg = msg;
			msgKey = getKey();
			encMsg = encrypt();
		}
		
		protected String getKey()
		{
			String key = "";
			for(int i=0; i<plnMsg.length(); i++)
			{
				char randChar = Character.toChars( (int)(Math.random() * 60 ))[0];
				key += randChar;
				//System.out.println("KEY: "+key);
			}
			return key;
		}
		
		protected String encrypt()
		{
			String encryptedMsg = "";
			for(int i=0; i<plnMsg.length(); i++)
			{
				encryptedMsg = encryptedMsg + Character.toChars(   ( (msgKey.charAt(i)) + plnMsg.charAt(i)  ) )[0];
				//System.out.println("ENCRYPTED MSG: "+ encryptedMsg );
			}
			return encryptedMsg;
		}
		
		
		protected String decrypt()
		{
			String decryptedMsg = "";
			for(int i=0; i<encMsg.length(); i++)
			{
				decryptedMsg = decryptedMsg +  Character.toChars(  Math.abs( ( encMsg.charAt(i) - msgKey.charAt(i)) )  )[0];
				//System.out.println("DECRYPTED MSG: "+ decryptedMsg);
			}
			return decryptedMsg;
		}
		
		
		protected String decrypt(String e, String k)
		{
			String decryptedMsg = "";
			for(int i=0; i<e.length(); i++)
			{
				decryptedMsg = decryptedMsg +  Character.toChars( Math.abs(  ( e.charAt(i) - k.charAt(i)) )  )[0];
				//System.out.println("DECRYPTED MSG: "+ decryptedMsg);
			}
			return decryptedMsg;
		}
		
		
		protected String getEncrMsg()
		{
			return encMsg;
		}
		protected String getKeyForMsg()
		{
			return msgKey;
		}
		
	}
	

	public void handle(String msg){
		/*int indexOfMsgStart = msg.lastIndexOf(": ")+2;//find where the real Msg starts... because of what the server sticks onto the end
		String realMsg = msg.substring(indexOfMsgStart);//get the real msg from the "User said blah blah: ...."
		int midIndex = realMsg.length()/2 ;//0 to midIndex non-inclusive encr ... from midIndex to end is key
		String encMsg = realMsg.substring(0, midIndex);//0 to midIndex non-inclusive encr
		String keyMsg = realMsg.substring(midIndex);//from midIndex to end 
		OneTimePad otp = new OneTimePad();
		String decMsg = otp.decrypt(encMsg, keyMsg);//I know the enc and the key... can decrypt
		*/
		
		if(msg.equalsIgnoreCase("bye"))
		{
			//line="bye";
			stop();
		}
		else{
			System.out.println(msg);
		}
	}
	
	@Override
	public void run() 
	{
		while( (thread!=null)  && (!line.equalsIgnoreCase("bye")))//step 3
		{
			try 
			{
				line = console.readLine();
				//OneTimePad otp = new OneTimePad(line);//make all messages encrypted
				//String longerENC_KEY_MSG = otp.getEncrMsg() +otp.getKeyForMsg();//build a string double the lenght of the msg... include the encr + key 
				
				//strOut.writeUTF(longerENC_KEY_MSG);
				strOut.writeUTF(line);
				strOut.flush();
				addToTextArea(line);
				
			} catch (IOException e) 
			{
				addToTextArea("Problem reading line from client: "+e.getMessage());
			}
		}
			
	}
	public void start() throws IOException
	{
		console = new BufferedReader(new InputStreamReader(System.in));//step 2a
		strOut = new DataOutputStream(socket.getOutputStream());//step 2b
		if(thread==null)
		{
			client = new ChatClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
		}
	}
	
	
	public void open()
	{
		try
		{
			strIn = new DataInputStream(socket.getInputStream());
			strOut = new DataOutputStream(socket.getOutputStream());
			new Thread(this).start();
		}
		catch(IOException e)
		{
				e.printStackTrace();
		}
	}
	
	
	
	public void stop()
	{
		done=true;
		if(thread!=null)
		{
			thread=null;
		}
		try
		{
			if(console != null)
			{
				console.close();
			}
			if(strOut != null)
			{
				strOut.close();
			}
			if(socket != null)
			{
				socket.close();
			}
		}
		catch(IOException e){
			System.out.println("STOPPING ERROR"+e.getMessage());
		}
	}
	
	
	public void connect()
	{
		String connectStatus = "Start :Message";
		try
		{
			socket = new Socket(_serverName, _serverPort);
			open();
			send.setEnabled(true);
			privateMsg.setEnabled(true);
			encrypt.setEnabled(true);
			connect.setEnabled(false);
			privEnc.setEnabled(true);
			disconnect.setEnabled(true);
			textField.setEditable(true);
			//clients[clientCount]=   socket.getPort()
		
		}
		catch(UnknownHostException uhe)
		{
			connectStatus = uhe.getMessage();
		}
		catch(IOException ioe)
		{
			connectStatus = ioe.getMessage();
		}
		finally
		{
			addToTextArea(connectStatus);
		}
				
	}
	
	
	
	public void disconnect()
	{
		send.setEnabled(false);
		privateMsg.setEnabled(false);
		encrypt.setEnabled(false);
		disconnect.setEnabled(false);
		privEnc.setEnabled(false);
		connect.setEnabled(true);
		send("bye");
		done=true;
	}
	
	
	
	public void send(String msg)
	{
		try
		{
			strOut.writeUTF(msg);
			strOut.flush();
		}
		catch(IOException ioe)
		{
			addToTextArea(ioe.getMessage());
		}
	}
	
	public void sendEncrypted(String msg)
	{
		try
		{
			OneTimePad otp = new OneTimePad(msg);//make all messages encrypted
			String longerENC_KEY_MSG = otp.getEncrMsg() +otp.getKeyForMsg();//build a string double the lenght of the msg... include the encr + key 
			strOut.writeUTF(longerENC_KEY_MSG);
			strOut.flush();
		}
		catch(IOException ioe)
		{
			addToTextArea(ioe.getMessage());
		}
	}
	
	
	public void sendPrivate(String ID, String input)
	{
		 System.out.println("Hello");
		 System.out.println(ID);
		//System.out.println("SERVERLOG- USER: "+ ID + " said: "+input);
		String privMsg = "pm:";//pm:12345-
		String delimIDStart = ":";
		String delimMsgStart= "-";
		if(input.startsWith(privMsg))
		{
			int indexIDStart = input.indexOf(delimIDStart)+1;//get the starting index of the id using the delimeter
			int indexIDEnd = input.indexOf(delimMsgStart);
			int indexMsgStart = input.indexOf(delimMsgStart)+1;
			int ID_SendTo = Integer.parseInt(ID);
			String msg = input.substring(indexMsgStart);//the rest is the message
			System.out.println(msg);
			int indexOfID_SendTo = findClient(ID_SendTo);
			System.out.println(indexOfID_SendTo);
						System.out.println("Test 23232");                                                                                            
			if(indexOfID_SendTo != -1)
			{//user found so we send privateF
				clients[indexOfID_SendTo].send("USER: "+ ID + " said: "+msg);
				System.out.println(ID);
			}
		}
		else
		{
			System.out.println("Test 1");
			for(int i=0; i<clientCount; i++)
			{
				clients[i].send("USER: "+ ID + " said: "+input);
			}
		}
		
	}
	
	
	
	static int findClient(int ID){//introduced in version 4'
		System.out.println(clientCount);
		for(int i=0; i<clientCount; i++){
			 	System.out.println(clients[i].getID());        
			if(clients[i].getID() == ID){//if clients ID matches target
			
				return i;//match found return index location
			}
		}
		return -1;//no match found
	}
	
	
	
	public void sendPrivateEncr(String ID, String input)
	{
		String privEncMsg = "pm:";//pme=:12345-
		String delimUserStart = ":";
		String delimMessageStart= "-";
		if(input.startsWith(privEncMsg))
		{
			int indexUserStart = input.indexOf(delimUserStart);//get the starting index of the id using the delimeter
			int indexUserEnd = input.indexOf(delimMessageStart);
			int indexMessageStart = input.indexOf(delimMessageStart);
			int User_SendTo = Integer.parseInt(input.substring(indexUserStart,indexUserEnd));
			String message = input.substring(indexMessageStart);//the rest is the message
			OneTimePad otp = new OneTimePad(message);//make all messages encrypted
			String longerENC_KEY_MSG = otp.getEncrMsg() +otp.getKeyForMsg();//build a string double the lenght of the msg... include the encr + key 
			int indexOfUser_SendTo = findClient(User_SendTo);
			
			if(indexOfUser_SendTo != -1)
			{//user found so we send private
				clients[indexOfUser_SendTo].send("USER: "+ ID + " said: "+longerENC_KEY_MSG);
			}
		}
		
		else
		{
			for(int i=0; i<clientCount; i++)
			{
				clients[i].send("USER: "+ ID + " said: "+input);
			}
		}
		//use the id and the msg to send to the server as normal
		//make sure you encrypt the msg
		//prefix the encrypted by the unencrypted pm: ...
		//use the same string format you did in your reg console chat
	}
	
	
	public void addToTextArea(String fromMsg) 
	{
		if (areaToType.getText().trim().length() == 0) 
		{
			areaToType.append(fromMsg);
		} 
		else 
		{
			areaToType.append("\n" + fromMsg);
		}
	}
	
	
	public static void main(String[] args) 
	{
		//ChatClient myClient = new ChatClient("127.0.0.1", 8080);
		ChatClient myClient = null;
		if(args.length != 2)
		{
			System.out.println("You need host name and a port address to run the client");
		}
		else
		{
			String serverName = args[0];
			int port = Integer.parseInt(args[1]);
			myClient = new ChatClient(serverName, port );
			myClient.setVisible(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		JButton sendClicked = (JButton) e.getSource();
		String btn = sendClicked.getText();
		
		switch(btn)
		{
		case "Connect":
			connect();
			break;
		case "Disconnect":
			disconnect();
			break;
		case "messege":
			line = textField.getText();
			textField.setText("");
			send(line);
			addToTextArea("User Said: " + line);
			break;
		case "Private msg":
			line = textField.getText();
			textField.setText("");
			sendPrivate(line.substring(3, 8), line);
			break;
		case "Private Encrypted":
			line = textField.getText();
			textField.setText("");
			sendPrivateEncr(line.substring(4, 9), line.substring(10));
			addToTextArea("User Said: " + line);
			break;
		case "Encrypt":
			line = textField.getText();
			textField.setText("");
			sendEncrypted(line);
			addToTextArea("User Said: " + line);
			break;
		}
		
	}}
		
		
	




