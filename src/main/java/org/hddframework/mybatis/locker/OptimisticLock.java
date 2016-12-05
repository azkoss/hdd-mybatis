package org.hddframework.mybatis.locker;

import java.sql.Connection;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.hddframework.mybatis.locker.refactor.SQLRefactor;
import org.hddframework.mybatis.locker.refactor.SQLRefactorFactory;

/**
 * Created by SongWei on 23/11/2016.
 */
@Intercepts({
		@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }), })
public class OptimisticLock implements Interceptor {

	public Object intercept(Invocation invocation) throws Throwable {
		Object target = invocation.getTarget();
		if (target instanceof StatementHandler) {
			StatementHandler statementHandler = (StatementHandler) target;
			BoundSql boundSql = statementHandler.getBoundSql();
			String originalSql = boundSql.getSql();
			SQLRefactor sqlRefactor = SQLRefactorFactory.buildNewSQLRefactor(this.column, this.property);
			String changedSql = sqlRefactor.refactorSQL(originalSql, statementHandler);
			if (!StringUtils.isBlank(changedSql)) {
				reBoundSql(boundSql, changedSql);
			}
		}

		return invocation.proceed();
	}

	private void reBoundSql(BoundSql boundSql, String sql) {
		MetaObject metaObject = SystemMetaObject.forObject(boundSql);
		metaObject.setValue("sql", sql);
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		this.property = "" + properties.get("property").toString();
		this.column = "" + properties.get("column").toString();
	}

	private String property;
	private String column;

}
