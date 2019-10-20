package haoframe.core.model;

import java.io.Serializable;
import java.util.List;

/**
 * 从互联网爬取的书本信息
 * @author Administrator
 *
 */
public class BookWormInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String name;//揭秘Java虚拟机 [专著] : JVM设计原理与实现 ",
	private String englishName;
	private String author;
	private String translators;
	private String isbn;
	private List<String> tags;//:["JAVA语言 "," 程序设计"],
	private String chinaBookType;//":"TP312JA"
	private String publisher;// 电子工业出版社",
	private String publishingTime;//":" 2017",
	private String publishingAddress;
	private String content;
	
	
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTranslators() {
		return translators;
	}
	public void setTranslators(String translators) {
		this.translators = translators;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getChinaBookType() {
		return chinaBookType;
	}
	public void setChinaBookType(String chinaBookType) {
		this.chinaBookType = chinaBookType;
	}
	public String getPublishingTime() {
		return publishingTime;
	}
	public void setPublishingTime(String publishingTime) {
		this.publishingTime = publishingTime;
	}
	public String getPublishingAddress() {
		return publishingAddress;
	}
	public void setPublishingAddress(String publishingAddress) {
		this.publishingAddress = publishingAddress;
	}
	
}
