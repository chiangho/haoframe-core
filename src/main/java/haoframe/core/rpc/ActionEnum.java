package haoframe.core.rpc;

public enum ActionEnum {

	//登记
	register(1),
	//请求接口
	requst(2),
	//心跳检查
	lineCheck(3),
	//
	serviceRegister(4)
	;
	
	private int code;
	private ActionEnum(int code) {
		this.code = code;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	public static ActionEnum get(int code) {
		for(ActionEnum e:ActionEnum.values()) {
			if(e.getCode()==code) {
				return e;
			}
		}
		return null;
	}
}
