package client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;

public class MainGui extends JFrame {
	
	private int guiId = ClientReceiver.MAIN_GUI_ID;
	
	//private int guiId = ClientReceiver.getGuiID();
	private LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();
	
	private ClientManager manager = new ClientManager();
	
	public MainGui(){
		ClientReceiver.addQueue(guiId, queue);
		new Thread(new Handler(this)).start();
		
		//gui�� close �� �� ClientReceiver.deleteQueue(guiId); �� ȣ�����־���Ѵ�.!!
		this.addWindowListener(new windowHandler());
		
		this.setSize(100, 100);
		this.setVisible(true);
		
	}
	
	public void test() {
		manager.test();
	}
	
	private class Handler implements Runnable{
		JFrame gui ;
		
		public Handler(JFrame gui){
			this.gui = gui;
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					Object[] receive = (Object[])queue.take();
					String proto = (String)receive[1];
					
					switch(proto){
					case "test" :
						System.out.println("�׽�Ʈ�Դϴ�");
						break;
						
					//�������ݿ� ���� �ൿ ����.
						
					case "exit" :
						if(gui != null) {
							gui.dispose();
						}
						return;
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class windowHandler extends WindowAdapter{
		public void windowClosing (WindowEvent e){
			ClientReceiver.deleteQueue(guiId);
		}
	}
}
