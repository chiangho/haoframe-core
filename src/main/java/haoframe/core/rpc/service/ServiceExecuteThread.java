package haoframe.core.rpc.service;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.alibaba.fastjson.JSON;

import haoframe.core.rpc.RPCErrorMsg;
import haoframe.core.rpc.ServiceRegister;
import haoframe.core.rpc.model.RPCRequest;
import haoframe.core.rpc.model.RPCRequestContent;
import haoframe.core.rpc.model.ServiceInfo;
import io.netty.channel.Channel;

/**
 * 请求接口执行
 * @author Administrator
 *
 */
public class ServiceExecuteThread  implements Runnable  {

	private Channel upChannel;
	
	private Channel downChannel;
	
	private RPCRequest request;
	
	
	
	public ServiceExecuteThread(Channel upChannel,RPCRequest request) throws Exception {
		this.upChannel   = upChannel;
		RPCRequestContent content = JSON.parseObject(request.getContnent(), RPCRequestContent.class);
		ServiceInfo serviceInfo = ServiceRegister.get(content.getClassName());
		if(serviceInfo==null) {
			upChannel.writeAndFlush(createRequest(false,RPCErrorMsg.no_find_interface));
			throw new Exception("未能找到对应的接口服务");
		}
		downChannel = RPCService.getChannel(serviceInfo.getApp());
		content.setClassName(serviceInfo.getImplementationClassName());
		request.setContnent(JSON.toJSONString(content));
		this.request   = request;
	}
	
	
	private RPCRequest createRequest(boolean success,RPCErrorMsg error) {
		RPCRequest backData  = new RPCRequest();
		backData.setType(1);
		backData.setId(request.getId());
		backData.setSuccess(success);
		backData.setErrorCode(error.getCode());
		return backData;
	}
	
	
	@Override
	public void run() {
		try {
			if(downChannel==null) {
				upChannel.writeAndFlush(createRequest(false,RPCErrorMsg.no_find_down_channel));
			}else {
				downChannel.writeAndFlush(request);
				FutureTask<RPCRequest> ft = new FutureTask<RPCRequest>( new Callable<RPCRequest>() {
					@SuppressWarnings("static-access")
					@Override
					public RPCRequest call() throws Exception {
						long beginTime  = System.currentTimeMillis();
						while(true) {
							RPCRequest o = ServiceResponseMap.get(request.getId());
							if(o != null) {
								return o;
							}
							if(System.currentTimeMillis()-beginTime>(15*1000)) {
								break;
							}
							Thread.currentThread().sleep(500);
						}
						//构建超时异常
						return createRequest(false,RPCErrorMsg.request_out_time);
					}}
				);
				ft.run();
				RPCRequest backDate = ft.get();
				upChannel.writeAndFlush(backDate);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
