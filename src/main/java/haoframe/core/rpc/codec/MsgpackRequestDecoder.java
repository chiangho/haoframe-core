package haoframe.core.rpc.codec;

import java.util.List;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import haoframe.core.rpc.model.RPCRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class MsgpackRequestDecoder extends MessageToMessageDecoder<ByteBuf> {

	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		int index = msg.readerIndex();
		final byte[] array;
		final int length = msg.readableBytes();
		array = new byte[length];
		msg.getBytes(index, array,0,length);
		MessagePack pack = new MessagePack();
		try {
			RPCRequest request  = pack.read(array, RPCRequest.class);
			out.add(request);
		}catch (Exception e) {
			log.error("消息解析异常",e);
			throw e;
		}
	}
}
