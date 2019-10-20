package haoframe.core.model;



/**
 * jwt存储内容
 * @author chianghao
 *
 */
public class JwtStorageContent {

	
	
	//token
	private String token;
	
	//存储内容
	private Object content;
	
	//过期时间
	private long   expirationTime;

	public JwtStorageContent(String token,Object content,long expirationTime) {
		this.token = token;
		this.content = content;
		this.expirationTime = expirationTime;
	}
	
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}
	
	
	
}
