package haoframe.core.rpc.client;

import java.util.concurrent.ConcurrentHashMap;

import haoframe.core.rpc.model.RPCRequest;

public class ResponseMap {
	
	
	
	
	
	private static ConcurrentHashMap<String, ResponseData> map = new ConcurrentHashMap<String,ResponseData>();
	
	
	public static void setReturnData(String id,RPCRequest data) {
		ResponseData rd = new ResponseData();
		rd.setRequest(data);
		rd.setIsBack(1);
		map.put(id,rd);
	}
	
	public static ResponseData getReturnData(String id) {
		if(map.containsKey(id)) {
			return map.get(id);
		}
		return null;
	}
	
	public static void remove(String id) {
		map.remove(id);
	}
	
}
