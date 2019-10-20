package haoframe.core.rpc.model;

import org.msgpack.annotation.Message;

@Message
public class RPCRequest {

	//请求ID
	private String id;
	
	//请求动作
	private int action;
	
	//请求内容 json 数据, 需要使用RPCRequestContent 序列化和反序列化
	private String contnent;
	
	//true 请求成功   false 请求失败
	private boolean success;
	
	//返回的错误码
	private String errorCode;
	
	//返回数据
	private byte[] data;
	
	//0 请求  1响应
	private int type;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public String getContnent() {
		return contnent;
	}
	public void setContnent(String contnent) {
		this.contnent = contnent;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
}
