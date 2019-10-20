package haoframe.core.rpc.codec;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;

public class DecoderUtils {

	
	@SuppressWarnings("unchecked")
	public static  <T> T decoder(ByteBuf msg,Class<?> clazz) throws Exception {
		int index = msg.readerIndex();
		try {
			final byte[] array;
			final int length = msg.readableBytes();
			array = new byte[length];
			msg.getBytes(index, array,0,length);
			MessagePack pack = new MessagePack();
			Object object  = clazz.newInstance();
			object = pack.read(array, clazz);
			return (T) object;
		}finally {
			msg.readerIndex(index);
		}
		
	}
	
}
