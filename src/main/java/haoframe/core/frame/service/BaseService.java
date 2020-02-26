package haoframe.core.frame.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import haoframe.core.exception.HaoException;
import haoframe.core.mybatis.mapper.BaseMapper;
import haoframe.core.mybatis.plugins.Paging;
import haoframe.core.mybatis.sql.SqlWrapper;
import haoframe.core.mybatis.sql.db_enum.SqlOperators;
import haoframe.core.utils.ClassUtils;

public abstract class BaseService<M extends BaseMapper<T>,T> {

	@Autowired
	public M mapper;
	//	///////////////////////////////////////////////写方法////////////////////////////////////////////////////////////////////////
	
	/**
	 * 根据主键code在数据库中是否存在
	 * if 主键 code 不存在
	 * 		insert 
	 * else
	 *      upate
	 * 
	 * @param bean
	 * @return bean主键
	 */
	public boolean save(T bean) {
		Object code = ClassUtils.getFieldValue(bean, "code");
		if(code==null) {
			throw new HaoException("the code can not null");
		}
		if(codeIsExist(code)) {
			//update
			this.mapper.updateByCode(bean, code);
		}else {
			//insert
			this.mapper.insert(bean);
		}
		return true;
	}
	
	/**
	 * 删除
	 * @param code
	 */
	public void delete(Object code) {
		mapper.deleteByCode(code);
	}
	
	public void insert(T bean) {
		Object code = ClassUtils.getFieldValue(bean, "code");
		if(code==null) {
			throw new HaoException("the code can not null");
		}
		mapper.insert(bean);
	}
	
	public void insertBatch(List<T> beans) {
		mapper.batchInsert(beans);
	}
	
	public void update(T bean) {
		Object code = ClassUtils.getFieldValue(bean, "code");
		if(code==null) {
			throw new HaoException("the code can not null");
		}
		mapper.updateByCode(bean, code);
	}
	
	
	///////////////////////////////////////////////////读方法////////////////////////////////////////////////////////////////////////
	
	/**
	 * 查询字段字段的值是否已经存在
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public int queryCount(String fieldName,Object value) {
		Integer count = (Integer) this.mapper.queryObject(new SqlWrapper().addFields("count(1)").addCondition(fieldName,value));
		return count;
	}
	
	/**
	 * 查询字段字段的值是否已经存在，排序自己
	 * @param fieldName
	 * @param value
	 * @param code
	 * @return
	 */
	public int queryCount(String fieldName,Object value,Object code) {
		Integer count = (Integer) this.mapper.queryObject(new SqlWrapper().addFields("count(1)").addCondition(fieldName,value).addCondition("code",SqlOperators.not_equals,code));
		return count;
	}
	
	public boolean codeIsExist(Object code) {
		Integer count = (Integer) this.mapper.queryObject(new SqlWrapper().addFields("count(1)").addCondition("code",code));
		return count>0?true:false;
	}
	
	public List<T> query(T where) {
		return mapper.queryListByEntity(where, null);
	}
	
	public List<T> queryPage(T where,Paging paging){
		return mapper.queryPageListByEntity(paging, where, null);
	}
	
	
}
