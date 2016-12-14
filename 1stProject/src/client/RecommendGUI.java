package client;

import java.awt.BorderLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

public class RecommendGUI extends JDialog{
	
	private JPanel north;
	private JPanel pnl_area;
	private JPanel pnl_type;
	private JPanel pnl_score;
	private JComboBox<String> cb_area;
	private JComboBox<String> cb_type;
	private JComboBox<String> cb_score;
	
	private JPanel panel;
	private JLabel lbl_random;
	private JLabel lbl_user;
	private JButton btn_random;
	private JButton btn_user;

	public RecommendGUI() {
		//super(main, Dialog.ModalityType.APPLICATION_MODAL);
		this.setSize(320,264);
		
		north = new JPanel();
		getContentPane().add(north,BorderLayout.NORTH);
		
		pnl_area = new JPanel();
		cb_area = new JComboBox<>();
		cb_area.setFont(new Font("���� ����", Font.PLAIN, 11));
		cb_area.setModel(new DefaultComboBoxModel<>(new String[] {"����","������", "������", "���ϱ�", "������", "���Ǳ�", "������", "���α�", "��õ��", "�����", "������", "���빮��", "���۱�",
						"������", "���빮��", "���ʱ�", "������", "���ϱ�", "���ı�", "��õ��", "��������", "��걸", "����", "���α�", "�߱�", "�߶���"}));
		pnl_type = new JPanel();
		cb_type = new JComboBox<>();
		cb_type.setFont(new Font("���� ����", Font.PLAIN, 11));
		cb_type.setModel(new DefaultComboBoxModel<>(new String[] {"����","�ѽ�","�߽�","�Ͻ�","���"}));
		
		pnl_score = new JPanel();
		cb_score = new JComboBox<>();
		cb_score.setFont(new Font("���� ����", Font.PLAIN, 11));
		cb_score.setModel(new DefaultComboBoxModel<>(new String[] {"����","�١١١١�","�١١١ڡ�","�١١ڡڡ�","�١ڡڡڡ�","�ڡڡڡڡ�"}));
		
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
		
		btn_random = new JButton("�������� ��õ�ޱ�");
		btn_random.setFont(new Font("���� ����", Font.PLAIN, 12));
		btn_random.setBounds(95, 20, 133, 36);
		panel.add(btn_random);
		
		btn_user = new JButton("�����ڿ��� ��õ�ޱ�");
		btn_user.setFont(new Font("���� ����", Font.PLAIN, 12));
		btn_user.setBounds(95, 82, 133, 36);
		panel.add(btn_user);
		
		//actionListener
		btn_random.addActionListener(new Action());
		btn_user.addActionListener(new Action());

		this.setVisible(true);
	}
	
	class Action implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == btn_random){
				String area = (String) cb_area.getSelectedItem();
				String type = (String) cb_type.getSelectedItem();
				double score = 0.0;
				if(cb_score.getSelectedItem() == "�١١١١�"){
					score = 1.0;
				} else if(cb_score.getSelectedItem() == "�١١١ڡ�"){
					score = 2.0;
				} else if(cb_score.getSelectedItem() == "�١١ڡڡ�"){
					score = 3.0;
				} else if(cb_score.getSelectedItem() == "�١ڡڡڡ�"){
					score = 4.0;
				} else if(cb_score.getSelectedItem() == "�ڡڡڡڡ�"){
					score = 5.0;
				} RandomRecommGUI randomGui = new RandomRecommGUI(area, type, score);
				randomGui.setVisible(true);
			}
		}
		
	}
	
	public static void main(String[] args) {
		new RecommendGUI();
	}
	

}