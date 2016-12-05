package org.hddframework.mybatis.locker.refactor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.hddframework.mybatis.locker.handler.HandlerFactory;

/**
 * Created by SongWei on 30/11/2016.
 */
public class SQLRefactorFactory {

    public static SQLRefactor buildNewSQLRefactor(String column, String property) {
        return new SQLBuilder(column, property);
    }
    
     private static class SQLBuilder implements SQLRefactor {

        protected final String column;
        protected final String property;

        public SQLBuilder(String column, String property) {
            this.column = column;
            this.property = property;
        }

        @Override
        public String refactorSQL(String originalSQL, StatementHandler statementHandler) {

            return HandlerFactory.build(this.column, this.property).refactorSQL(originalSQL, statementHandler);

        }
     }

}
