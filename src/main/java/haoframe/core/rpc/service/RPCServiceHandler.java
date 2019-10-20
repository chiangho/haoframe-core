package haoframe.core.rpc.service;

import java.util.List;

import org.apache.dubbo.common.utils.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import haoframe.core.rpc.ActionEnum;
import haoframe.core.rpc.ServiceRegister;
import haoframe.core.rpc.model.AppInfo;
import haoframe.core.rpc.model.RPCRequest;
import haoframe.core.rpc.model.ServiceInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class RPCServiceHandler extends ChannelInboundHandlerAdapter{

	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		RPCRequest request = (RPCRequest) msg;
		//接收到请求信息
		try {
			if(request.getType()==0) {
				
				switch(ActionEnum.get(request.getAction())) {
				case register:
					register(ctx,request.getContnent());
					break;
				case requst:
					invokeMethod(ctx.channel(),request);
					break;
				case serviceRegister:
					serviceRegister(request.getContnent());
					break;
				case lineCheck:
					ctx.writeAndFlush(msg);
					break;
				default:
					log.info("指定的动作不存在。the action code is {}",request.getAction());
					break;
				}
				
			}
			//接受到响应消息
			if(request.getType()==1) {
				setReturnData(request.getId(),request);
			}
		}catch(Exception e) {
			log.error("处理异常",e);
			log.error("this request message is {}",JSON.toJSONString(msg));
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
		e.printStackTrace();
		ctx.close();
	}

	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.READER_IDLE){
            	//将通道关闭
            	ctx.close();
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }
	
	
	private void serviceRegister(String content) {
		log.info("接收到接口登记信息为{}",content);
		List<ServiceInfo> list = JSON.parseArray(content, ServiceInfo.class);
		for(ServiceInfo s:list) {
			ServiceRegister.register(s);
		}
	}
	
	private void setReturnData(String id,RPCRequest request) {
		ServiceResponseMap.create(id, request);
	}
	
	private void invokeMethod(Channel channel,RPCRequest request) {
		ServiceExecuteThread e =  null;
		try {
			e = new ServiceExecuteThread(channel,request);
		}catch (Exception e1) {
			e1.printStackTrace();
		}
		if(e!=null) {
			try {
				ServiceHandlerTask.queue.put(e);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	

	private void register(ChannelHandlerContext ctx,String content) {
		AppInfo appInfo = JSON.parseObject(content, AppInfo.class);
		ConcurrentHashSet<Channel> appObjectHashCodesSet  = RPCService.appChannelMap.get(appInfo.getAppName());
		if(appObjectHashCodesSet==null) {
			appObjectHashCodesSet =  new ConcurrentHashSet<Channel>();
		}
		appObjectHashCodesSet.add(ctx.channel());
		RPCService.appChannelMap.put(appInfo.getAppName(),appObjectHashCodesSet );
	}

}
