package haoframe.core.rpc.client;

import java.lang.reflect.Method;
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
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibProxy implements MethodInterceptor {

	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	public Class<?> tagClass;
	
	public CglibProxy(Class<?> tagClass) {
		this.tagClass = tagClass;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get() {
		Enhancer enhancer = new Enhancer();
		 //生成指定类对象的子类,也就是重写类中的业务函数，在重写中加入intercept()函数而已。   
        enhancer.setSuperclass(tagClass);  
        //这里是回调函数，enhancer中肯定有个MethodInterceptor属性。   
        //回调函数是在setSuperclass中的那些重写的方法中调用---猜想   
        enhancer.setCallback(this);  
        //创建这个子类对象   
        return (T) enhancer.create();
	}
//	
	@Override
	public Object intercept(Object obj, Method method, Object[] args,MethodProxy proxy) throws Throwable {
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
								String  e =  (String) ObjectUtil.byteArrayToObject(request.getData());
								log.error(Base64.decodeToString(e));
								throw  new HaoException(e);
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

}
