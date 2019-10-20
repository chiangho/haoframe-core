package haoframe.core.rpc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
 
 
public class OutBoundHandler extends ChannelOutboundHandlerAdapter {
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg,ChannelPromise promise) throws Exception {
		ctx.write(msg,promise);
	    promise.addListener(new GenericFutureListener<Future<? super Void>>() {
	        @Override
	        public void operationComplete(Future<? super Void> future) throws Exception {
	            if(future.isSuccess()){
	                log.info("下发成功");
	            }
	        }
	    });
	}
}
