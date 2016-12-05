package org.hddframework.mybatis.locker.handler;

/**
 * Created by SongWei on 01/12/2016.
 */
public abstract class AbstractHandler {
	
    protected final String column;
    protected final String property;
    protected final SQLRefactorHandler next;

    public AbstractHandler(String column, String property, SQLRefactorHandler next) {
        this.column = column;
        this.property = property;
        this.next = next;
    }

}
