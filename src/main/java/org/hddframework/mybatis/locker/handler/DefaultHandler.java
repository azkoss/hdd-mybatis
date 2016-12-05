package org.hddframework.mybatis.locker.handler;

import org.apache.ibatis.executor.statement.StatementHandler;

/**
 * Created by SongWei on 30/11/2016.
 */
public class DefaultHandler implements SQLRefactorHandler {

    @Override
    public String refactorSQL(String originalSQL, StatementHandler statementHandler) {
        return originalSQL;
    }

}
