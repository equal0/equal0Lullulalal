package client.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.ClientManager;
import client.ClientReceiver;
import client.LoginStatement;
import vo.Address;
import vo.Category;
import vo.Evaluation;
import vo.Member;
import vo.Restaurant;

public class RecommendGUI extends JDialog{
	private int guiId = ClientReceiver.getGuiID();
	private LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();
	private ClientManager manager = new ClientManager();
	
	private JPanel north;
	private JPanel pnl_area;
	private JPanel pnl_type;
	private JPanel pnl_score;
	private JComboBox<String> cb_area;
	private JComboBox<String> cb_type;
	private JComboBox<String> cb_score;
	private JComboBox<String> cb_price;
	
	private JPanel panel;
	private JLabel lbl_random;
	private JLabel lbl_user;
	private JButton btn_random;
	private JButton btn_user;

	public RecommendGUI(MainGui main) {
		super(main, Dialog.ModalityType.APPLICATION_MODAL);
		
		ClientReceiver.addQueue(guiId, queue);
		new Thread(new Handler(this)).start();
		
		
		Point p = MainGui.getMainGui().getLocation();
		this.setBounds(p.x + 20, 
				p.y + 170, 320, 264);
		
		Font font = new Font("나눔바른고딕", Font.PLAIN, 12);
		
		north = new JPanel();
		getContentPane().add(north,BorderLayout.NORTH);
		
		pnl_area = new JPanel();
		cb_area = new JComboBox<>();
		cb_area.setFont(font);
		cb_area.setModel(new DefaultComboBoxModel<>(new String[] {"지역","강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구", "동대문구", "동작구",
						"마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"}));
		pnl_type = new JPanel();
		cb_type = new JComboBox<>();
		cb_type.setFont(font);
		cb_type.setModel(new DefaultComboBoxModel<>(new String[] {"종류","한식","중식","일식","양식"}));
		
		cb_price = new JComboBox<>();
		cb_price.setFont(font);
		cb_price.setModel(new DefaultComboBoxModel<>(new String[] {}));
		
		pnl_score = new JPanel();
		cb_score = new JComboBox<>();
		cb_score.setFont(font);
		cb_score.setModel(new DefaultComboBoxModel<>(new String[] {"☆☆☆☆☆","☆☆☆☆★","☆☆☆★★","☆☆★★★","☆★★★★","★★★★★"}));
		
		north.add(cb_area);
		north.add(cb_type);
		north.add(cb_score);
		
		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		lbl_random = new JLabel(new ImageIcon("img/user.png"));
		lbl_random.setBounds(32, 15, 47, 49);
		panel.add(lbl_random);
		
		lbl_user = new JLabel(new ImageIcon("img/lbl_user.png"));
		lbl_user.setBounds(32, 75, 47, 49);
		panel.add(lbl_user);
		
		btn_random = new JButton("랜덤으로 추천받기");
		btn_random.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		btn_random.setBounds(95, 20, 133, 36);
		panel.add(btn_random);
		
		btn_user = new JButton("접속자에게 추천받기");
		btn_user.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		btn_user.setBounds(95, 82, 133, 36);
		panel.add(btn_user);
		
		//actionListener
		btn_random.addActionListener(new Action());
		btn_user.addActionListener(new Action());

		this.setVisible(true);
	}
	
	private class Handler implements Runnable {
		RecommendGUI gui;

		public Handler(RecommendGUI gui) {
			this.gui = gui;
		}

		@Override
		public void run() {
			while (true) {
				try {
					Object[] receive = (Object[]) queue.take();
					String proto = (String) receive[1];

					switch (proto) {
					
					case "exit":
						if (gui != null) {
							System.out.println("dispose");
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
	
	class Action implements ActionListener{

		JFrame gui;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Address location = new Address();
			location.setSido("서울특별시");
			location.setSigungu((String) cb_area.getSelectedItem());
			
			int type = 0;
			if(cb_type.getSelectedItem() == "한식"){
				type = Category.KOREAN;
			} else if(cb_type.getSelectedItem() == "중식"){
				type = Category.CHINA;
			} else if(cb_type.getSelectedItem() == "일식"){
				type = Category.JAPAN;
			} else if(cb_type.getSelectedItem() == "양식"){
				type = Category.WESTERN;
			}
			
			Evaluation evaluation = new Evaluation();
			double score = 0.0;
			if(cb_score.getSelectedItem() == "☆☆☆☆★"){
				score = 1.0;
			} else if(cb_score.getSelectedItem() == "☆☆☆★★"){
				score = 2.0;
			} else if(cb_score.getSelectedItem() == "☆☆★★★"){
				score = 3.0;
			} else if(cb_score.getSelectedItem() == "☆★★★★"){
				score = 4.0;
			} else if(cb_score.getSelectedItem() == "★★★★★"){
				score = 5.0;
			}
			evaluation.setAverage(score);
			Category category = new Category();
			category.setLocation(location);
			category.setType(type);
			category.setEvaluation(evaluation);
			Member member = LoginStatement.getLoginUser();
			
			if(e.getSource() == btn_random){
				boolean isRandom = true;
				manager.askRestaurant(ClientReceiver.MAIN_GUI_ID, category, member, isRandom);
				dispose();
			} else if(e.getSource() == btn_user){
				boolean isRandom = false;
				manager.askRestaurant(ClientReceiver.MAIN_GUI_ID, category, member, isRandom);
				System.out.println("접속자추천");
				dispose();
				//현재 접속자들에게 추천요청메시지 broadcast
			} 
		}
		
	}
	

}
