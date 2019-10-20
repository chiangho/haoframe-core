package haoframe.core.rpc.client;

import haoframe.core.rpc.model.RPCRequest;

public class ResponseData {

	//是否返回 0 表示未返回结果，1表示返回结果
	private int isBack;
	//返回的数据
	private RPCRequest request;
	
	public int getIsBack() {
		return isBack;
	}
	public void setIsBack(int isBack) {
		this.isBack = isBack;
	}
	public RPCRequest getRequest() {
		return request;
	}
	public void setRequest(RPCRequest request) {
		this.request = request;
	}
}
