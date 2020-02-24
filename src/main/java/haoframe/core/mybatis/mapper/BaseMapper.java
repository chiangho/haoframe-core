package haoframe.core.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import haoframe.core.mybatis.plugins.Page;
import haoframe.core.mybatis.sql.SqlOrder;
import haoframe.core.mybatis.sql.SqlWrapper;

public interface BaseMapper<T>  {

	//register start 基本方法
	public void insert(T bean);
	
	public void update(@Param("bean")T bean,@Param("sqlWrapper") SqlWrapper sqlWrapper);
	
	public void delete(SqlWrapper conditions);
	
	public T queryOne(SqlWrapper conditions);
	
	public List<T> queryList(SqlWrapper conditions);
	
	public List<T> queryPageList(@Param("page")Page page,@Param("sqlWrapper") SqlWrapper sqlWrapper);
	
	public Object queryObject(SqlWrapper conditions);
	
	//register end 基本方法
	
	//register start 基于实体操作
	public void updateByEntity(@Param("bean")T bean,@Param("where") T where);
	
	public void deleteByEntity(T where);
	
	public T queryOneByEntity(T where);
	
	public List<T> queryListByEntity(@Param("where") T where,@Param("order") SqlOrder order);
	
	public List<T> queryPageListByEntity(@Param("page")Page page,@Param("where") T where,@Param("order") SqlOrder order);
	
	public Object queryObjectByEntity(String fieldName,T bean);
	//register end 基于实体操作
	
	
	//register start 基于主键CODE操作
	public void deleteByCode(Object code);
	
	public T queryBeanByCode(Object code);
	
	public T queryOneByCode(Object code);
	//register end 基于主键CODE操作
}
