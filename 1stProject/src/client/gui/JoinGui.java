package client.gui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import client.ClientManager;
import client.ClientReceiver;
import client.Config;
import client.LoginStatement;
//import client.gui.JoinGui.Handler;
import vo.Member;
import vo.Restaurant;

public class JoinGui extends JDialog implements ActionListener {
	private static final int WIDTH = 300;
	private static final int HEIGHT = 330;
	private JTextField tf_id;
	private JPasswordField tf_pwd;
	private JTextField tf_name;
	private JTextField tf_year;
	
	private int guiId = ClientReceiver.getGuiID();
	private LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();
	
	private ClientManager manager = new ClientManager();
	private JTextField month;
	private JTextField day;
	
	public JoinGui(MainGui gui){
		
		super(gui, Dialog.ModalityType.APPLICATION_MODAL);
		ClientReceiver.addQueue(guiId, queue);
		new Thread(new Handler(this)).start();
		this.addWindowListener(new windowHandler());
		this.setSize(WIDTH, HEIGHT);
		
		Font font = new Font("�����ٸ�����", Font.PLAIN, 12);
		
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 292, 292);
		panel.setBackground(new Color(255, 255, 255));
		getContentPane().add(panel);
		panel.setLayout(null);
		
		tf_id = new JTextField();
		tf_id.setBounds(103, 72, 123, 21);
		panel.add(tf_id);
		tf_id.setColumns(10);
		
		tf_pwd = new JPasswordField();
		tf_pwd.setBounds(103, 103, 123, 21);
		panel.add(tf_pwd);
		tf_pwd.setColumns(10);
		
		tf_name = new JTextField();
		tf_name.setBounds(103, 134, 123, 21);
		panel.add(tf_name);
		tf_name.setColumns(10);
		
		tf_year = new JTextField();
		tf_year.setBounds(103, 165, 32, 21);
		panel.add(tf_year);
		tf_year.setColumns(10);
		
		
		
		Font fontJoinLb = new Font("�����ٸ�����", Font.PLAIN, 25);
		JLabel lb_join = new JLabel("ȸ������");
		lb_join.setBounds(23, 10, 203, 52);
		panel.add(lb_join);
		lb_join.setFont(fontJoinLb);
		
		JButton btn_ok = new JButton("\uD655\uC778");
		btn_ok.setBounds(96, 205, 50, 25);
		panel.add(btn_ok);
		btn_ok.setFont(font);
		btn_ok.addActionListener(this);
		
		JLabel lb_id = new JLabel("\uC544\uC774\uB514");
		lb_id.setBounds(23, 72, 57, 15);
		panel.add(lb_id);
		lb_id.setFont(font);
		
		JLabel lb_pwd = new JLabel("\uBE44\uBC00\uBC88\uD638");
		lb_pwd.setBounds(23, 104, 57, 15);
		panel.add(lb_pwd);
		
		
		JLabel lb_name = new JLabel("\uC774   \uB984");
		lb_name.setBounds(23, 137, 57, 15);
		panel.add(lb_name);
		lb_name.setFont(font);
		
		JLabel lb_birth = new JLabel("\uC0DD   \uC77C");
		lb_birth.setBounds(23, 168, 57, 15);
		panel.add(lb_birth);
		lb_birth.setFont(font);
		
		month = new JTextField();
		month.setColumns(10);
		month.setBounds(148, 165, 32, 21);
		panel.add(month);
		
		day = new JTextField();
		day.setColumns(10);
		day.setBounds(194, 165, 32, 21);
		panel.add(day);
		
		this.setBounds((int)gui.getLocation().getX() + 40, 
				(int)gui.getLocation().getY() + 150, WIDTH, HEIGHT);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Ȯ��")){
			String id = tf_id.getText();
			String pwd = new String(tf_pwd.getPassword());
			String name = tf_name.getText();
			String birth = tf_year.getText();
			
			if( !"".equals(id) && !"".equals(pwd) &&
				!"".equals(name) && !"".equals(birth)	){
				
				Member newUser = new Member( id, pwd, 
						name, Member.USER, birth);
				
				manager.join(guiId, newUser);
				
			}
		}
		
	}
	
	private class windowHandler extends WindowAdapter{
		public void windowClosing (WindowEvent e){
			ClientReceiver.deleteQueue(guiId);
		}
	}
	
	private class Handler implements Runnable{
		JoinGui gui ;
		
		public Handler(JoinGui gui){
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
						
					case "join":
						boolean rst = (Boolean)receive[2];
						if (rst == false )
							JOptionPane.showMessageDialog(gui, "�ߺ��� ���̵� �Դϴ�.");
						else
							ClientReceiver.deleteQueue(guiId);
						break;
						
						
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
}