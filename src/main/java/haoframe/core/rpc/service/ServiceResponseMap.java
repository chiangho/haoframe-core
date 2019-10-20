package haoframe.core.rpc.service;

import java.util.concurrent.ConcurrentHashMap;

import haoframe.core.rpc.model.RPCRequest;

public class ServiceResponseMap {
	
	
	private static ConcurrentHashMap<String, RPCRequest> map = new ConcurrentHashMap<String,RPCRequest>();
	
	public static void create(String id,RPCRequest r) {
		map.put(id,r);
	}
	
	
	public static void remove(String id) {
		map.remove(id);
	}


	public static RPCRequest get(String id) {
		return map.get(id);
	}
	
}
