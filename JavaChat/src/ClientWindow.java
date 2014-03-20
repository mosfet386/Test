import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class ClientWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField txtMessage;
	private JTextArea history;
	private DefaultCaret caret;
	private JPanel contentPane;
	private Client client;
	private Thread listen;
	private boolean running=false;
	
	public ClientWindow(String name, String address, int port) {
		this.client = new Client(name,address,port);
		createWindow();
		boolean connect = client.openConnection(address);
		if(!connect){
			System.err.println("Connection failed!");
			console("Connection failed!");
		}
		console("Connecting user <"+name+"> at address <"+address+":"+port+">");
		String connection = "/c/"+name;
		client.send(connection.getBytes());
		listen();
	}
	private void createWindow() {
		this.setTitle("JavaChat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880,550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{10,830,30,10};
		gbl_contentPane.rowHeights = new int[]{30,480,40};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		history = new JTextArea();
		history.setEditable(false);
		caret = (DefaultCaret)history.getCaret();
		JScrollPane scroll = new JScrollPane(history);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		scrollConstraints.insets = new Insets(0,5,0,0);
		contentPane.add(scroll, scrollConstraints);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					send(txtMessage.getText());
				}
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				send(txtMessage.getText());
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 2;
		contentPane.add(btnNewButton, gbc_btnNewButton);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				String disconnect="/d/"+client.getID()+"/e/";
				client.send(disconnect.getBytes());
				running=false;
				client.close();
			}
		});
		
		setVisible(true);
		txtMessage.requestFocusInWindow();
	}
	public void console(String message) {
			history.append(message+"\n");
			history.setCaretPosition(history.getDocument().getLength());
	}
	public void send(String message) {
		if(message.isEmpty()) return;
		message=client.getName()+": "+message;
		message="/m/"+message;
		client.send(message.getBytes());
		txtMessage.setText("");
	}
	public void listen() {
		listen=new Thread("Listen") {
			public void run(){
				while(running){
					String message=client.receive();
					if(message.startsWith("/c/")){
						client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
						console("Successfully connected to server! ID: "+client.getID());
					} else if (message.startsWith("/m/")) {
						String text = message.substring(3).split("/e/")[0];
						console(text);
					} else if (message.startsWith("/i/")) {
						String text="/i/"+client.getID()+"/e/";
						client.send(text.getBytes());
					}
				}
			}
		};
		running=true;
		listen.start();
	}
}