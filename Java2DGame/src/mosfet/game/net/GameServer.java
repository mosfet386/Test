package mosfet.game.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import mosfet.game.Game;

public class GameServer extends Thread{
	private DatagramSocket socket;
	private Game game;
	
	public void sendData(byte[] data,InetAddress ipAddress, int port){
		DatagramPacket packet=new DatagramPacket(data,data.length,ipAddress,port);
		try {socket.send(packet);}
		catch (IOException e){e.printStackTrace();}
	}
	public void run(){
		while(true){
			byte[] data=new byte[1024];
			DatagramPacket packet=new DatagramPacket(data,data.length);
			try {socket.receive(packet);} 
			catch (IOException e){e.printStackTrace();}
			String message=new String(packet.getData());
			System.out.println("Client["+packet.getAddress().getHostAddress()+":"
													+packet.getPort()+"]: "+message);
			if(message.trim().equals("ping")){
				System.out.println("From Client: "+message);
				sendData("pong".getBytes(),packet.getAddress(),packet.getPort());
			}
		}
	}
	public GameServer(Game game){
		this.game=game;
		try {this.socket=new DatagramSocket(5335);}
		catch (SocketException e){e.printStackTrace();} 
	}
}
