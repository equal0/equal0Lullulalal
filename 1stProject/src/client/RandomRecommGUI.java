package client;

import java.awt.BorderLayout;
import java.awt.Dialog;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RandomRecommGUI extends JDialog{
	
	private String area;
	private String type;
	private double score;

	public RandomRecommGUI(String area, String type, double score) {
		//super(main, Dialog.ModalityType.APPLICATION_MODAL);
		this.area = area;
		this.type = type;
		this.score = score;
		
		this.setSize(320,400);

		//north �󺧶���
		JPanel north = new JPanel();
		this.add(north, BorderLayout.NORTH);
		
		JLabel lbl_area = new JLabel("("+area+")"+"������");
		JLabel lbl_score = new JLabel(score+"�̻�");
		JLabel lbl_type = new JLabel(type+"����");
		
		north.add(lbl_area);
		north.add(lbl_score);
		north.add(lbl_type);
		
		//center ���� �Ұ� ����_�Ұ� ������ ������� ������ ȭ������
		
		//south �ٸ� ��õ �ޱ��ư, �Ϸ��ư

		this.setVisible(true);
	
	}
	
	public static void main(String[] args) {
		new RandomRecommGUI("������", "�߽�", 5.0); 
	}
	
}
