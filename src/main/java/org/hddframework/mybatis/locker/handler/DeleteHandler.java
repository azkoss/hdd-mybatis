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
public class DeleteHandler extends AbstractHandler implements SQLRefactorHandler {

	public DeleteHandler(String column, String property, SQLRefactorHandler next) {
		super(column, property, next);
	}

	@Override
	public String refactorSQL(String originalSQL, StatementHandler statementHandler) {
		if (deletePattern.matcher(originalSQL).find()) {
			String changedSQL = null;
			ParameterHandler parameterHandler = statementHandler.getParameterHandler();
			if (!Objects.isNull(parameterHandler)) {
				Object parameterObject = parameterHandler.getParameterObject();
				MetaObject metaObject = SystemMetaObject.forObject(parameterObject);
				boolean hasProperty = metaObject.hasGetter(this.property);
				Object parameterValue = null;
				Class<?> parameterType = null;
				if (hasProperty) {
					parameterValue = metaObject.getValue(this.property);
					parameterType = metaObject.getGetterType(this.property);
					String step = DefaultValueGenerator.defaultStepping(parameterType.getName());
					Matcher m = wherePattern.matcher(originalSQL);
					if (m.find()) {
						StringBuffer sb = new StringBuffer();
						if (DefaultValueGenerator.isNumber(step)) {
							m.appendReplacement(sb, "$0" + " and " + this.column + "=" + parameterValue);
							changedSQL = sb.toString();
						} else {

						}
					} else {
						changedSQL = originalSQL + " where " + this.column + "=" + parameterValue;
					}
				} else {
					if (parameterObject instanceof MapperMethod.ParamMap) {
						boolean containProperty = true;
						MapperMethod.ParamMap<?> methodMap = (MapperMethod.ParamMap<?>) parameterObject;
						Set<?> set = methodMap.keySet();
						Iterator<?> iterator = set.iterator();
						while (iterator.hasNext()) {
							Object key = iterator.next();
							List<?> entityList = (List<?>) methodMap.get(key);
							for (Object entity : entityList) {
								MetaObject entityObject = SystemMetaObject.forObject(entity);
								if (!entityObject.hasGetter(this.property)) {
									containProperty = false;
									break;
								}
							}
							if (containProperty) {
								parameterObject = entityList.get(0);
								metaObject = SystemMetaObject.forObject(parameterObject);
								parameterValue = metaObject.getValue(this.property);
								parameterType = metaObject.getGetterType(this.property);
							} else {
								break;
							}
						}
						if (!containProperty) {
							changedSQL = originalSQL;
						} else {
							String step = DefaultValueGenerator.defaultStepping(parameterType);
							String[] updateSqls = originalSQL.split(";");
							changedSQL = "";
							for (String update : updateSqls) {
								Matcher m = wherePattern.matcher(update);
								if (m.find()) {
									StringBuffer sb = new StringBuffer();
									if (DefaultValueGenerator.isNumber(step)) {
										m.appendReplacement(sb, "$0" + " and " + this.column + "=" + parameterValue);
										changedSQL += sb.toString() + " ; ";
									}
								} else {
									changedSQL += originalSQL + " where " + this.column + "=" + parameterValue + " ; ";
								}
							}
							changedSQL = changedSQL.replaceFirst(";\\s*$", "");
						}

					} else {
						changedSQL = originalSQL;
					}
				}

			} else {
				changedSQL = originalSQL;
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

	// static final String deleteRegex = "(delete\\s*from\\s*)(.*)";
	// static final Pattern deletePattern = Pattern.compile(deleteRegex,
	// Pattern.CASE_INSENSITIVE);
	// static final String whereRegex = "(\\s*where\\s*)(.*)";
	// static final Pattern wherePattern = Pattern.compile(whereRegex,
	// Pattern.CASE_INSENSITIVE);

}
