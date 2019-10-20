package haoframe.core.rpc.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 处理任务，当有队列信息时执行队列
 * @author Administrator
 *
 */
public class ServiceHandlerTask implements Runnable{

     
	public final static LinkedBlockingQueue<ServiceExecuteThread> queue = new LinkedBlockingQueue<ServiceExecuteThread>();// 无缓存
	
	
	private final static ExecutorService threadPool ;
	
	static {
		int maxSize = Runtime.getRuntime().availableProcessors()+1;
		final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();// 无缓存
		threadPool = new ThreadPoolExecutor(0,maxSize, 0L, TimeUnit.MILLISECONDS, queue, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "pool_exe_request" + r.hashCode());
			}
		});
	}
	
	public static ExecutorService getPool() {
		return threadPool;
	}

	@Override
	public void run() {
		while(true) {
			try {
				ServiceExecuteThread e = queue.take();
				threadPool.execute(e);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void start() {
		new Thread(new ServiceHandlerTask()).start();
	}
	
}