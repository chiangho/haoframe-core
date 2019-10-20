package haoframe.core.rpc;

import java.util.concurrent.ConcurrentHashMap;

import haoframe.core.rpc.model.ServiceInfo;

/**
 * 服务登记信息
 * @author chianghao
 *
 */
public class ServiceRegister {

	
	
	//服务登记信息
	private final static ConcurrentHashMap<String,ServiceInfo> serviceRegisterMap = new ConcurrentHashMap<String,ServiceInfo>();
	
	
	//登记
	public static void register(ServiceInfo s) {
		serviceRegisterMap.put(s.getInterfaceClassName(), s);
	}
	
	
	//获取
	public static ServiceInfo get(String interfaceName) {
		return serviceRegisterMap.get(interfaceName);
	}
	
	
}
