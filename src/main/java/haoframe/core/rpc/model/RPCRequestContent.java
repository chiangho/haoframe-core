package haoframe.core.rpc.model;

public class RPCRequestContent {

	private String className;
	private String method;
	private byte[] argsTypeByteArray;
	private byte[] argsByteArray;
	
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public byte[] getArgsTypeByteArray() {
		return argsTypeByteArray;
	}
	public void setArgsTypeByteArray(byte[] argsTypeByteArray) {
		this.argsTypeByteArray = argsTypeByteArray;
	}
	public byte[] getArgsByteArray() {
		return argsByteArray;
	}
	public void setArgsByteArray(byte[] argsByteArray) {
		this.argsByteArray = argsByteArray;
	}
	
}
