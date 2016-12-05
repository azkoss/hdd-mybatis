package org.hddframework.mybatis.locker.handler;

import java.util.regex.Pattern;

import org.apache.ibatis.executor.statement.StatementHandler;

/**
 * Created by SongWei on 01/12/2016.
 */
public class ComplexHandler extends AbstractHandler implements SQLRefactorHandler {

	public ComplexHandler(String column, String property, SQLRefactorHandler next) {
        super(column, property, next);
    }

    @Override
    public String refactorSQL(String originalSQL, StatementHandler statementHandler) {
    	if (isComplexSQL(originalSQL)) {
            return originalSQL;
        }
        return next.refactorSQL(originalSQL, statementHandler);
    }
    
    private boolean isComplexSQL(String sql) {
        if ((RegexClass.selectPattern.matcher(sql).find() && RegexClass.insertPattern.matcher(sql).find()) ||
                (RegexClass.selectPattern.matcher(sql).find() && RegexClass.updatePattern.matcher(sql).find()) ||
                (RegexClass.selectPattern.matcher(sql).find() && RegexClass.deletePattern.matcher(sql).find()) ||
                (RegexClass.insertPattern.matcher(sql).find() && RegexClass.updatePattern.matcher(sql).find()) ||
                (RegexClass.insertPattern.matcher(sql).find() && RegexClass.deletePattern.matcher(sql).find()) ||
                (RegexClass.updatePattern.matcher(sql).find() && RegexClass.deletePattern.matcher(sql).find())) {
            return true;
        }
        return false;
    }

    static class RegexClass {
        static final String selectRegex = "(\\s*select\\s*)(.*)(\\s*from\\s*)";
        static final String insertRegex = "(\\s*insert\\s*into\\s*)";
        static final String updateRegex = "(\\s*update\\s*)(.*)(\\s*set\\s*)";
        static final String deleteRegex = "(\\s*delete\\s*from\\s*)";

        static final Pattern selectPattern = Pattern.compile(selectRegex, Pattern.CASE_INSENSITIVE);
        static final Pattern insertPattern = Pattern.compile(insertRegex, Pattern.CASE_INSENSITIVE);
        static final Pattern updatePattern = Pattern.compile(updateRegex, Pattern.CASE_INSENSITIVE);
        static final Pattern deletePattern = Pattern.compile(deleteRegex, Pattern.CASE_INSENSITIVE);
    }
    
}
