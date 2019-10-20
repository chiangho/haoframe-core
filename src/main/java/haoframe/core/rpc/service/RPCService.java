package haoframe.core.rpc.service;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.dubbo.common.utils.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * rpc 服务器，基于netty构建
 * @author chianghao
 *
 */
public class RPCService {

	
	public static final RPCService INSTANCE = new RPCService();
	
	private final RPCConfig config;
	
	Logger log  = LoggerFactory.getLogger(this.getClass());
	
	//应用和channel hashCode 的映射关系
	public static final ConcurrentHashMap<String, ConcurrentHashSet<Channel>> appChannelMap = new ConcurrentHashMap<String, ConcurrentHashSet<Channel>>();
	
	
	public static Channel getChannel(String appName) {
		if(appChannelMap.containsKey(appName)) {
			ConcurrentHashSet<Channel> ss = appChannelMap.get(appName);
			int index=new Random().nextInt(ss.size());
			Channel[] carray = ss.toArray(new Channel[ss.size()]);
			return carray[index];
		}
		return null;
	}
	
	
	
	
	public static String getAppXinTiaoKey(String appName,int channelHashCode) {
		return appName+"_"+channelHashCode;
	}
	
	
	public static boolean isExistChannel(String appName,Channel c) {
		ConcurrentHashSet<Channel> cc = appChannelMap.get(appName);
		for(Channel t:cc) {
			if(t.hashCode()==c.hashCode()) {
				return true;
			}
		}
		return false;
	}
	
	
	private RPCService() {
		config = new RPCConfig();
	}
	
	
	
	/**
	 * 启动服务器
	 */
	public void run(int port) {
		this.config.setPort(port);
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap  b  = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.childHandler(new ChildChannelHandler());
			ServiceHandlerTask.start();
			
			TimerTask timerTask = new CleanAppChannelMapTask();
	        Timer timer = new Timer(true);
	        timer.scheduleAtFixedRate(timerTask, 0, 10*1000);

			ChannelFuture f = b.bind(this.config.getPort()).sync();
			f.channel().closeFuture().sync();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	

	
	class  CleanAppChannelMapTask  extends TimerTask {

		@Override
		public void run() {
			for(Map.Entry<String, ConcurrentHashSet<Channel>> a :appChannelMap.entrySet()) {
				ConcurrentHashSet<Channel> cl = a.getValue();
				Iterator<Channel> it= cl.iterator();
				while(it.hasNext()) {
					Channel c = it.next();
					if(!c.isOpen()) {
						log.info("channel is not open , reomve from appChannelMap . the key is  {}",a.getKey());
						it.remove();
						continue;
					}
				}
			};
		}
	}


	public static void main(String[] args) {
		RPCService.INSTANCE.run(9004);
	}

}
