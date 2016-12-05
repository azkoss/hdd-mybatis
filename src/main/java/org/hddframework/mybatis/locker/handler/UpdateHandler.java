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
public class UpdateHandler extends AbstractHandler implements SQLRefactorHandler {

	public UpdateHandler(String column, String property, SQLRefactorHandler next) {
		super(column, property, next);
	}

	@Override
	public String refactorSQL(String originalSQL, StatementHandler statementHandler) {
		if (updatePattern.matcher(originalSQL).find()) {
			String changedSQL = null;
			ParameterHandler parameterHandler = statementHandler.getParameterHandler();
			if (!Objects.isNull(parameterHandler)) {
				Object parameterObject = parameterHandler.getParameterObject();
				MetaObject metaObject = SystemMetaObject.forObject(parameterObject);
				Object parameterValue = null;
				Class<?> parameterType = null;

				boolean hasProperty = metaObject.hasGetter(this.property);
				if (hasProperty) {
					parameterValue = metaObject.getValue(this.property);
					parameterType = metaObject.getGetterType(this.property);

					String step = DefaultValueGenerator.defaultStepping(parameterType.getName());

					Matcher m = wherePattern.matcher(originalSQL);
					if (m.find()) {
						StringBuffer sb = new StringBuffer();
						if (DefaultValueGenerator.isNumber(step)) {
							m.appendReplacement(sb, ", " + this.column + " = " + this.column + "+" + step + " " + "$0"
									+ " and " + this.column + "=" + parameterValue);
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
								MetaObject metaEntity = SystemMetaObject.forObject(entity);
								if (!metaEntity.hasGetter(this.property)) {
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
							String[] updateSqls = originalSQL.split(";");
							String step = DefaultValueGenerator.defaultStepping(parameterType);
							changedSQL = "";
							for (String update : updateSqls) {
								Matcher m = wherePattern.matcher(update);
								if (m.find()) {
									StringBuffer sb = new StringBuffer();
									if (DefaultValueGenerator.isNumber(step)) {
										m.appendReplacement(sb, ", " + this.column + " = " + this.column + "+" + step
												+ " " + "$0" + " and " + this.column + "=" + parameterValue);
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

	// static final String updateRegex = "(update\\s*)(.*)(\\s*set\\s*)";
	// static final Pattern updatePattern = Pattern.compile(updateRegex,
	// Pattern.CASE_INSENSITIVE);
	// static final String whereRegex = "(\\s*where\\s*)(.*)";
	// static final Pattern wherePattern = Pattern.compile(whereRegex,
	// Pattern.CASE_INSENSITIVE);

}
