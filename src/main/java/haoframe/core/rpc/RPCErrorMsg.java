package haoframe.core.rpc;

public enum RPCErrorMsg {

	//未能找到接口
	no_find_interface("000001","未能找到接口"),
	//未能找到下行通道
	no_find_down_channel("000002","未能找到下行通道"), 
	//请求超时
	request_out_time("000003","请求超时"), 
	//执行异常
	execute_error("000004","执行异常");
	
	private String code;
	private String msg;
	private RPCErrorMsg(String code,String msg) {
		this.code  = code;
		this.msg   = msg;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public static RPCErrorMsg get(String code) {
		for(RPCErrorMsg r:RPCErrorMsg.values()) {
			if(r.getCode().equals(code)) {
				return r;
			}
		}
		return null;
	}
	
}
