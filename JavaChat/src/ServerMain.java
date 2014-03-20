
public class ServerMain {
	
	private int port;
	private Server server;
	
	public ServerMain(int port) {
		this.port = port;
		System.out.println("Creating new server on port: "+this.port);
		server = new Server(port);
	}
	public static void main(String[] args) {
		int port;
		if(args.length!=1){
			System.out.print("Usage: java -jar Login.jar [port]");
			return;
		}
		port = Integer.parseInt(args[0]);
		new ServerMain(port);
	}
}
