package haoframe.core.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import haoframe.core.exception.HaoException;
import haoframe.core.rpc.ActionEnum;
import haoframe.core.rpc.RPCErrorMsg;
import haoframe.core.rpc.model.RPCRequest;
import haoframe.core.rpc.model.RPCRequestContent;
import jodd.util.Base64;
import jodd.util.ObjectUtil;

public class RPCProxy  implements InvocationHandler{

	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	private RPCProxy(Class<?> tagClass) {
		this.tagClass = tagClass;
	}
	
	private Class<?> tagClass;
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String id =  UUID.randomUUID().toString();
		String serviceName = tagClass.getName();
		String methodName  = method.getName();
		byte[] argsByte    = ObjectUtil.objectToByteArray(args);
		byte[] argsTypeByte= ObjectUtil.objectToByteArray(method.getParameterTypes());
		RPCRequestContent c = new RPCRequestContent();
		c.setArgsByteArray(argsByte);
		c.setArgsTypeByteArray(argsTypeByte);
		c.setClassName(serviceName);
		c.setMethod(methodName);
		
		RPCRequest r = new RPCRequest();
		r.setAction(ActionEnum.requst.getCode());
		r.setContnent(JSON.toJSONString(c));
		r.setId(id);
		r.setType(0);
		
//		ResponseModel rsp = new ResponseModel();
//		rsp.setRequestId(id);
		
		RPCClient.channel.get().writeAndFlush(r);
		
		long beginTime = System.currentTimeMillis();
		
		Object backDate = null;
		FutureTask<Object> ft = new FutureTask<Object>( new Callable<Object>() {
			@SuppressWarnings("static-access")
			@Override
			public Object call() throws Exception {
				while(true) {
					ResponseData o = ResponseMap.getReturnData(id);
					if(o!=null&&o.getIsBack()==1) {
						RPCRequest request = o.getRequest();
						if(request.isSuccess()) {
						   return ObjectUtil.byteArrayToObject(request.getData());
						}else {
							switch(RPCErrorMsg.get(request.getErrorCode())) {
							case no_find_interface:
								throw new Exception(RPCErrorMsg.no_find_interface.getMsg());
							case no_find_down_channel:
								throw new Exception(RPCErrorMsg.no_find_down_channel.getMsg());
							case request_out_time:
								throw new Exception(RPCErrorMsg.request_out_time.getMsg());
							case execute_error:
								Exception  e =  (Exception) ObjectUtil.byteArrayToObject(request.getData());
								throw e;
							}
						}
					}
					Thread.currentThread().sleep(10);
					if((System.currentTimeMillis()-beginTime)>(1000*30)) {
						throw new Exception(RPCErrorMsg.request_out_time.getMsg());
					}
				}
			}}
		);
		ft.run();
		backDate = ft.get();
		ResponseMap.remove(id);
		return backDate;
	}

	
	@SuppressWarnings("unchecked")
	public static <T> T getImplements(Class<?> clazz) {
		RPCProxy proxy  =new RPCProxy(clazz);
		T  i = (T)(Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, proxy));
		return i;
	} 
	
	
	
}
