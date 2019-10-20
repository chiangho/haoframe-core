package haoframe.core.rpc.codec;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MsgpackEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		MessagePack pack = new MessagePack();
		byte[] raw = pack.write(msg);
		out.writeBytes(raw);
	}

}
