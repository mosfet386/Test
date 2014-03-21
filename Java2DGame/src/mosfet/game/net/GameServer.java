package mosfet.game.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import mosfet.game.Game;
import mosfet.game.entities.PlayerMP;
import mosfet.game.net.packets.Packet;
import mosfet.game.net.packets.Packet.PacketTypes;
import mosfet.game.net.packets.Packet00Login;

public class GameServer extends Thread{
	private DatagramSocket socket;
	private Game game;
	Packet packet=null;
	private List<PlayerMP> connectedPlayers=new ArrayList<PlayerMP>();
	
	public void addConnection(PlayerMP player,Packet00Login packet){
		 boolean alreadyConnected=false;
		 for(PlayerMP p:connectedPlayers){
			 //is player already connected?
			 if(player.getUsername().equalsIgnoreCase(p.getUsername())){
				 //do we have the connected players IP?
				 if(p.ipAddress==null){p.ipAddress=player.ipAddress;}
				 if(p.port==-1){p.port=player.port;}
				 alreadyConnected=true;
			 }else{
				 //forward new player connection request to existing players
				 sendData(packet.getData(),p.ipAddress,p.port);
				 packet=new Packet00Login(p.getUsername());
				 sendData(packet.getData(),player.ipAddress,player.port);
			 }
		 }
		 if(!alreadyConnected){
			connectedPlayers.add(player);
			//alreadyconnected
		 }
	}
	public void sendDataToAllClients(byte[] data) {
		for(PlayerMP p:connectedPlayers){sendData(data,p.ipAddress,p.port);}
	}
	public void sendData(byte[] data,InetAddress ipAddress, int port){
		DatagramPacket packet=new DatagramPacket(data,data.length,ipAddress,port);
		try {socket.send(packet);}
		catch (IOException e){e.printStackTrace();}
	}
	public void parsePacket(byte[] data,InetAddress address,int port){
		String message=new String(data).trim();
		PacketTypes type=Packet.lookupPacket(message.substring(0,2));
		switch(type){
			case INVALID: break;
			case LOGIN: 
				this.packet=new Packet00Login(data); 
				System.out.println("["+address.getHostAddress()+":"+port+"]"+
									((Packet00Login)packet).getUsername()+" has connected...");
				PlayerMP player=new PlayerMP(game.level,100,100,
									((Packet00Login)packet).getUsername(),address,port);
				addConnection(player,(Packet00Login)packet);
				break;
			case DISCONNECT: break;
			default:
		}
	}
	public void run(){
		while(true){
			byte[] data=new byte[1024];
			DatagramPacket packet=new DatagramPacket(data,data.length);
			try {socket.receive(packet);} 
			catch (IOException e){e.printStackTrace();}
			this.parsePacket(packet.getData(),packet.getAddress(),packet.getPort());
//			String message=new String(packet.getData());
//			System.out.println("Client["+packet.getAddress().getHostAddress()+":"
//													+packet.getPort()+"]: "+message);
//			if(message.trim().equals("ping")){
//				System.out.println("From Client: "+message);
//				sendData("pong".getBytes(),packet.getAddress(),packet.getPort());
//			}
		}
	}
	public GameServer(Game game){
		this.game=game;
		try {this.socket=new DatagramSocket(5335);}
		catch (SocketException e){e.printStackTrace();} 
	}
}
