import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;


public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField txtname;
	private JTextField txtaddress;
	private JLabel lblIpAddress;
	private JLabel lblPort;
	private JTextField txtport;
	private JLabel lbleg;
	private JLabel lbleg_1;
	
	public Login() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 280, 407);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtname = new JTextField();
		txtname.setBounds(64, 78, 138, 19);
		contentPane.add(txtname);
		txtname.setColumns(10);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(64, 51, 70, 15);
		contentPane.add(lblName);
		
		txtaddress = new JTextField();
		txtaddress.setBounds(64, 153, 138, 19);
		contentPane.add(txtaddress);
		txtaddress.setColumns(10);
		
		lblIpAddress = new JLabel("IP Address:");
		lblIpAddress.setBounds(64, 126, 114, 15);
		contentPane.add(lblIpAddress);
		
		lblPort = new JLabel("Port:");
		lblPort.setBounds(64, 213, 114, 15);
		contentPane.add(lblPort);
		
		txtport = new JTextField();
		txtport.setColumns(10);
		txtport.setBounds(64, 240, 138, 19);
		contentPane.add(txtport);
		
		lbleg = new JLabel("(eg. 192.168.0.2)");
		lbleg.setBounds(64, 172, 138, 19);
		contentPane.add(lbleg);
		
		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name=txtname.getText();
				String address=txtaddress.getText();
				int port=Integer.parseInt(txtport.getText());
				login(name,address,port);
			}
		});
		btnNewButton.setBounds(90, 311, 88, 25);
		contentPane.add(btnNewButton);
		
		lbleg_1 = new JLabel("(eg. 4444)");
		lbleg_1.setBounds(64, 260, 138, 19);
		contentPane.add(lbleg_1);
	}
	private void login(String name, String address, int port) {
		dispose();
		new ClientWindow(name,address,port);
//		ClientWindow frame = new ClientWindow(name,address,port);
//		frame.setVisible(true);
		
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
