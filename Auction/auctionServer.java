/* Auction Server to connect multiple clients, with the use of threading!*/
/* Server Functionality:
   1. Receive connections from multiple clients
   2. After client connects, notify the client which item is on sale 
   		and the highest bid offer (or reserve price)
   3. Specifies the bid period, Max allowed 1minute. When the bid is raised, the bid period is reset back.
   4. When a bid is placed all clients should be notified
   5. Clients should be notified how much time is left on bidding( when appriopriate )
   6. When auction for one item is finished another item should become available for auction minimum of 5 items
   7. Any item not sold should be auctioned again

*/
import java.net.*;
import java.io.*;
import java.util.*;

public class AuctionServer implements Runnable
{  
   
   // Array of clients	
   private AuctionServerThread clients[] = new AuctionServerThread[50];
   private ServerSocket server = null;
   private Thread       thread = null;
   private int clientCount = 0;
   private List<Items> itemStore;

   public AuctionServer(int port)
   {
    /*Port Binding!*/
	  try {

		 System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port);
         System.out.println("Server started: " + server.getInetAddress());
         start();
      }
      catch(IOException ioe)
      {
		  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());

      }
      /*Initialize a list of ttems to be stored! */
      
      itemStore = new ArrayList<Items>();
   }
   public void storeItems{
     Items item = new Items(1, "Painting", "Portrait of Juana", "ART", 500.55);
     itemStore.add(item);
     item = new Items(2, "Car", "BMW", "Vehicles", 2500.95);
     itemStore.add(item);
     item = new Items(3, "House", "123 HelloStreet, Town", "Housing", 245000.95);
     itemStore.add(item);
     item = new Items(4, "Phone", "Iphone6", "Mobiles", 600.15);
     itemStore.add(item);
     item = new Items(5, "Jewelery", "Golden Twisted Bracelet", "Bracelets", 300.95);
     itemStore.add(item);
      
   }
   public void run()
   {
	  while (thread != null)
      {
		 try{

			System.out.println("Waiting for a client ...");
            addThread(server.accept());

			int pause = (int)(Math.random()*3000);
			Thread.sleep(pause);

         }
         catch(IOException ioe){
			System.out.println("Server accept error: " + ioe);
			stop();
         }
         catch (InterruptedException e){
		 	System.out.println(e);
		 }
      }
   }

  public void start()
    {
		if (thread == null) {
		  thread = new Thread(this);
          thread.start();
       }
    }

   public void stop(){
	   thread = null;

   }

   private int findClient(int ID)
   {
	   for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }

   public synchronized void broadcast(int ID, String input)
   {

	   if (input.equals(".bye")){
		  clients[findClient(ID)].send(".bye");
          remove(ID);
       }
       else
         for (int i = 0; i < clientCount; i++){
			if(clients[i].getID() != ID)
            	clients[i].send(ID + ": " + input); // sends messages to clients
      
		}
       notifyAll();
   }
   public synchronized void remove(int ID)
   {
	  int pos = findClient(ID);
      if (pos >= 0){
		 ChatServerThread toTerminate = clients[pos];
         System.out.println("Removing client thread " + ID + " at " + pos);

         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
               clients[i-1] = clients[i];
         clientCount--;

         try{
			 toTerminate.close();
	     }
         catch(IOException ioe)
         {
			 System.out.println("Error closing thread: " + ioe);
		 }
		 toTerminate = null;
		 System.out.println("Client " + pos + " removed");
		 notifyAll();
      }
   }

   private void addThread(Socket socket)
   {
	  if (clientCount < clients.length){

		 System.out.println("Client accepted: " + socket);
         clients[clientCount] = new AuctionServerThread(this, socket);
         try{
			clients[clientCount].open();
            clients[clientCount].start();
            clients[clientCount].send("The current item up for auction is: " + "\r\n Item ID:" + itemStore.get(0).getID() + "\r\n Type:" + itemStore.get(0).getType() + "\r\n Title:" + itemStore.get(0).getTitle() + "\r\n Starting Bid: " + itemStore.get(0).getStartingBid() + "\r\n CurrentBid:" +itemStore.get(0).getCurrentBid() + "\r\n Current Highest Bidder ID:" + itemStore.get(0).getBidderId());
            clientCount++;
         }
         catch(IOException ioe){
			 System.out.println("Error opening thread: " + ioe);
		  }
	  }
      else
         System.out.println("Client refused: maximum " + clients.length + " reached.");
   }


   public static void main(String args[]) {
	   AuctionServer server = null;
      if (args.length != 1)
         System.out.println("Usage: java AuctionServer port");
      else
         server = new AuctionServer(Integer.parseInt(args[0]));
         server.storeItems();
   }

}