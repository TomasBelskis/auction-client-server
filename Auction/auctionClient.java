/*  1. Connects to server 
	2. Item on for auction is displayed and current bid
	3. Ammount entered should be greater than current bid
	4. After the bid is placed a the new bid value should be displayed on clients window/console*/

import java.net.*;
import java.io.*;

public class AuctionClient implements Runnable
{  private Socket socket              = null;
   private Thread thread              = null;
   private BufferedReader  console   = null;
   private DataOutputStream streamOut = null;
   private ChatClientThread client    = null;
   private String chatName;

   
   public AuctionClient(String serverName, int serverPort, String name)
   {
	  System.out.println("Establishing connection. Please wait ...");

	  this.chatName = name;
      try{
		 socket = new Socket(serverName, serverPort);
         System.out.println("Connected: " + socket);
         start();
      }
      catch(UnknownHostException uhe){
		  System.out.println("Host unknown: " + uhe.getMessage());
	  }
      catch(IOException ioe){
		  System.out.println("Unexpected exception: " + ioe.getMessage());
	  }
   }

   public void run()
   {
	   while (thread != null){
		 try {
			//String message = chatName + " > " + console.readLine();
			String message = console.readLine();
			streamOut.writeUTF(message);
            streamOut.flush();
         }
         catch(IOException ioe)
         {  System.out.println("Sending error: " + ioe.getMessage());
            stop();
         }
      }
   }

   public void handle(String msg)
   {  if (msg.equals(".bye"))
      {  System.out.println("Good bye. Press RETURN to exit ...");
         stop();
      }
      else
         System.out.println(msg);
   }

   public void start() throws IOException
   {
	  console = new BufferedReader(new InputStreamReader(System.in));

      streamOut = new DataOutputStream(socket.getOutputStream());
      if (thread == null)
      {  client = new ChatClientThread(this, socket);
         thread = new Thread(this);
         thread.start();
      }
   }

   public void stop()
   {
      try
      {  if (console   != null)  console.close();
         if (streamOut != null)  streamOut.close();
         if (socket    != null)  socket.close();
      }
      catch(IOException ioe)
      {
		  System.out.println("Error closing ...");

      }
      client.close();
      thread = null;
   }


   public static void main(String args[])
   {  AuctionClient client = null;
      if (args.length != 3)
         System.out.println("Usage: java ChatClient host port name");
      else
         client = new Auction
         Client(args[0], Integer.parseInt(args[1]), args[2]);
   }
}