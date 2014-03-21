package mosfet.game.net.packets;

import mosfet.game.net.GameClient;
import mosfet.game.net.GameServer;

public class Packet01Disconnect extends Packet{
	private String username;
	
	public String getUsername(){
		return username;
	}
	@Override
	public byte[] getData() {
		return ("01"+this.username).getBytes();
	}
	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}
	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}
	public Packet01Disconnect(String username) {
		super(01);
		this.username=username;
	}
	public Packet01Disconnect(byte[] data) {
		super(01);
		this.username=readData(data);
	}
}
