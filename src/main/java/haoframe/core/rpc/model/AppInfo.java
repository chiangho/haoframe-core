package haoframe.core.rpc.model;

import java.io.Serializable;

import org.msgpack.annotation.Message;

/**
 * 程序的登记信息
 * @author chianghao
 *
 */
@Message
public class AppInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String appName;
	private String machineCode;
	
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getMachineCode() {
		return machineCode;
	}
	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}
	
	@Override
	public String toString() {
		return "AppInfo [appName=" + appName + ", machineCode=" + machineCode + "]";
	}
	
}
