package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.JComboBox;

public class insertUserEvaluations extends JFrame{
	private JComboBox combo_score;
	private JButton btn_insert;
	private JButton btn_cancel;
	
	
	public insertUserEvaluations() {
		this.setBounds(500, 200, 250, 300);
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		North();
		south();
		this.setVisible(true);
	}
	
	public void North(){
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		combo_score = new JComboBox();
		ImageIcon image = new ImageIcon("img\test.png");
		combo_score.addItem(image);
		panel.add(combo_score);
	}
	
	public void south(){
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btn_insert = new JButton("\uB4F1\uB85D");
		panel_1.add(btn_insert);
		
		btn_cancel = new JButton("\uCDE8\uC18C");
		panel_1.add(btn_cancel);
	}

	
	
	
	
	class Action implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
		}
		
	}
	
	public static void main(String[] args) {
		new insertUserEvaluations();
	}
}