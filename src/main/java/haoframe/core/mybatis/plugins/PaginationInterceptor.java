package haoframe.core.mybatis.plugins;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class PaginationInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = realTarget(invocation.getTarget());
		MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);  
		MappedStatement mappedStatement=(MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
		
		if (SqlCommandType.SELECT != mappedStatement.getSqlCommandType()
	            || StatementType.CALLABLE == mappedStatement.getStatementType()) {
	            return invocation.proceed();
	    } 
		
		BoundSql boundSql = statementHandler.getBoundSql();
		//条件1  id 是queryPageList  有参数 pageSize(Integer) pageIndex（Integer） sqlWrapper（SqlWrapper）
		//条件2  
		System.out.println("========================"+boundSql.getParameterObject().getClass().getName());
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		 if (target instanceof StatementHandler) {
	            return Plugin.wrap(target, this);
	        }
	        return target;
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}

	public <T> T realTarget(Object target) {
		if (Proxy.isProxyClass(target.getClass())) {
			MetaObject metaObject = SystemMetaObject.forObject(target);
			return realTarget(metaObject.getValue("h.target"));
		}
		return (T) target;
	}

}
