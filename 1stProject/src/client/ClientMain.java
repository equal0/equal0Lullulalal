package client;

public class ClientMain {

	public static void main(String[] args) {
		new Thread(new ClientReceiver()).start();
	}

}