package haoframe.core.rpc.service;

public class RPCConfig {

	
	public static final int DEFAULT_PORT=9004;
	
	private int port=DEFAULT_PORT;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}
