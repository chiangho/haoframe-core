package haoframe.core.rpc.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import haoframe.core.rpc.RPCErrorMsg;
import haoframe.core.rpc.model.RPCRequest;
import haoframe.core.rpc.model.RPCRequestContent;
import io.netty.channel.Channel;
import jodd.util.ObjectUtil;

/**
 * 请求接口执行
 * @author Administrator
 *
 */
public class ExecuteThread  implements Runnable  {

	
	Logger log  = LoggerFactory.getLogger(this.getClass());
	
	private Channel channel;
	
	private String requestId;
	
	private RPCRequestContent content;
	
	
	public ExecuteThread(Channel channel,String requestId,RPCRequestContent request) {
		this.channel = channel;
		this.requestId = requestId;
		this.content = request;
	}
	
	private Class<?>[]  parseArgeTypes(byte[] array) throws ClassNotFoundException, IOException{
		 Class<?>[] argsType= (Class<?>[]) ObjectUtil.byteArrayToObject(array);
		 return argsType;
	}
	
	private Object[]  parseArge(byte[] array) throws ClassNotFoundException, IOException{
		 Object[] args= (Object[]) ObjectUtil.byteArrayToObject(array);
		 return args;
	}
	
	@Override
	public void run() {
		try {
			Class<?> service  = Class.forName(content.getClassName());
			String method = content.getMethod();
			Class<?>[] argsTypes = parseArgeTypes(this.content.getArgsTypeByteArray());
			Object[] args = parseArge(this.content.getArgsByteArray());
			Method m = service.getMethod(method, argsTypes);
			Object data = m.invoke(service.newInstance(), args);
			byte[] dataByte = ObjectUtil.objectToByteArray(data);
			RPCRequest r=  createBackRequest(dataByte);
			channel.writeAndFlush(r);
		} catch (Exception e) {
			log.error("执行异常",e);
			RPCRequest r=  ceeateErrorRequest(e);
			channel.writeAndFlush(r);
		}
		
	}
	
	
	
	/**
	 * 将 Exception 转化为 String 
	 */
	public String getExceptionToString(Throwable e) {
		if (e == null){
			return "";
		}
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		String errorString  = stringWriter.toString();
		System.out.println("==============error=================="+errorString);
		return errorString;
	}
	
	
	private RPCRequest ceeateErrorRequest(Exception e) {
		RPCRequest r = new RPCRequest();
		r.setId(this.requestId);
		r.setSuccess(false);
		r.setType(1);
		r.setErrorCode(RPCErrorMsg.execute_error.getCode());
		byte[] dataByte = null;
		try {
			dataByte = ObjectUtil.objectToByteArray(e);
			r.setData(dataByte);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return r;
	}
	
	private RPCRequest createBackRequest(byte[] data) {
		RPCRequest r = new RPCRequest();
		r.setId(this.requestId);
		r.setSuccess(true);
		r.setType(1);
		r.setData(data);
		return r;
	}
	
}
