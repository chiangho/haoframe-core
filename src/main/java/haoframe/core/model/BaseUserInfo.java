package haoframe.core.model;

import java.io.Serializable;

/**
 *   用户信息
 * @author chianghao
 *
 */
public class BaseUserInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String userId;   //用户编号
	
	private String userName; //用户姓名
	
	private String appName;//所属平台
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
}
