package org.hddframework.mybatis.locker.refactor;

import org.apache.ibatis.executor.statement.StatementHandler;

/**
 * Created by SongWei on 30/11/2016.
 */
public interface SQLRefactor {

     String refactorSQL(String originalSQL, StatementHandler statementHandler);

}
