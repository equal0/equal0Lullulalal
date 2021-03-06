package client.gui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import client.ClientManager;
import client.ClientReceiver;
import client.Config;
import vo.Member;

public class LogInGui extends JDialog implements ActionListener{

	private static final int WIDTH = 300;
	private static final int HEIGHT = 250;
	
	private ClientManager manager = new ClientManager();
	private JTextField tf_id;
	private JPasswordField tf_pwd;
	private JCheckBox chkbox_autologin ;

	public LogInGui(MainGui mainGui) {
		super(mainGui, Dialog.ModalityType.APPLICATION_MODAL);
		
		getContentPane().setFont(new Font("�����ٸ�����", Font.PLAIN, 12));
		getContentPane().setLayout(null);
		
		
		Font font = new Font("�����ٸ�����", Font.PLAIN, 12);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 292, 223);
		panel.setBackground(new Color(255, 255, 255));
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton btn_login = new JButton("\uB85C\uADF8\uC778");
		btn_login.setBounds(158, 126, 57, 25);
		panel.add(btn_login);
		btn_login.setFont(font);
		btn_login.addActionListener(this);
	
		
		chkbox_autologin = new JCheckBox("\uC790\uB3D9 \uB85C\uADF8\uC778");
		chkbox_autologin.setBounds(21, 127, 115, 23);
		panel.add(chkbox_autologin);
		chkbox_autologin.setFont(font);
		if (Config.getInstance().getAutoLoginConfig() == true)
			chkbox_autologin.setSelected(true);
		
		
		tf_id = new JTextField();
		tf_id.setBounds(80, 62, 135, 21);
		panel.add(tf_id);
		tf_id.setColumns(10);
		
		tf_pwd = new JPasswordField();
		tf_pwd.setBounds(80, 95, 135, 21);
		panel.add(tf_pwd);
		tf_pwd.setColumns(10);
		  
		Font fontLoginLb = new Font("�����ٸ�����", Font.PLAIN, 25);
		JLabel lb_login = new JLabel("�� �� ��");
		lb_login.setBounds(35, 10, 222, 42);
		lb_login.setFont(fontLoginLb);
		panel.add(lb_login);
		
		ImageIcon idIcon = new ImageIcon("resource/pwd.png");
		JLabel lb_idImg = new JLabel(idIcon);
		lb_idImg.setBounds(21, 50, 57, 42);
		panel.add(lb_idImg);
		
		ImageIcon idPwd = new ImageIcon("resource/key.png");
		JLabel lb_pwdImg = new JLabel(idPwd);
		lb_pwdImg.setBounds(21, 88, 57, 33);
		panel.add(lb_pwdImg);
		
		this.setBounds((int)mainGui.getLocation().getX() + 40, 
				(int)mainGui.getLocation().getY() + 170, WIDTH, HEIGHT);
		
		this.setVisible(true);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "�α���":
			boolean onAutoLogin = chkbox_autologin.isSelected();
			//Config.test();
			Config.getInstance().setAutoLoginConfig(onAutoLogin);
			Member tryLoginUser = new Member();
			tryLoginUser.setId(tf_id.getText());
			tryLoginUser.setPassword(new String(tf_pwd.getPassword()));
			manager.login(ClientReceiver.MAIN_GUI_ID, tryLoginUser);
			this.dispose();
			break;
		}
		
	}
}
