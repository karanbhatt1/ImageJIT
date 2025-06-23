// Generated from Imagescript.g4 by ANTLR 4.13.1

    package com.parsingfiles;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ImagescriptParser}.
 */
public interface ImagescriptListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ImagescriptParser#script}.
	 * @param ctx the parse tree
	 */
	void enterScript(ImagescriptParser.ScriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link ImagescriptParser#script}.
	 * @param ctx the parse tree
	 */
	void exitScript(ImagescriptParser.ScriptContext ctx);
	/**
	 * Enter a parse tree produced by {@link ImagescriptParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(ImagescriptParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ImagescriptParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(ImagescriptParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ImagescriptParser#loadStatement}.
	 * @param ctx the parse tree
	 */
	void enterLoadStatement(ImagescriptParser.LoadStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ImagescriptParser#loadStatement}.
	 * @param ctx the parse tree
	 */
	void exitLoadStatement(ImagescriptParser.LoadStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ImagescriptParser#resizeStatement}.
	 * @param ctx the parse tree
	 */
	void enterResizeStatement(ImagescriptParser.ResizeStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ImagescriptParser#resizeStatement}.
	 * @param ctx the parse tree
	 */
	void exitResizeStatement(ImagescriptParser.ResizeStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ImagescriptParser#grayscaleStatement}.
	 * @param ctx the parse tree
	 */
	void enterGrayscaleStatement(ImagescriptParser.GrayscaleStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ImagescriptParser#grayscaleStatement}.
	 * @param ctx the parse tree
	 */
	void exitGrayscaleStatement(ImagescriptParser.GrayscaleStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ImagescriptParser#rotateStatement}.
	 * @param ctx the parse tree
	 */
	void enterRotateStatement(ImagescriptParser.RotateStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ImagescriptParser#rotateStatement}.
	 * @param ctx the parse tree
	 */
	void exitRotateStatement(ImagescriptParser.RotateStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ImagescriptParser#flipStatement}.
	 * @param ctx the parse tree
	 */
	void enterFlipStatement(ImagescriptParser.FlipStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ImagescriptParser#flipStatement}.
	 * @param ctx the parse tree
	 */
	void exitFlipStatement(ImagescriptParser.FlipStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ImagescriptParser#flipDirection}.
	 * @param ctx the parse tree
	 */
	void enterFlipDirection(ImagescriptParser.FlipDirectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ImagescriptParser#flipDirection}.
	 * @param ctx the parse tree
	 */
	void exitFlipDirection(ImagescriptParser.FlipDirectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ImagescriptParser#saveStatement}.
	 * @param ctx the parse tree
	 */
	void enterSaveStatement(ImagescriptParser.SaveStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ImagescriptParser#saveStatement}.
	 * @param ctx the parse tree
	 */
	void exitSaveStatement(ImagescriptParser.SaveStatementContext ctx);
}