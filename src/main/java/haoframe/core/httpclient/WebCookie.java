package haoframe.core.httpclient;

import java.util.Date;

import org.apache.http.cookie.Cookie;

public class WebCookie implements Cookie{

	private String name;

	
	private String value;

	private String comment;

	private String commentURL;

	private Date expiryDate;

	private boolean persistent;

	private String domain;

	private String path;

	private int[] ports;

	private boolean secure;

	private int version;

	private boolean expired;
	
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getCommentURL() {
		return commentURL;
	}


	public void setCommentURL(String commentURL) {
		this.commentURL = commentURL;
	}


	public Date getExpiryDate() {
		return expiryDate;
	}


	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}


	public boolean isPersistent() {
		return persistent;
	}


	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}


	public String getDomain() {
		return domain;
	}


	public void setDomain(String domain) {
		this.domain = domain;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public int[] getPorts() {
		return ports;
	}


	public void setPorts(int[] ports) {
		this.ports = ports;
	}


	public boolean isSecure() {
		return secure;
	}


	public void setSecure(boolean secure) {
		this.secure = secure;
	}


	public int getVersion() {
		return version;
	}


	public void setVersion(int version) {
		this.version = version;
	}


	public boolean isExpired() {
		return expired;
	}


	public void setExpired(boolean expired) {
		this.expired = expired;
	}


	@Override
	public boolean isExpired(Date date) {
		// TODO Auto-generated method stub
		return false;
	}

}
