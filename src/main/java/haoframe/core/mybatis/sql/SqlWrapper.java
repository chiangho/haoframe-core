package haoframe.core.mybatis.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import haoframe.core.mybatis.sql.db_enum.SqlConnector;
import haoframe.core.mybatis.sql.db_enum.SqlOperators;

public class SqlWrapper {

	List<SqlWhere> sqlWhereList;
	
	Set<String> fieldNames;
	
	Map<String,Object> params;
	
	SqlOrder sqlOrder;
	
	public SqlWrapper() {
		sqlWhereList = new ArrayList<SqlWhere>();
		params = new HashMap<String,Object>();
		fieldNames = new HashSet<>();
		sqlOrder = new SqlOrder();
	}
	
	public SqlWrapper(String... fields) {
		this();
		for(String field:fields) {
			fieldNames.add(field);
		}
	}
	
	public String[] getFieldNames() {
		String[] array = fieldNames.toArray(new String[fieldNames.size()]);
		return array;
	}

	public void setFieldNames(Set<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public SqlWrapper addCondition(String fieldName, Object value) {
		sqlWhereList.add(new SqlWhere(fieldName,value));
		params.put(fieldName, value);
		return this;
	}
	
	public SqlWrapper addCondition(String fieldName, SqlOperators operators,Object value) {
		sqlWhereList.add(new SqlWhere(fieldName,operators,value));
		params.put(fieldName, value);
		return this;
	}

	public SqlWrapper addCondition(String fieldName, SqlOperators operators,Object[] value) {
		sqlWhereList.add(new SqlWhere(fieldName,value));
		params.put(fieldName, value);
		return this;
	}
	
	public SqlWrapper and() {
		sqlWhereList.add(new SqlWhere(SqlConnector.and));
		return this;
	}

	public SqlWrapper andStart() {
		sqlWhereList.add(new SqlWhere(SqlConnector.andStart));
		return this;
	}

	public SqlWrapper or() {
		sqlWhereList.add(new SqlWhere(SqlConnector.or));
		return this;
	}

	public SqlWrapper orStart() {
		sqlWhereList.add(new SqlWhere(SqlConnector.orStart));
		return this;
	}

	public SqlWrapper end() {
		sqlWhereList.add(new SqlWhere(SqlConnector.end));
		return this;
	}

	public List<SqlWhere> getSqlWhereList() {
		return sqlWhereList;
	}

	public void setSqlWhereList(List<SqlWhere> sqlWhereList) {
		this.sqlWhereList = sqlWhereList;
	}

	public SqlOrder getSqlOrder() {
		return sqlOrder;
	}

	public void setSqlOrder(SqlOrder sqlOrder) {
		this.sqlOrder = sqlOrder;
	}
	
	
}
