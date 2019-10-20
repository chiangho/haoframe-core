package haoframe.core;

/**
 * 系统常量定义
 * @author chianghao
 *
 */
public class Constant {

	
	/**
	 * 平台类型
	 * @author chianghao
	 *
	 */
	public enum AppInfo{
		
		readers("readers","读者平台"),
		tenant("tenant","服务点支撑平台"),
		admin("admin","总后台");
		
		private String name;
		private String title;
		
		public String toString() {
			return name;
		}
		
		private AppInfo(String name,String title) {
			this.name=name;
			this.title=title;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	
	
	/**
	 * 终端枚举类型
	 * @author chianghao
	 *
	 */
	public enum Terminal{
		PC_WEB,
		IOS,
		ANDROID,
		WAP,
		WECHAT;
		
		public static Terminal getByName(String name) {
			for(Terminal ty:Terminal.values()) {
				if(ty.toString().equals(name)) {
					return ty;
				}
			}
			return null;
		}
	}
	
	
}
