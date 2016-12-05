package org.hddframework.mybatis.locker.handler;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.hddframework.mybatis.locker.gen.DefaultValueGenerator;

/**
 * Created by SongWei on 30/11/2016.
 */
public class InsertHandler extends AbstractHandler implements SQLRefactorHandler {

	public InsertHandler(String column, String property, SQLRefactorHandler next) {
		super(column, property, next);
	}

	@Override
	public String refactorSQL(String originalSQL, StatementHandler statementHandler) {
		if (insertPattern.matcher(originalSQL).find()) {
			String changedSQL = null;
			ParameterHandler parameterHandler = statementHandler.getParameterHandler();
			if (!Objects.isNull(parameterHandler)) {
				Object parameterObject = parameterHandler.getParameterObject();
				MetaObject metaObject = SystemMetaObject.forObject(parameterObject);
				boolean hasProperty = metaObject.hasGetter(this.property);
				if (!hasProperty) {
					if (parameterObject instanceof MapperMethod.ParamMap) {
						boolean containProperty = true;
						MapperMethod.ParamMap<?> methodMap = (MapperMethod.ParamMap<?>) parameterObject;
						Set<?> set = methodMap.keySet();
						Iterator<?> iterator = set.iterator();
						while (iterator.hasNext()) {
							Object key = iterator.next();
							List<?> entityList = (List<?>) methodMap.get(key);
							for (Object entity : entityList) {
								MetaObject metaEntity = SystemMetaObject.forObject(entity);
								if (!metaEntity.hasGetter(this.property)) {
									containProperty = false;
									break;
								}
							}
							if (!containProperty) {
								break;
							} else {
								parameterObject = entityList.get(0);
								metaObject = SystemMetaObject.forObject(parameterObject);
							}
						}
						if (!containProperty) {
							changedSQL = originalSQL;
						} else {
							changedSQL = getInsertSql(originalSQL, metaObject.getValue(this.property),
									metaObject.getGetterType(this.property));
						}
					} else {
						changedSQL = originalSQL;
					}
				} else {
					changedSQL = getInsertSql(originalSQL, metaObject.getValue(this.property),
							metaObject.getGetterType(this.property));
				}
			}
			if (StringUtils.isBlank(changedSQL)) {
				return originalSQL;
			}
			return changedSQL;
		}
		if (!Objects.isNull(next)) {
			return next.refactorSQL(originalSQL, statementHandler);
		}
		return null;
	}

	private String getInsertSql(String originalSql, Object parameterValue, Class<?> fieldClass) {
		String typeName = fieldClass.getName();
		if (Objects.isNull(parameterValue)) {
			parameterValue = DefaultValueGenerator.defaultValue(typeName);
		}
		String sql = reBuildInsertSql(originalSql, parameterValue);
		return sql;
	}

	private String reBuildInsertSql(String originalSql, Object parameterValue) {
		String changedSql = null;
		Matcher m0 = insertPattern_start.matcher(originalSql);
		if (m0.find()) {
			StringBuffer sb = new StringBuffer();
			m0.appendReplacement(sb, "," + this.column + "$0");
			changedSql = sb.toString();
			if (DefaultValueGenerator.isNumber(parameterValue)) {
				changedSql = changedSql.replaceAll(insertRegex_1, ", " + parameterValue + " ), ( ");
			} else {
				changedSql = changedSql.replaceAll(insertRegex_1, ", '" + parameterValue + "' ), ( ");
			}
			Matcher m2 = insertPattern_end.matcher(changedSql);
			if (m2.find()) {
				sb = new StringBuffer();
				if (DefaultValueGenerator.isNumber(parameterValue)) {
					m2.appendReplacement(sb, ", " + parameterValue + "$0");
				} else {
					m2.appendReplacement(sb, ", '" + parameterValue + "'" + "$0");
				}
				changedSql = sb.toString();
			}
			return changedSql;
		}
		return originalSql;
	}

	// static final String insertRegex =
	// "(insert\\s*into\\s*)(.*)(\\s*values\\s*)";
	// static final Pattern insertPattern = Pattern.compile(insertRegex,
	// Pattern.CASE_INSENSITIVE);
	//
	// static final String insertRegex_0 = "(\\)\\s*values\\s*\\().*";
	// static final String insertRegex_1 = "(\\)\\s*,\\s*\\()";
	// static final String insertRegex_2 = "\\)\\s*$";
	//
	// static final Pattern insertPattern_start = Pattern.compile(insertRegex_0,
	// Pattern.CASE_INSENSITIVE);
	// static final Pattern insertPattern_end = Pattern.compile(insertRegex_2,
	// Pattern.CASE_INSENSITIVE);

}
