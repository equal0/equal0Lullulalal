package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientReceiver implements Runnable{
	
	private LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();
	
	private MainGui mainGui;
	private ClientManager manager = new ClientManager();
	
	public ClientReceiver(){
		new Thread(new Handler()).start();
		mainGui = new MainGui(manager);
	}
	
	@Override
	public void run() {
		while(true){
			try(Socket client = new Socket("localhost", 8888)){
				
				try(ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
					ObjectInputStream ois = new ObjectInputStream(client.getInputStream())){
					
					System.out.println("���� ���� ����~!");
					//mainGui.connected(oos);
					manager.setOos(oos);
					manager.test();
					while(true){
						try {
							queue.add(ois.readObject());
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
				System.out.println("������������");
				//mainGui.disConnected();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class Handler implements Runnable{

		@Override
		public void run() {
			while(true) {
				try {
					Object[] receive = (Object[])queue.take();
					String proto = (String)receive[0];
					
					switch(proto){
					case "test" :
						System.out.println("�׽�Ʈ�Դϴ�");
						break;
					case "join" :
						break;
					case "login":
						break;
					case "showList":
						break;
					case "insert":
						break;
					case "evaluate":
						break;
					case "logout":
						break;
					case "askRestaurant":
						break;
					case "replyRestaurant":
						break;
					case "findAddress":
						break;
					case "recommend":
						break;
						
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}