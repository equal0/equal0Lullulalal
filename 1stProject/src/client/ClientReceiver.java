package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import client.gui.MainGui;

public class ClientReceiver implements Runnable{
	
	public static int MAIN_GUI_ID = 0; 
	private static int guiId = 1;
	private static ConcurrentHashMap<Integer, LinkedBlockingQueue<Object>> guiReceiveQlist= new ConcurrentHashMap<>();
	
	public ClientReceiver(){
	//	gui = new MainGui();
	}
	
	public static int getGuiID(){
		int rtn = guiId;
		guiId++;
		return rtn;
	}
	public static void addQueue(int guiId, LinkedBlockingQueue<Object> q) {
		guiReceiveQlist.put(guiId, q);
	}
	public static void deleteQueue(int guiId) {
		LinkedBlockingQueue<Object> queue = guiReceiveQlist.get(guiId);	
		
		Object[] exit = {guiId, "exit"};
		queue.add(exit);
		
		guiReceiveQlist.remove(guiId);
	}
	
	
	@Override
	public void run() {
		while(true){
			try(Socket client = new Socket("localhost", 8888)){
				
				try(ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
					ObjectInputStream ois = new ObjectInputStream(client.getInputStream())){
					
					System.out.println("서버 연결 성공~!");
					ClientManager.setOos(oos);
					MainGui gui = new MainGui();
					//gui.connected();
					while(true){
						try {
							Object responseData = ois.readObject();
							int gid = (int)(((Object[])responseData)[0]);
							LinkedBlockingQueue<Object> q = guiReceiveQlist.get(gid);
							q.add(responseData);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				
			} catch (UnknownHostException e) {
				//e.printStackTrace();
			} catch (IOException e) {
				//e.printStackTrace();
			}
			
			try {
				System.out.println("서버연결대기중");
				//mainGui.disConnected();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
