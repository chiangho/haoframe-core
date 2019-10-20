package haoframe.core.rpc.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import haoframe.core.rpc.ActionEnum;
import haoframe.core.rpc.codec.MsgpackEncoder;
import haoframe.core.rpc.codec.MsgpackRequestDecoder;
import haoframe.core.rpc.model.RPCRequest;
import haoframe.core.rpc.model.ServiceInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;


/**
 * 类说明：客户端
 */
public class RPCClient {

	static Logger log = LoggerFactory.getLogger(RPCClient.class);
	
	public static AtomicReference<Channel> channel = new AtomicReference<Channel>();

//	private NioEventLoopGroup workGroup;

	private String host;

	private int port;

	private Bootstrap bootstrap;
	
	
	private String appName;
	
	Map<String,ServiceInfo> regisertServiceInfo = new HashMap<String,ServiceInfo>();
	
	
//    protected final HashedWheelTimer timer = new HashedWheelTimer();

	public static final RPCClient client = new RPCClient();


	
	public RPCClient setHost(String host) {
		this.host = host;
		return this;
	}
	
	public RPCClient setPort(int port) {
		this.port = port;
		return this;
	} 

	
	private RPCClient() {
		this.host = "127.0.0.1";
		this.port = 7001;
	}

	public RPCClient setAppName(String appName) {
		this.appName = appName;
		return this;
	}
	
	public String getAppName() {
		return this.appName;
	}
	
	public void start() {
		new Thread(new CheckChanel()).start();
		HandlerTask.start();
	}

	
	class  CheckChanel  implements Runnable {
		@SuppressWarnings("static-access")
		@Override
		public void run() {
			while(true) {
				if (channel.get() == null || !channel.get().isActive()) {
					connect();
				}
				try {
					Thread.currentThread().sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void connect() {
			ChannelFuture  future = null;
			NioEventLoopGroup workGroup =  new NioEventLoopGroup(4);
			try {
				if (channel.get() != null && channel.get().isActive()) {
					return;
				}
				bootstrap = new Bootstrap();
				bootstrap.group(workGroup)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializerImp());
				bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
				bootstrap.remoteAddress(host, port);
				synchronized(channel) {
					future =bootstrap.connect();//.addListener(new ConnectionListener());
				}
				future.sync();
				System.out.println("连接成功");
				setRegisterServiceInfo(getServiceInfoList());
				future.channel().closeFuture().sync();
			} catch (Throwable e) {
				System.out.println("====================未连接到服务异常=====================");
			}finally {
				if(workGroup != null) {
					try {
						workGroup.shutdownGracefully().sync();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
//		}
	}

	class ConnectionListener implements ChannelFutureListener {

		@Override
		public void operationComplete(ChannelFuture channelFuture) throws Exception {
			if (!channelFuture.isSuccess()) {
				
			}else {
				channel.getAndSet(channelFuture.channel());
			}
		}
	}

	private static class ChannelInitializerImp extends ChannelInitializer<Channel> {

		@Override
		protected void initChannel(Channel ch) throws Exception {
			ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));    
			ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4)); 
			ch.pipeline().addLast(new IdleStateHandler(20, 10, 0));
//			ch.pipeline().addLast(new OutBoundHandler());
			ch.pipeline().addLast(new MsgpackEncoder());
			ch.pipeline().addLast(new MsgpackRequestDecoder());
			ch.pipeline().addLast(new ClientHandler());
		}
	}

	public static List<ServiceInfo> getServiceInfoList(){
		List<ServiceInfo> li = new ArrayList<ServiceInfo>();
		for(Map.Entry<String, ServiceInfo> entry : RPCClient.client.regisertServiceInfo.entrySet()) {
			li.add(entry.getValue());
		}
		return li;
	}
	
	
	
	public static void setRegisterServiceInfo(List<ServiceInfo> list) {
		RPCRequest q = new RPCRequest();
		q.setAction(ActionEnum.serviceRegister.getCode());
		q.setId(UUID.randomUUID().toString());
		q.setType(0);
		q.setContnent(JSON.toJSONString(list));
		log.info("登记服务内容为{}",JSON.toJSONString(q));
		channel.get().writeAndFlush(q);
	}
	
	public static void setRegisterServiceInfo(String interfaceName,String implementsName) {
		ServiceInfo s = new ServiceInfo();
		s.setApp(client.appName);
		s.setImplementationClassName(implementsName);
		s.setInterfaceClassName(interfaceName);
		RPCClient.client.regisertServiceInfo.put(interfaceName, s);
		setRegisterServiceInfo(Arrays.asList(s));
	}
	
	public static void main(String[] args) throws InterruptedException {
		new RPCClient().setHost("127.0.0.1").setPort(7001).start();
	}

}
