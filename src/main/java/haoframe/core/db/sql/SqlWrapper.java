package haoframe.core.db.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import haoframe.core.db.model.Table;
import haoframe.core.db.sql.dbenum.SqlConnector;
import haoframe.core.db.sql.dbenum.SqlOperators;

public class SqlWrapper {

	List<SqlWhere> sqlWhereList;
	
	Set<String> fieldNames;
	
	Map<String,Object> params;
	
	
	public SqlWrapper() {
		sqlWhereList = new ArrayList<SqlWhere>();
		params = new HashMap<String,Object>();
		fieldNames = new HashSet<>();
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

	public SqlWrapper addCondition(String column, Object value) {
		sqlWhereList.add(new SqlWhere(column,value));
		return this;
	}
	
	public SqlWrapper addCondition(String column, SqlOperators operators,Object value) {
		sqlWhereList.add(new SqlWhere(column,operators,value));
		return this;
	}

	public SqlWrapper addCondition(String column, SqlOperators operators,Object[] value) {
		sqlWhereList.add(new SqlWhere(column,value));
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
	
	public String getMybatisSql(String prefix,Table table) {
		if(sqlWhereList==null||sqlWhereList.isEmpty()) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		int count=0;
		for(int i=0;i<sqlWhereList.size();i++) {
			SqlWhere sqlWhere = sqlWhereList.get(i);
			if(count==0&&!sqlWhere.isCondition()) {
				continue;
			}
			String sql = sqlWhere.getMybatisSql(prefix,table);
			sb.append(sql);
			SqlWhere nextSqlWhere =null;
			if((i+1)==(sqlWhereList.size()-1)) {
				nextSqlWhere = sqlWhereList.get((i+1));
			}
			
			//当前和下一个都是where条件的添加默认的and
			if(sqlWhere.isCondition()&&nextSqlWhere!=null&&nextSqlWhere.isCondition()) {
				sb.append(" "+SqlConnector.and.getConnector()+" ");
			}
			count++;
		}
		
		
		//将无效的后缀截取掉
		String sql = sb.toString().trim();
		if(sql.toUpperCase().endsWith(SqlConnector.and.getConnector())) {
			sql  = sql.substring(0,sql.length()-SqlConnector.and.getConnector().length());
		}
		if(sql.toUpperCase().endsWith(SqlConnector.or.getConnector())) {
			sql  = sql.substring(0,sql.length()-SqlConnector.or.getConnector().length());
		}
		if(sql.toUpperCase().endsWith(SqlConnector.orStart.getConnector())) {
			sql  = sql.substring(0,sql.length()-SqlConnector.orStart.getConnector().length());
		}
		if(sql.toUpperCase().endsWith(SqlConnector.andStart.getConnector())) {
			sql  = sql.substring(0,sql.length()-SqlConnector.andStart.getConnector().length());
		}
		
		return sql;
	}
	
	
	public Sql getJdbcSql() {
		if(sqlWhereList==null||sqlWhereList.isEmpty()) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		List<Object> args = new ArrayList<Object>();
		
		
		int count=0;
		for(int i=0;i<sqlWhereList.size();i++) {
			SqlWhere sqlWhere = sqlWhereList.get(i);
			if(count==0&&!sqlWhere.isCondition()) {
				continue;
			}
			Sql sql = sqlWhere.getJdbcSql();
			sb.append(sql.getSql());
			if(sql.getArgs()!=null&&!sql.getArgs().isEmpty()) {
				args.addAll(sql.getArgs());
			}
			SqlWhere nextSqlWhere =null;
			if((i+1)==(sqlWhereList.size()-1)) {
				nextSqlWhere = sqlWhereList.get((i+1));
			}
			
			//当前和下一个都是where条件的添加默认的and
			if(sqlWhere.isCondition()&&nextSqlWhere!=null&&nextSqlWhere.isCondition()) {
				sb.append(" "+SqlConnector.and.getConnector()+" ");
			}
			count++;
		}
		
		
		//将无效的后缀截取掉
		String sql = sb.toString().trim();
		if(sql.toUpperCase().endsWith(SqlConnector.and.getConnector())) {
			sql  = sql.substring(0,sql.length()-SqlConnector.and.getConnector().length());
		}
		if(sql.toUpperCase().endsWith(SqlConnector.or.getConnector())) {
			sql  = sql.substring(0,sql.length()-SqlConnector.or.getConnector().length());
		}
		if(sql.toUpperCase().endsWith(SqlConnector.orStart.getConnector())) {
			sql  = sql.substring(0,sql.length()-SqlConnector.orStart.getConnector().length());
		}
		if(sql.toUpperCase().endsWith(SqlConnector.andStart.getConnector())) {
			sql  = sql.substring(0,sql.length()-SqlConnector.andStart.getConnector().length());
		}
		if(sql.toUpperCase().endsWith(SqlConnector.end.getConnector())) {
			sql  = sql.substring(0,sql.length()-SqlConnector.end.getConnector().length());
		}
		return new Sql(sql,args);
	}

	
	
	class SqlWhere {

		private SqlOperators operators;
		private Object value;
		private String column;
		
		private boolean isCondition=true;
		private SqlConnector connector;
		
		
		public SqlWhere(String column, Object value) {
			this(column, SqlOperators.equals, value);
		}
		
		public SqlWhere(String column, SqlOperators operators, Object value) {
			this.column = column;
			this.operators = operators;
			this.value = value;
			params.put(column, value);
		}

		public SqlWhere(String column, SqlOperators operators, Object[] values) {
			this.column = column;
			this.operators = operators;
			this.value = values;
			params.put(column, values);
		}

		public SqlWhere(SqlConnector connector) {
			this.connector = connector;
			this.isCondition = false;
		}
		

		public boolean isCondition() {
			return isCondition;
		}

		public void setCondition(boolean isCondition) {
			this.isCondition = isCondition;
		}

		public SqlConnector getConnector() {
			return connector;
		}

		public void setConnector(SqlConnector connector) {
			this.connector = connector;
		}

		public SqlOperators getOperators() {
			return operators;
		}

		public void setOperators(SqlOperators operators) {
			this.operators = operators;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public String getColumn() {
			return column;
		}

		public void setColumn(String column) {
			this.column = column;
		}

		Logger log = LoggerFactory.getLogger(this.getClass());

		public Sql getJdbcSql() {
			StringBuffer sb = new StringBuffer();
			if (StringUtils.isNotEmpty(this.column)) {
				sb.append(" " + this.column);
			}
			List<Object> args = new ArrayList<Object>();
			switch (this.operators) {
			case in:
				sb.append(" in( ");
				Object[] values1=(Object[]) this.value;
				for (int j = 0; j < values1.length; j++) {
					if (j == 0) {
						sb.append("?");
					} else {
						sb.append(",?");
					}
					args.add(values1[j]);
				}
				sb.append(") ");
				break;
			case not_in:
				sb.append(" not in( ");
				Object[] values2=(Object[]) this.value;
				for (int j = 0; j < values2.length; j++) {
					if (j == 0) {
						sb.append("?");
					} else {
						sb.append(",?");
					}
					args.add(values2[j]);
				}
				sb.append(") ");
				break;
			case between:
				sb.append(" between ? and ? ");
				Object[] values3=(Object[]) this.value;
				args.add(values3[0]);
				args.add(values3[1]);
				break;
			case not_between:
				sb.append(" not between ? and ? ");
				Object[] values4=(Object[]) this.value;
				args.add(values4[0]);
				args.add(values4[1]);
				break;
			case like:
				sb.append(" like concat('%',?,'%') ");
				args.add(this.value);
				break;
			case front_like:
				sb.append(" like concat('%',?) ");
				args.add(this.value);
				break;
			case behind_like:
				sb.append(" like concat(?,'%') ");
				args.add(this.value);
				break;
			case equals:
				sb.append(" = ? ");
				args.add(this.value);
				break;
			case not_equals:
				sb.append(" != ? ");
				args.add(this.value);
				break;
			case greater:
				sb.append(" > ? ");
				args.add(this.value);
				break;
			case greater_equals:
				sb.append(" >= ? ");
				args.add(this.value);
				break;
			case less:
				sb.append(" < ? ");
				args.add(this.value);
				break;
			case less_equals:
				sb.append(" <= ? ");
				args.add(this.value);
				break;
			default:
				log.error("未找到对应的逻辑操作条件,你设置的sql信息如下{}", this);
				break;
			}
			return new Sql(sb.toString(), args);
		}

		public String getMybatisSql(String prefix,Table table) {
			StringBuffer sb = new StringBuffer();
			if (StringUtils.isNotEmpty(this.column)) {
				sb.append(" `"+table.getColumnMap().get(this.column).getName()+"`");
			}
			if(StringUtils.isNotBlank(prefix)) {
				prefix =prefix+".";
			}
			
			switch (this.operators) {
			case in:
				sb.append(" in( ");
				Object[] values1=(Object[]) this.value;
				for (int j = 0; j < values1.length; j++) {
					if (j == 0) {
						sb.append("#{"+prefix+"params."+this.column+"[0]}");
					} else {
						sb.append(",#{"+prefix+"params."+this.column+"[1]}");
					}
				}
				sb.append(") ");
				break;
			case not_in:
				sb.append(" not in( ");
				Object[] values2=(Object[]) this.value;
				for (int j = 0; j < values2.length; j++) {
					if (j == 0) {
						sb.append("#{"+prefix+"params."+this.column+"[0]}");
					} else {
						sb.append(",#{"+prefix+"params."+this.column+"[1]}");
					}
				}
				sb.append(") ");
				break;
			case between:
				sb.append(" between #{"+prefix+"params."+this.column+"[0]} and #{params."+this.column+"[1]} ");
				break;
			case not_between:
				sb.append(" not between #{"+prefix+"params."+this.column+"[0]} and #{params."+this.column+"[1]} ");
				break;
			case like:
				sb.append(" like concat('%',#{"+prefix+"params."+this.column+"},'%') ");
				break;
			case front_like:
				sb.append(" like concat('%',#{"+prefix+"params."+this.column+"}) ");
				break;
			case behind_like:
				sb.append(" like concat(#{"+prefix+"params."+this.column+"},'%') ");
				break;
			case equals:
				sb.append(" = #{"+prefix+"params."+this.column+"} ");
				break;
			case not_equals:
				sb.append(" != #{"+prefix+"params."+this.column+"} ");
				break;
			case greater:
				sb.append(" > #{"+prefix+"params."+this.column+"} ");
				break;
			case greater_equals:
				sb.append(" >= #{"+prefix+"params."+this.column+"} ");
				break;
			case less:
				sb.append(" < #{"+prefix+"params."+this.column+"} ");
				break;
			case less_equals:
				sb.append(" <= #{"+prefix+"params."+this.column+"} ");
				break;
			default:
				log.error("未找到对应的逻辑操作条件,你设置的sql信息如下{}", this);
				break;
			}
			return sb.toString();
		}

	}
	
}
