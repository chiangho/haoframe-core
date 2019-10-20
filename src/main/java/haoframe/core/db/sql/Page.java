package haoframe.core.db.sql;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 分页信息
 * @author chianghao
 *
 */
public class Page implements Serializable{

	private static final long serialVersionUID = -2513132642456114868L;
	
	/** 从第几条记录开始 **/
	protected int offset;

	/** 总记录数 **/
	protected long totalRows;

	/** 每页大小 **/
	protected int pageSize = 10;
	
	/**
	 * 如果页码页码设置为小于1，那么用户需要自己计算出start的位置
	 */
	protected int pageIndex = 1;
	
	/** 总页数 **/
	protected long pageCount;
	
	/** 排序的字段 **/
	private Map<String,String> order = new HashMap<String, String>();
	
	
	public Map<String, String> getOrder() {
		return order;
	}

	public void setOrder(Map<String, String> order) {
		this.order = order;
	}

	public void setPageCount(long pageCount) {
		this.pageCount = pageCount;
	}

	public long getTotalRows() {
		return totalRows;
	}

	public int getPageSize() {
		return pageSize;
	}

	public long getPageCount() {
		return totalRows % pageSize == 0 ? totalRows / pageSize : totalRows / pageSize + 1;
	}


	public void setTotalRows(long totalRows) {
		this.totalRows = totalRows;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getOffset() {
		if(pageIndex>=1){
			offset = (pageIndex - 1) * pageSize;
		}
		return offset;
	}
	
	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
}
