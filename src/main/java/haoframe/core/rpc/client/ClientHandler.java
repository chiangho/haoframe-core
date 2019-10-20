package haoframe.core.rpc.client;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import haoframe.core.rpc.ActionEnum;
import haoframe.core.rpc.model.AppInfo;
import haoframe.core.rpc.model.RPCRequest;
import haoframe.core.rpc.model.RPCRequestContent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.close();
	}

	public RPCRequest createRequest(ActionEnum a, Object obj) {
		RPCRequest r = new RPCRequest();
		r.setId(UUID.randomUUID().toString());
		r.setAction(a.getCode());
		r.setContnent(JSON.toJSONString(obj));
		return r;
	}

	// 连接立刻发送等级注册事件
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		RPCClient.channel.getAndSet(ctx.channel());
		AppInfo a = new AppInfo();
		a.setAppName(RPCClient.client.getAppName());
		a.setMachineCode("");
		//登记注册所有服务
		//RPCClient.setRegisterServiceInfo(RPCClient.getServiceInfoList());
		ctx.writeAndFlush(createRequest(ActionEnum.register, a));
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		RPCRequest request = (RPCRequest) msg;
		// 接收到请求信息
		if (request.getType() == 0) {
			switch (ActionEnum.get(request.getAction())) {
			case requst:
				log.info("=============={}============",JSON.toJSONString(msg));
				invokeMethod(ctx.channel(), request.getId(), request.getContnent());
				break;
			default:
				break;
			}
		}
		// 接受到响应消息
		if (request.getType() == 1) {
			log.info("=============={}============",JSON.toJSONString(msg));
			setReturnData(request.getId(),request);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
		log.info("通道处理发生异常，做释放处理");
		e.printStackTrace();
		ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				RPCClient.client.connect();
			} else if (event.state() == IdleState.WRITER_IDLE) {
				ctx.writeAndFlush(createRequest(ActionEnum.lineCheck, ""));
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	private void setReturnData(String id,RPCRequest request) {
		ResponseMap.setReturnData(id, request);
	}

	private void invokeMethod(Channel channel, String requestId, String content) {
		RPCRequestContent rc = JSON.parseObject(content, RPCRequestContent.class);
		ExecuteThread e = new ExecuteThread(channel, requestId, rc);
		try {
			HandlerTask.queue.put(e);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

}
