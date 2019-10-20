package haoframe.core.rpc.model;

public class ServiceInfo {

	//应用
	private String app;
	
	//接口类
	private String interfaceClassName;
	
	//接口实现类
	private String ImplementationClassName;
	
	
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public String getInterfaceClassName() {
		return interfaceClassName;
	}
	public void setInterfaceClassName(String interfaceClassName) {
		this.interfaceClassName = interfaceClassName;
	}
	public String getImplementationClassName() {
		return ImplementationClassName;
	}
	public void setImplementationClassName(String implementationClassName) {
		ImplementationClassName = implementationClassName;
	}
	
	
	
	
}
