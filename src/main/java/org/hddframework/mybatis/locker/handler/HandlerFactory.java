package org.hddframework.mybatis.locker.handler;

import java.util.Objects;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.hddframework.mybatis.locker.refactor.SQLRefactor;

/**
 * Created by SongWei on 01/12/2016.
 */
public class HandlerFactory {

	public static SQLRefactor build(String column, String property) {
		DefaultHandler defaultHandler = new DefaultHandler();
		SQLRefactorHandler deleteHandler = new DeleteHandler(column, property, defaultHandler);
		SQLRefactorHandler updateHandler = new UpdateHandler(column, property, deleteHandler);
		SQLRefactorHandler insertHandler = new InsertHandler(column, property, updateHandler);
		SQLRefactorHandler selectHandler = new SelectHandler(column, property, insertHandler);
		SQLRefactorHandler complexHandler = new ComplexHandler(column, property, selectHandler);
		HandlerChain chain = new HandlerChain(complexHandler);
		return chain;
	}

	private static class HandlerChain implements SQLRefactorHandler {

		private final SQLRefactorHandler next;

		public HandlerChain(SQLRefactorHandler next) {
			this.next = next;
		}

		@Override
		public String refactorSQL(String originalSQL, StatementHandler statementHandler) {
			if (!Objects.isNull(next)) {
				return next.refactorSQL(originalSQL, statementHandler);
			}
			return null;
		}

	}

}
