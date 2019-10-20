package haoframe.core.rpc.service;

import haoframe.core.rpc.codec.MsgpackEncoder;
import haoframe.core.rpc.codec.MsgpackRequestDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));    
		ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4)); 
		ch.pipeline().addLast(new IdleStateHandler(20,0,0));
//		ch.pipeline().addLast(new OutBoundHandler());
		ch.pipeline().addLast(new MsgpackEncoder());
		ch.pipeline().addLast(new MsgpackRequestDecoder());
		ch.pipeline().addLast(new RPCServiceHandler());
	}

}
