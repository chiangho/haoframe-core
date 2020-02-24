package haoframe.core.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import haoframe.core.mybatis.plugins.Page;
import haoframe.core.mybatis.sql.SqlWrapper;

public interface BaseMapper<T>  {

	//register start 基本方法
	public void insert(T bean);
	
	public void update(@Param("bean")T bean,@Param("sqlWrapper") SqlWrapper sqlWrapper);
	
	public void delete(SqlWrapper conditions);
	
	public T queryBean(SqlWrapper conditions);
	
	public List<T> queryList(SqlWrapper conditions);
	
	public List<T> queryPageList(@Param("page")Page<T> page,@Param("sqlWrapper") SqlWrapper sqlWrapper);
	
	public Object queryObject(SqlWrapper conditions);
	
	//register end 基本方法
	/*
	 * public void insertBatch(List<T> list);
	 * 
	 * public void updateByCode(T bean,Object code);
	 * 
	 * public void delete(T bean);
	 * 
	 * public void delByCode(Object code);
	 * 
	 * public void executeSql(String sql,List<Object> args);
	 * 
	 * public T queryBean(T bean);
	 * 
	 * public T queryBeanByCode(Object code);
	 * 
	 * public int queryCount(T bean);
	 * 
	 * public int queryCountByConditions(SqlConditions conditions);
	 * 
	 * public List<T> queryList(T bean);
	 * 
	 * public List<T> queryAll();
	 * 
	 * public List<Object[]> queryListItemToArray(SqlConditions conditions);
	 * 
	 * public void queryPageList(Page<T> page);
	 * 
	 * public void queryPageListByBean(Page<T> page,T bean);
	 */
	
}
