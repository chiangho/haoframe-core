package haoframe.core.rpc.model;

public class ResponseModel {

	private String requestId;
	private Object returnData;
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public Object getReturnData() {
		return returnData;
	}
	public void setReturnData(Object returnData) {
		this.returnData = returnData;
	}
	
	
	
}
