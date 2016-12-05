package org.hddframework.mybatis.locker.handler;

import java.util.regex.Pattern;

import org.hddframework.mybatis.locker.refactor.SQLRefactor;

/**
 * Created by SongWei on 30/11/2016.
 */
public interface SQLRefactorHandler extends SQLRefactor {

	static final String selectRegex = "(select\\s*)(.*\\s*)(from\\s*.*)";
	static final Pattern selectPattern = Pattern.compile(selectRegex, Pattern.CASE_INSENSITIVE);

	static final String insertRegex = "(insert\\s*into\\s*)(.*)(\\s*values\\s*)";
	static final Pattern insertPattern = Pattern.compile(insertRegex, Pattern.CASE_INSENSITIVE);

	static final String insertRegex_0 = "(\\)\\s*values\\s*\\().*";
	static final String insertRegex_1 = "(\\)\\s*,\\s*\\()";
	static final String insertRegex_2 = "\\)\\s*$";

	static final Pattern insertPattern_start = Pattern.compile(insertRegex_0, Pattern.CASE_INSENSITIVE);
	static final Pattern insertPattern_end = Pattern.compile(insertRegex_2, Pattern.CASE_INSENSITIVE);

	static final String updateRegex = "(update\\s*)(.*)(\\s*set\\s*)";
	static final Pattern updatePattern = Pattern.compile(updateRegex, Pattern.CASE_INSENSITIVE);

	static final String deleteRegex = "(delete\\s*from\\s*)(.*)";
	static final Pattern deletePattern = Pattern.compile(deleteRegex, Pattern.CASE_INSENSITIVE);

	static final String whereRegex = "(\\s*where\\s*)(.*)";
	static final Pattern wherePattern = Pattern.compile(whereRegex, Pattern.CASE_INSENSITIVE);

}
