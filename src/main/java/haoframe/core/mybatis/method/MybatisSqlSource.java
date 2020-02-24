package haoframe.core.mybatis.method;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import haoframe.core.db.model.Column;
import haoframe.core.db.model.Table;
import haoframe.core.mybatis.sql.SqlWhere;
import haoframe.core.mybatis.sql.SqlWrapper;
import haoframe.core.mybatis.sql.db_enum.SqlConnector;
import haoframe.core.utils.ClassUtils;

public class MybatisSqlSource implements SqlSource{

	Logger logger = LoggerFactory.getLogger(MybatisSqlSource.class);
	
	private Table table;
	private Class<?> mapperClass;
	private BaseMapperMethodDefinition method;
	private final Configuration configuration;
	
	public MybatisSqlSource(Table table,Class<?> mapperClass,BaseMapperMethodDefinition method,Configuration configuration) {
		this.table = table;
		this.mapperClass = mapperClass;
		this.method = method;
		this.configuration = configuration;
	}
	
	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}
	
	public Class<?> getMapperClass() {
		return mapperClass;
	}
	
	public void setMapperClass(Class<?> mapperClass) {
		this.mapperClass = mapperClass;
	}

	public BaseMapperMethodDefinition getMethod() {
		return method;
	}

	public void setMethod(BaseMapperMethodDefinition method) {
		this.method = method;
	}

	@Override
	public BoundSql getBoundSql(Object parameterObject) {
		String sql="";
		switch(method) {
		case insert:
			sql = getInsert(parameterObject);
			break;
		case delete:
			sql = getDelete((SqlWrapper) parameterObject);
			break;
		case update:
			sql = getUpdate(parameterObject);
			break;
		case queryBean:
			sql = queryBean((SqlWrapper) parameterObject);
			break;
		case queryPageList:
			sql = queryPageList(parameterObject);
			break;
		case queryList:
			sql = queryList((SqlWrapper) parameterObject);
			break;
		case queryObject:
			sql = queryObject((SqlWrapper) parameterObject);
			break;
		default:
			break;
		}
		logger.info("动态生成的原MyBatis Sql  ===>{}",sql);
	    DynamicContext context = new DynamicContext(configuration, parameterObject);
	    SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
	    Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
	    
	    SqlSource sqlSource = sqlSourceParser.parse(sql, parameterType, context.getBindings());
	    BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
	    // 对BoundSql对象保存额外的bind节点类型
	    for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
	      boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
	    }
	    return boundSql;
	}
	
	private String queryObject(SqlWrapper conditions) {
		String[] fieldNames = conditions.getFieldNames();
		if(fieldNames!=null&&fieldNames.length==1) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		String fieldName = fieldNames[0];
		sb.append("select `"+table.getColumnName(fieldName)+"` from `"+this.table.getTableName()+"` ");
		String whereSql = sqlWrapperToSqlAsMysql("",conditions);
		if(StringUtils.isNotEmpty(whereSql)) {
			sb.append(" where "+whereSql);
		}
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private String queryPageList(Object parameterObject) {
		Map<String,Object> params = (Map<String, Object>) parameterObject;
		SqlWrapper sqlWrapper = (SqlWrapper) params.get("param2");
		StringBuffer sb = new StringBuffer();
		sb.append("select "+getSelectFiled()+" from `"+this.table.getTableName()+"` ");
		String sql = sqlWrapperToSqlAsMysql("sqlWrapper",sqlWrapper);
		if(StringUtils.isNotEmpty(sql)) {
			sb.append(" where "+sql);
		}
		return sb.toString();
	}
	
	private String queryBean(SqlWrapper conditions) {
		StringBuffer sb = new StringBuffer();
		sb.append("select "+getSelectFiled()+" from `"+this.table.getTableName()+"` ");
		String whereSql = this.sqlWrapperToSqlAsMysql("",conditions);
		if(StringUtils.isNotEmpty(whereSql)) {
			sb.append(" where "+whereSql);
		}
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private String getUpdate(Object parameterObject) {
		Map<String,Object> params = (Map<String, Object>) parameterObject;
		Object bean = params.get("param1");
		SqlWrapper sqlWrapper = (SqlWrapper) params.get("param2");
		if(bean==null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("update `"+table.getTableName()+"` ");
		int fieldSize = 0;
		if(bean!=null) {
			StringBuffer fieldString = new StringBuffer();
			int index= 0;
			for(String fieldName:table.getColumnMap().keySet()) {
				Object value = ClassUtils.getFieldValue(bean, fieldName); 
				if(value!=null) {
					Column c = table.getColumnInfo(fieldName);
					if(index==0) {
						fieldString.append("set `"+c.getName()+"` = #{bean."+fieldName+"}");
					}else {
						fieldString.append(",`"+c.getName()+"`=#{bean."+fieldName+"}");
					}
					index++;
					fieldSize++;
				}
			}
			if(fieldSize==0) {
				return null;
			}
			sb.append(fieldString);
		}
		String whereSql = this.sqlWrapperToSqlAsMysql("sqlWrapper", sqlWrapper);
		if(StringUtils.isNotBlank(whereSql)) {
			sb.append(" where "+whereSql);
		}
		return sb.toString();
	} 
	
	private String getSelectFiled() {
		StringBuffer sb = new StringBuffer();
		int i=0;
		for(Map.Entry<String, Column> entiry:table.getColumnMap().entrySet()) {
			if(i==0) {
				sb.append("`"+entiry.getValue().getName() +"` as `"+entiry.getKey()+"`" );
			}else {
				sb.append(",`"+entiry.getValue().getName() +"` as `"+entiry.getKey()+"`" );
			}
			i++;
		}
		return sb.toString();
	}
	
	private String queryList(SqlWrapper condition) {
		StringBuffer sb = new StringBuffer();
		sb.append("select "+getSelectFiled()+" from `"+this.table.getTableName()+"` ");
		String sql = sqlWrapperToSqlAsMysql("",condition);
		if(StringUtils.isNotEmpty(sql)) {
			sb.append(" where "+sql);
		}
		return sb.toString();
	}

	private String getDelete(SqlWrapper condition) {
		StringBuffer sb = new StringBuffer();
		sb.append("delete from `"+table.getTableName()+"` ");
		String whereSql = sqlWrapperToSqlAsMysql("", condition);
		if(StringUtils.isNotEmpty(whereSql)) {
			sb.append(" where "+whereSql);
		}
		return sb.toString();
	}
	
	private String getInsert(Object bean) {
		if(bean==null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		StringBuffer fields = new StringBuffer();
		StringBuffer values = new StringBuffer();
		int index=0;
		for(String fieldName:table.getColumnMap().keySet()) {
			Object value = ClassUtils.getFieldValue(bean, fieldName); 
			if(value!=null) {
				Column c = table.getColumnInfo(fieldName);
				if(index==0) {
					fields.append("`"+c.getName()+"`");
					values.append("#{"+fieldName+"}");
				}else {
					fields.append(",`"+c.getName()+"`");
					values.append(",#{"+fieldName+"}");
				}
				index++;
			}
		}
		if(index>0) {
			sb.append("insert into `"+table.getTableName()+"`("+fields+")values("+values+")");
		}
		return sb.toString();
	}
	
	
	public String sqlWrapperToSqlAsMysql(String prefix,SqlWrapper sqlWrapper) {
		List<SqlWhere> sqlWhereList = sqlWrapper.getSqlWhereList();
		if(sqlWhereList==null||sqlWhereList.isEmpty()) {
			return "";
		}
		if(StringUtils.isNotEmpty(prefix)) {
			prefix=prefix+".params";
		}else {
			prefix="params";
		}
		StringBuffer sb = new StringBuffer();
		int count=0;
		for(int i=0;i<sqlWhereList.size();i++) {
			SqlWhere sqlWhere = sqlWhereList.get(i);
			if(count==0&&!sqlWhere.isCondition()) {
				continue;
			}
			String sql = sqlWhereToSqlAsMysql(prefix,sqlWhere);
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
	
	
	public String sqlWhereToSqlAsMysql(String prefix,SqlWhere sqlWhere) {
		StringBuffer sb = new StringBuffer();
		String fieldName = sqlWhere.getFieldName();
		sb.append(" `"+this.table.getColumnName(fieldName)+"`");
		if(StringUtils.isNotBlank(prefix)) {
			prefix =prefix+".";
		}
		switch (sqlWhere.getOperators()) {
		case in:
			sb.append(" in( ");
			Object[] values1=(Object[]) sqlWhere.getValue();
			for (int j = 0; j < values1.length; j++) {
				if (j == 0) {
					sb.append("#{"+prefix+fieldName+"[0]}");
				} else {
					sb.append(",#{"+prefix+fieldName+"[1]}");
				}
			}
			sb.append(") ");
			break;
		case not_in:
			sb.append(" not in( ");
			Object[] values2=(Object[]) sqlWhere.getValue();
			for (int j = 0; j < values2.length; j++) {
				if (j == 0) {
					sb.append("#{"+prefix+fieldName+"[0]}");
				} else {
					sb.append(",#{"+prefix+fieldName+"[1]}");
				}
			}
			sb.append(") ");
			break;
		case between:
			sb.append(" between #{"+prefix+fieldName+"[0]} and #{params."+fieldName+"[1]} ");
			break;
		case not_between:
			sb.append(" not between #{"+prefix+fieldName+"[0]} and #{params."+fieldName+"[1]} ");
			break;
		case like:
			sb.append(" like concat('%',#{"+prefix+fieldName+"},'%') ");
			break;
		case front_like:
			sb.append(" like concat('%',#{"+prefix+fieldName+"}) ");
			break;
		case behind_like:
			sb.append(" like concat(#{"+prefix+fieldName+"},'%') ");
			break;
		case equals:
			sb.append(" = #{"+prefix+fieldName+"} ");
			break;
		case not_equals:
			sb.append(" != #{"+prefix+fieldName+"} ");
			break;
		case greater:
			sb.append(" > #{"+prefix+fieldName+"} ");
			break;
		case greater_equals:
			sb.append(" >= #{"+prefix+fieldName+"} ");
			break;
		case less:
			sb.append(" < #{"+prefix+fieldName+"} ");
			break;
		case less_equals:
			sb.append(" <= #{"+prefix+fieldName+"} ");
			break;
		default:
			logger.error("未找到对应的逻辑操作条件,你设置的sql信息如下{}", this);
			break;
		}
		return sb.toString();
	}
	
}
