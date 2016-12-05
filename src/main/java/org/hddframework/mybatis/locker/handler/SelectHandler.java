package org.hddframework.mybatis.locker.handler;

import java.util.Objects;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * Created by SongWei on 30/11/2016.
 */
public class SelectHandler extends AbstractHandler implements SQLRefactorHandler {

	public SelectHandler(String column, String property, SQLRefactorHandler next) {
		super(column, property, next);
	}

	@Override
	public String refactorSQL(String originalSQL, StatementHandler statementHandler) {
		if (selectPattern.matcher(originalSQL).find()) {
			ParameterHandler parameterHandler = statementHandler.getParameterHandler();
			if (!Objects.isNull(parameterHandler)) {
				Object parameterObject = parameterHandler.getParameterObject();
				MetaObject metaObject = SystemMetaObject.forObject(parameterObject);
				boolean hasProperty = metaObject.hasGetter(this.property);
				if (hasProperty) {
					String changedSql = originalSQL.replaceAll(selectRegex,
							"$1 $2, " + this.column + " as " + this.property + " $3");
					return changedSql;
				} else {
					return originalSQL;
				}
			}
		}
		if (!Objects.isNull(next)) {
			return next.refactorSQL(originalSQL, statementHandler);
		}
		return null;
	}

	// static final String selectRegex = "(select\\s*)(.*\\s*)(from\\s*.*)";
	// static final Pattern selectPattern = Pattern.compile(selectRegex,
	// Pattern.CASE_INSENSITIVE);

}
