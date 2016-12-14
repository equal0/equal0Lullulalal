package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import client.ClientManager;
import client.ClientReceiver;
import vo.Address;
import vo.Category;
import vo.Evaluation;
import vo.Member;
import vo.Restaurant;

public class UserEvaluationsGUI extends JFrame {
	private int guiId = ClientReceiver.getGuiID();
	private LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();
	private ClientManager manager = new ClientManager();

	private JButton score5;
	private JButton score4;
	private JButton score3;
	private JButton score2;
	private JButton score1;
	private JTextArea comments;
	private JButton btn_insert;
	private JButton btn_cancel;
	private String selected;
	private Evaluation evaluation = new Evaluation();
	private Restaurant restaurant = new Restaurant();

	public UserEvaluationsGUI() {
		ClientReceiver.addQueue(guiId, queue);
		new Thread(new Handler(this)).start();

		this.setBounds(500, 200, 250, 230);
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		north();
		south();
		center();
		setScoreButton();
		this.setVisible(true);
	}

	private class Handler implements Runnable {
		JFrame gui;

		public Handler(JFrame gui) {
			this.gui = gui;
		}

		@Override
		public void run() {
			while (true) {
				try {
					Object[] receive = (Object[]) queue.take();
					String proto = (String) receive[1];
					switch (proto) {
					case "evaluate":
						boolean result = (boolean) receive[2];
						if(result){
							JOptionPane.showMessageDialog(gui, "��ϿϷ�!");
							return;
						}else
							JOptionPane.showMessageDialog(gui, "�� ���� ����� �� �ֽ��ϴ�");
						break;
					case "exit":
						if (gui != null) {
							gui.dispose();
							System.exit(0);
						}
						return;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void north() {
		JPanel north = new JPanel();
		this.add(north, BorderLayout.NORTH);

		score5 = new JButton("5");
		score4 = new JButton("4");
		score3 = new JButton("3");
		score2 = new JButton("2");
		score1 = new JButton("1");

		north.add(score5);
		north.add(score4);
		north.add(score3);
		north.add(score2);
		north.add(score1);

		score5.addActionListener(new Action());
		score4.addActionListener(new Action());
		score3.addActionListener(new Action());
		score2.addActionListener(new Action());
		score1.addActionListener(new Action());
	}

	public void center() {
		JScrollPane scroll = new JScrollPane();
		comments = new JTextArea();
		scroll.setViewportView(comments);
		this.add(scroll, BorderLayout.CENTER);
	}

	public void south() {
		JPanel south = new JPanel();
		this.add(south, BorderLayout.SOUTH);

		btn_insert = new JButton("insert");
		btn_cancel = new JButton("cancel");

		south.add(btn_insert);
		south.add(btn_cancel);

		btn_insert.addActionListener(new Action());
		btn_cancel.addActionListener(new Action());
	}

	class Action implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == score5) {
				setScoreButton();
				score5.setBackground(Color.ORANGE);
				score5.setContentAreaFilled(false);
				score5.setOpaque(true);
				selected = "score5";
			} else if (e.getSource() == score4) {
				setScoreButton();
				score4.setBackground(Color.ORANGE);
				selected = "score4";
			} else if (e.getSource() == score3) {
				setScoreButton();
				score3.setBackground(Color.ORANGE);
				selected = "score3";
			} else if (e.getSource() == score2) {
				setScoreButton();
				score2.setBackground(Color.ORANGE);
				selected = "score2";
			} else if (e.getSource() == score1) {
				setScoreButton();
				score1.setBackground(Color.ORANGE);
				selected = "score1";
			} else if (e.getSource() == btn_insert) {
				if (selected == "score5") {
					evaluation.setAverage(5);
				} else if (selected == "score4") {
					evaluation.setAverage(4);
				} else if (selected == "score3") {
					evaluation.setAverage(3);
				} else if (selected == "score2") {
					evaluation.setAverage(2);
				} else if (selected == "score1") {
					evaluation.setAverage(1);
				}
				if (comments != null) {
					Member user = new Member();
					user.setId("lullulalal");
					evaluation.setUser(user); // Member �̱����������� Ŭ��������� ����� ����
					evaluation.setComment(comments.getText());
					
					Category category = new Category();
					Address location = new Address("����Ư���� ���α� ���Ϲ���12�� 4");
					category.setLocation(location);
					restaurant.setCategory(category);
					//�������GUIŬ������ �����ڸ� ȣ���Ͽ� �ٷ� ����� ����
					
					manager.evaluateRestaurant(guiId, evaluation, restaurant);
				}
			} else if (e.getSource() == btn_cancel) {
				ClientReceiver.deleteQueue(guiId);
			}
		}
	}

	public void setScoreButton() {
		score5.setBackground(Color.LIGHT_GRAY);
		score4.setBackground(Color.LIGHT_GRAY);
		score3.setBackground(Color.LIGHT_GRAY);
		score2.setBackground(Color.LIGHT_GRAY);
		score1.setBackground(Color.LIGHT_GRAY);
	}
}