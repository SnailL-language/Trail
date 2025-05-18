// Generated from Snail.g4 by ANTLR 4.13.2

package io.github.snaill.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SnailParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, NUMBER=44, STRING=45, 
		IDENTIFIER=46, WS=47, LINE_COMMENT=48, BLOCK_COMMENT=49;
	public static final int
		RULE_program = 0, RULE_statement = 1, RULE_funcDeclaration = 2, RULE_paramList = 3, 
		RULE_param = 4, RULE_argumentList = 5, RULE_scope = 6, RULE_variableDeclaration = 7, 
		RULE_forLoop = 8, RULE_whileLoop = 9, RULE_ifCondition = 10, RULE_breakStatement = 11, 
		RULE_returnStatement = 12, RULE_expression = 13, RULE_assigmentExpression = 14, 
		RULE_binaryExpression = 15, RULE_unaryExpression = 16, RULE_primaryExpression = 17, 
		RULE_literal = 18, RULE_stringLiteral = 19, RULE_numberLiteral = 20, RULE_booleanLiteral = 21, 
		RULE_identifier = 22, RULE_functionCall = 23, RULE_arrayLiteral = 24, 
		RULE_type = 25, RULE_arrayType = 26, RULE_primitiveType = 27;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "statement", "funcDeclaration", "paramList", "param", "argumentList", 
			"scope", "variableDeclaration", "forLoop", "whileLoop", "ifCondition", 
			"breakStatement", "returnStatement", "expression", "assigmentExpression", 
			"binaryExpression", "unaryExpression", "primaryExpression", "literal", 
			"stringLiteral", "numberLiteral", "booleanLiteral", "identifier", "functionCall", 
			"arrayLiteral", "type", "arrayType", "primitiveType"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'fn'", "'('", "')'", "'->'", "','", "':'", "'{'", "'}'", 
			"'let'", "'='", "'for'", "'while'", "'if'", "'else'", "'break'", "'return'", 
			"'+='", "'-='", "'*='", "'/='", "'||'", "'&&'", "'=='", "'!='", "'>'", 
			"'<'", "'>='", "'<='", "'+'", "'-'", "'*'", "'/'", "'!'", "'true'", "'false'", 
			"'['", "']'", "'i32'", "'usize'", "'void'", "'string'", "'bool'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, "NUMBER", "STRING", "IDENTIFIER", 
			"WS", "LINE_COMMENT", "BLOCK_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Snail.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SnailParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SnailParser.EOF, 0); }
		public List<VariableDeclarationContext> variableDeclaration() {
			return getRuleContexts(VariableDeclarationContext.class);
		}
		public VariableDeclarationContext variableDeclaration(int i) {
			return getRuleContext(VariableDeclarationContext.class,i);
		}
		public List<FuncDeclarationContext> funcDeclaration() {
			return getRuleContexts(FuncDeclarationContext.class);
		}
		public FuncDeclarationContext funcDeclaration(int i) {
			return getRuleContext(FuncDeclarationContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__9) {
				{
				{
				setState(56);
				variableDeclaration();
				}
				}
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(63); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(62);
				funcDeclaration();
				}
				}
				setState(65); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__1 );
			setState(67);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StatementContext extends ParserRuleContext {
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public ForLoopContext forLoop() {
			return getRuleContext(ForLoopContext.class,0);
		}
		public FuncDeclarationContext funcDeclaration() {
			return getRuleContext(FuncDeclarationContext.class,0);
		}
		public WhileLoopContext whileLoop() {
			return getRuleContext(WhileLoopContext.class,0);
		}
		public IfConditionContext ifCondition() {
			return getRuleContext(IfConditionContext.class,0);
		}
		public BreakStatementContext breakStatement() {
			return getRuleContext(BreakStatementContext.class,0);
		}
		public ReturnStatementContext returnStatement() {
			return getRuleContext(ReturnStatementContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statement);
		try {
			setState(79);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__9:
				enterOuterAlt(_localctx, 1);
				{
				setState(69);
				variableDeclaration();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 2);
				{
				setState(70);
				forLoop();
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 3);
				{
				setState(71);
				funcDeclaration();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 4);
				{
				setState(72);
				whileLoop();
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 5);
				{
				setState(73);
				ifCondition();
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 6);
				{
				setState(74);
				breakStatement();
				}
				break;
			case T__16:
				enterOuterAlt(_localctx, 7);
				{
				setState(75);
				returnStatement();
				}
				break;
			case T__2:
			case T__30:
			case T__33:
			case T__34:
			case T__35:
			case T__36:
			case NUMBER:
			case STRING:
			case IDENTIFIER:
				enterOuterAlt(_localctx, 8);
				{
				setState(76);
				expression();
				setState(77);
				match(T__0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FuncDeclarationContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SnailParser.IDENTIFIER, 0); }
		public ParamListContext paramList() {
			return getRuleContext(ParamListContext.class,0);
		}
		public ScopeContext scope() {
			return getRuleContext(ScopeContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public FuncDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterFuncDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitFuncDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitFuncDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncDeclarationContext funcDeclaration() throws RecognitionException {
		FuncDeclarationContext _localctx = new FuncDeclarationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_funcDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
			match(T__1);
			setState(82);
			match(IDENTIFIER);
			setState(83);
			match(T__2);
			setState(84);
			paramList();
			setState(85);
			match(T__3);
			setState(88);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(86);
				match(T__4);
				setState(87);
				type();
				}
			}

			setState(90);
			scope();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParamListContext extends ParserRuleContext {
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public ParamListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_paramList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterParamList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitParamList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitParamList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamListContext paramList() throws RecognitionException {
		ParamListContext _localctx = new ParamListContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_paramList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(92);
				param();
				setState(97);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(93);
					match(T__5);
					setState(94);
					param();
					}
					}
					setState(99);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParamContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SnailParser.IDENTIFIER, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102);
			match(IDENTIFIER);
			setState(103);
			match(T__6);
			setState(104);
			type();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentListContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ArgumentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterArgumentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitArgumentList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitArgumentList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentListContext argumentList() throws RecognitionException {
		ArgumentListContext _localctx = new ArgumentListContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_argumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 123405147832328L) != 0)) {
				{
				setState(106);
				expression();
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(107);
					match(T__5);
					setState(108);
					expression();
					}
					}
					setState(113);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ScopeContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ScopeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scope; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterScope(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitScope(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitScope(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScopeContext scope() throws RecognitionException {
		ScopeContext _localctx = new ScopeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_scope);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			match(T__7);
			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 123405148058636L) != 0)) {
				{
				{
				setState(117);
				statement();
				}
				}
				setState(122);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(123);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariableDeclarationContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SnailParser.IDENTIFIER, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public VariableDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitVariableDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitVariableDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableDeclarationContext variableDeclaration() throws RecognitionException {
		VariableDeclarationContext _localctx = new VariableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_variableDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(125);
			match(T__9);
			setState(126);
			match(IDENTIFIER);
			setState(127);
			match(T__6);
			setState(128);
			type();
			setState(129);
			match(T__10);
			setState(130);
			expression();
			setState(131);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ForLoopContext extends ParserRuleContext {
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ScopeContext scope() {
			return getRuleContext(ScopeContext.class,0);
		}
		public ForLoopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forLoop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterForLoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitForLoop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitForLoop(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForLoopContext forLoop() throws RecognitionException {
		ForLoopContext _localctx = new ForLoopContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_forLoop);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
			match(T__11);
			setState(134);
			match(T__2);
			setState(135);
			variableDeclaration();
			setState(136);
			expression();
			setState(137);
			match(T__0);
			setState(138);
			expression();
			setState(139);
			match(T__3);
			setState(140);
			scope();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class WhileLoopContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ScopeContext scope() {
			return getRuleContext(ScopeContext.class,0);
		}
		public WhileLoopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whileLoop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterWhileLoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitWhileLoop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitWhileLoop(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhileLoopContext whileLoop() throws RecognitionException {
		WhileLoopContext _localctx = new WhileLoopContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_whileLoop);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142);
			match(T__12);
			setState(143);
			match(T__2);
			setState(144);
			expression();
			setState(145);
			match(T__3);
			setState(146);
			scope();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IfConditionContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<ScopeContext> scope() {
			return getRuleContexts(ScopeContext.class);
		}
		public ScopeContext scope(int i) {
			return getRuleContext(ScopeContext.class,i);
		}
		public IfConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifCondition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterIfCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitIfCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitIfCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfConditionContext ifCondition() throws RecognitionException {
		IfConditionContext _localctx = new IfConditionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_ifCondition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(148);
			match(T__13);
			setState(149);
			match(T__2);
			setState(150);
			expression();
			setState(151);
			match(T__3);
			setState(152);
			scope();
			setState(155);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(153);
				match(T__14);
				setState(154);
				scope();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BreakStatementContext extends ParserRuleContext {
		public BreakStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterBreakStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitBreakStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitBreakStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BreakStatementContext breakStatement() throws RecognitionException {
		BreakStatementContext _localctx = new BreakStatementContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_breakStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			match(T__15);
			setState(158);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ReturnStatementContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterReturnStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitReturnStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitReturnStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReturnStatementContext returnStatement() throws RecognitionException {
		ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_returnStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			match(T__16);
			setState(162);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 123405147832328L) != 0)) {
				{
				setState(161);
				expression();
				}
			}

			setState(164);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionContext extends ParserRuleContext {
		public AssigmentExpressionContext assigmentExpression() {
			return getRuleContext(AssigmentExpressionContext.class,0);
		}
		public BinaryExpressionContext binaryExpression() {
			return getRuleContext(BinaryExpressionContext.class,0);
		}
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
		public PrimaryExpressionContext primaryExpression() {
			return getRuleContext(PrimaryExpressionContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_expression);
		try {
			setState(174);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(166);
				assigmentExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(167);
				binaryExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(168);
				unaryExpression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(169);
				primaryExpression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(170);
				match(T__2);
				setState(171);
				expression();
				setState(172);
				match(T__3);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssigmentExpressionContext extends ParserRuleContext {
		public Token assigmentOperator;
		public TerminalNode IDENTIFIER() { return getToken(SnailParser.IDENTIFIER, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AssigmentExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assigmentExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterAssigmentExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitAssigmentExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitAssigmentExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssigmentExpressionContext assigmentExpression() throws RecognitionException {
		AssigmentExpressionContext _localctx = new AssigmentExpressionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_assigmentExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(176);
			match(IDENTIFIER);
			setState(177);
			((AssigmentExpressionContext)_localctx).assigmentOperator = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 3934208L) != 0)) ) {
				((AssigmentExpressionContext)_localctx).assigmentOperator = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(178);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BinaryExpressionContext extends ParserRuleContext {
		public Token binaryOperator;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public PrimaryExpressionContext primaryExpression() {
			return getRuleContext(PrimaryExpressionContext.class,0);
		}
		public BinaryExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binaryExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterBinaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitBinaryExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitBinaryExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BinaryExpressionContext binaryExpression() throws RecognitionException {
		BinaryExpressionContext _localctx = new BinaryExpressionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_binaryExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(185);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__34:
			case T__35:
			case T__36:
			case NUMBER:
			case STRING:
			case IDENTIFIER:
				{
				setState(180);
				primaryExpression();
				}
				break;
			case T__2:
				{
				setState(181);
				match(T__2);
				setState(182);
				expression();
				setState(183);
				match(T__3);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(187);
			((BinaryExpressionContext)_localctx).binaryOperator = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 17175674880L) != 0)) ) {
				((BinaryExpressionContext)_localctx).binaryOperator = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(188);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UnaryExpressionContext extends ParserRuleContext {
		public Token unaryOperator;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public UnaryExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterUnaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitUnaryExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitUnaryExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryExpressionContext unaryExpression() throws RecognitionException {
		UnaryExpressionContext _localctx = new UnaryExpressionContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_unaryExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			((UnaryExpressionContext)_localctx).unaryOperator = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==T__30 || _la==T__33) ) {
				((UnaryExpressionContext)_localctx).unaryOperator = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(191);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PrimaryExpressionContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
		}
		public ArrayLiteralContext arrayLiteral() {
			return getRuleContext(ArrayLiteralContext.class,0);
		}
		public PrimaryExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterPrimaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitPrimaryExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitPrimaryExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryExpressionContext primaryExpression() throws RecognitionException {
		PrimaryExpressionContext _localctx = new PrimaryExpressionContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_primaryExpression);
		try {
			setState(197);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(193);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(194);
				identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(195);
				functionCall();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(196);
				arrayLiteral();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LiteralContext extends ParserRuleContext {
		public NumberLiteralContext numberLiteral() {
			return getRuleContext(NumberLiteralContext.class,0);
		}
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public BooleanLiteralContext booleanLiteral() {
			return getRuleContext(BooleanLiteralContext.class,0);
		}
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_literal);
		try {
			setState(202);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(199);
				numberLiteral();
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(200);
				stringLiteral();
				}
				break;
			case T__34:
			case T__35:
				enterOuterAlt(_localctx, 3);
				{
				setState(201);
				booleanLiteral();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StringLiteralContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(SnailParser.STRING, 0); }
		public StringLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterStringLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitStringLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitStringLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringLiteralContext stringLiteral() throws RecognitionException {
		StringLiteralContext _localctx = new StringLiteralContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_stringLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(204);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NumberLiteralContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(SnailParser.NUMBER, 0); }
		public NumberLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numberLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterNumberLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitNumberLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitNumberLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberLiteralContext numberLiteral() throws RecognitionException {
		NumberLiteralContext _localctx = new NumberLiteralContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_numberLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BooleanLiteralContext extends ParserRuleContext {
		public BooleanLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterBooleanLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitBooleanLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitBooleanLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BooleanLiteralContext booleanLiteral() throws RecognitionException {
		BooleanLiteralContext _localctx = new BooleanLiteralContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_booleanLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			_la = _input.LA(1);
			if ( !(_la==T__34 || _la==T__35) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdentifierContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SnailParser.IDENTIFIER, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionCallContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SnailParser.IDENTIFIER, 0); }
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public FunctionCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionCallContext functionCall() throws RecognitionException {
		FunctionCallContext _localctx = new FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_functionCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(212);
			match(IDENTIFIER);
			setState(213);
			match(T__2);
			setState(214);
			argumentList();
			setState(215);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayLiteralContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ArrayLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterArrayLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitArrayLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitArrayLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayLiteralContext arrayLiteral() throws RecognitionException {
		ArrayLiteralContext _localctx = new ArrayLiteralContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_arrayLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			match(T__36);
			setState(226);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 123405147832328L) != 0)) {
				{
				setState(218);
				expression();
				setState(223);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(219);
					match(T__5);
					setState(220);
					expression();
					}
					}
					setState(225);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(228);
			match(T__37);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeContext extends ParserRuleContext {
		public PrimitiveTypeContext primitiveType() {
			return getRuleContext(PrimitiveTypeContext.class,0);
		}
		public ArrayTypeContext arrayType() {
			return getRuleContext(ArrayTypeContext.class,0);
		}
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_type);
		try {
			setState(232);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__38:
			case T__39:
			case T__40:
			case T__41:
			case T__42:
				enterOuterAlt(_localctx, 1);
				{
				setState(230);
				primitiveType();
				}
				break;
			case T__36:
				enterOuterAlt(_localctx, 2);
				{
				setState(231);
				arrayType();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayTypeContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public NumberLiteralContext numberLiteral() {
			return getRuleContext(NumberLiteralContext.class,0);
		}
		public ArrayTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitArrayType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitArrayType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayTypeContext arrayType() throws RecognitionException {
		ArrayTypeContext _localctx = new ArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_arrayType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(234);
			match(T__36);
			setState(235);
			type();
			setState(236);
			match(T__0);
			setState(237);
			numberLiteral();
			setState(238);
			match(T__37);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PrimitiveTypeContext extends ParserRuleContext {
		public PrimitiveTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primitiveType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterPrimitiveType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitPrimitiveType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitPrimitiveType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimitiveTypeContext primitiveType() throws RecognitionException {
		PrimitiveTypeContext _localctx = new PrimitiveTypeContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_primitiveType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(240);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 17042430230528L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u00011\u00f3\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0001\u0000\u0005\u0000:\b\u0000\n\u0000\f\u0000=\t\u0000\u0001\u0000"+
		"\u0004\u0000@\b\u0000\u000b\u0000\f\u0000A\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001P\b\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0003\u0002Y\b\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0005\u0003`\b\u0003\n\u0003\f\u0003c\t\u0003\u0003"+
		"\u0003e\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0005\u0005n\b\u0005\n\u0005\f\u0005q\t"+
		"\u0005\u0003\u0005s\b\u0005\u0001\u0006\u0001\u0006\u0005\u0006w\b\u0006"+
		"\n\u0006\f\u0006z\t\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u009c\b\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\f\u0001\f\u0003\f\u00a3\b\f\u0001\f\u0001\f\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u00af"+
		"\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u00ba\b\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u00c6\b\u0011\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u00cb\b\u0012\u0001\u0013\u0001"+
		"\u0013\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0001"+
		"\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0005\u0018\u00de\b\u0018\n"+
		"\u0018\f\u0018\u00e1\t\u0018\u0003\u0018\u00e3\b\u0018\u0001\u0018\u0001"+
		"\u0018\u0001\u0019\u0001\u0019\u0003\u0019\u00e9\b\u0019\u0001\u001a\u0001"+
		"\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0000\u0000\u001c\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.0246\u0000\u0005"+
		"\u0002\u0000\u000b\u000b\u0012\u0015\u0001\u0000\u0016!\u0002\u0000\u001f"+
		"\u001f\"\"\u0001\u0000#$\u0001\u0000\'+\u00f4\u0000;\u0001\u0000\u0000"+
		"\u0000\u0002O\u0001\u0000\u0000\u0000\u0004Q\u0001\u0000\u0000\u0000\u0006"+
		"d\u0001\u0000\u0000\u0000\bf\u0001\u0000\u0000\u0000\nr\u0001\u0000\u0000"+
		"\u0000\ft\u0001\u0000\u0000\u0000\u000e}\u0001\u0000\u0000\u0000\u0010"+
		"\u0085\u0001\u0000\u0000\u0000\u0012\u008e\u0001\u0000\u0000\u0000\u0014"+
		"\u0094\u0001\u0000\u0000\u0000\u0016\u009d\u0001\u0000\u0000\u0000\u0018"+
		"\u00a0\u0001\u0000\u0000\u0000\u001a\u00ae\u0001\u0000\u0000\u0000\u001c"+
		"\u00b0\u0001\u0000\u0000\u0000\u001e\u00b9\u0001\u0000\u0000\u0000 \u00be"+
		"\u0001\u0000\u0000\u0000\"\u00c5\u0001\u0000\u0000\u0000$\u00ca\u0001"+
		"\u0000\u0000\u0000&\u00cc\u0001\u0000\u0000\u0000(\u00ce\u0001\u0000\u0000"+
		"\u0000*\u00d0\u0001\u0000\u0000\u0000,\u00d2\u0001\u0000\u0000\u0000."+
		"\u00d4\u0001\u0000\u0000\u00000\u00d9\u0001\u0000\u0000\u00002\u00e8\u0001"+
		"\u0000\u0000\u00004\u00ea\u0001\u0000\u0000\u00006\u00f0\u0001\u0000\u0000"+
		"\u00008:\u0003\u000e\u0007\u000098\u0001\u0000\u0000\u0000:=\u0001\u0000"+
		"\u0000\u0000;9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<?\u0001"+
		"\u0000\u0000\u0000=;\u0001\u0000\u0000\u0000>@\u0003\u0004\u0002\u0000"+
		"?>\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000A?\u0001\u0000\u0000"+
		"\u0000AB\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000\u0000CD\u0005\u0000"+
		"\u0000\u0001D\u0001\u0001\u0000\u0000\u0000EP\u0003\u000e\u0007\u0000"+
		"FP\u0003\u0010\b\u0000GP\u0003\u0004\u0002\u0000HP\u0003\u0012\t\u0000"+
		"IP\u0003\u0014\n\u0000JP\u0003\u0016\u000b\u0000KP\u0003\u0018\f\u0000"+
		"LM\u0003\u001a\r\u0000MN\u0005\u0001\u0000\u0000NP\u0001\u0000\u0000\u0000"+
		"OE\u0001\u0000\u0000\u0000OF\u0001\u0000\u0000\u0000OG\u0001\u0000\u0000"+
		"\u0000OH\u0001\u0000\u0000\u0000OI\u0001\u0000\u0000\u0000OJ\u0001\u0000"+
		"\u0000\u0000OK\u0001\u0000\u0000\u0000OL\u0001\u0000\u0000\u0000P\u0003"+
		"\u0001\u0000\u0000\u0000QR\u0005\u0002\u0000\u0000RS\u0005.\u0000\u0000"+
		"ST\u0005\u0003\u0000\u0000TU\u0003\u0006\u0003\u0000UX\u0005\u0004\u0000"+
		"\u0000VW\u0005\u0005\u0000\u0000WY\u00032\u0019\u0000XV\u0001\u0000\u0000"+
		"\u0000XY\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000Z[\u0003\f\u0006"+
		"\u0000[\u0005\u0001\u0000\u0000\u0000\\a\u0003\b\u0004\u0000]^\u0005\u0006"+
		"\u0000\u0000^`\u0003\b\u0004\u0000_]\u0001\u0000\u0000\u0000`c\u0001\u0000"+
		"\u0000\u0000a_\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000be\u0001"+
		"\u0000\u0000\u0000ca\u0001\u0000\u0000\u0000d\\\u0001\u0000\u0000\u0000"+
		"de\u0001\u0000\u0000\u0000e\u0007\u0001\u0000\u0000\u0000fg\u0005.\u0000"+
		"\u0000gh\u0005\u0007\u0000\u0000hi\u00032\u0019\u0000i\t\u0001\u0000\u0000"+
		"\u0000jo\u0003\u001a\r\u0000kl\u0005\u0006\u0000\u0000ln\u0003\u001a\r"+
		"\u0000mk\u0001\u0000\u0000\u0000nq\u0001\u0000\u0000\u0000om\u0001\u0000"+
		"\u0000\u0000op\u0001\u0000\u0000\u0000ps\u0001\u0000\u0000\u0000qo\u0001"+
		"\u0000\u0000\u0000rj\u0001\u0000\u0000\u0000rs\u0001\u0000\u0000\u0000"+
		"s\u000b\u0001\u0000\u0000\u0000tx\u0005\b\u0000\u0000uw\u0003\u0002\u0001"+
		"\u0000vu\u0001\u0000\u0000\u0000wz\u0001\u0000\u0000\u0000xv\u0001\u0000"+
		"\u0000\u0000xy\u0001\u0000\u0000\u0000y{\u0001\u0000\u0000\u0000zx\u0001"+
		"\u0000\u0000\u0000{|\u0005\t\u0000\u0000|\r\u0001\u0000\u0000\u0000}~"+
		"\u0005\n\u0000\u0000~\u007f\u0005.\u0000\u0000\u007f\u0080\u0005\u0007"+
		"\u0000\u0000\u0080\u0081\u00032\u0019\u0000\u0081\u0082\u0005\u000b\u0000"+
		"\u0000\u0082\u0083\u0003\u001a\r\u0000\u0083\u0084\u0005\u0001\u0000\u0000"+
		"\u0084\u000f\u0001\u0000\u0000\u0000\u0085\u0086\u0005\f\u0000\u0000\u0086"+
		"\u0087\u0005\u0003\u0000\u0000\u0087\u0088\u0003\u000e\u0007\u0000\u0088"+
		"\u0089\u0003\u001a\r\u0000\u0089\u008a\u0005\u0001\u0000\u0000\u008a\u008b"+
		"\u0003\u001a\r\u0000\u008b\u008c\u0005\u0004\u0000\u0000\u008c\u008d\u0003"+
		"\f\u0006\u0000\u008d\u0011\u0001\u0000\u0000\u0000\u008e\u008f\u0005\r"+
		"\u0000\u0000\u008f\u0090\u0005\u0003\u0000\u0000\u0090\u0091\u0003\u001a"+
		"\r\u0000\u0091\u0092\u0005\u0004\u0000\u0000\u0092\u0093\u0003\f\u0006"+
		"\u0000\u0093\u0013\u0001\u0000\u0000\u0000\u0094\u0095\u0005\u000e\u0000"+
		"\u0000\u0095\u0096\u0005\u0003\u0000\u0000\u0096\u0097\u0003\u001a\r\u0000"+
		"\u0097\u0098\u0005\u0004\u0000\u0000\u0098\u009b\u0003\f\u0006\u0000\u0099"+
		"\u009a\u0005\u000f\u0000\u0000\u009a\u009c\u0003\f\u0006\u0000\u009b\u0099"+
		"\u0001\u0000\u0000\u0000\u009b\u009c\u0001\u0000\u0000\u0000\u009c\u0015"+
		"\u0001\u0000\u0000\u0000\u009d\u009e\u0005\u0010\u0000\u0000\u009e\u009f"+
		"\u0005\u0001\u0000\u0000\u009f\u0017\u0001\u0000\u0000\u0000\u00a0\u00a2"+
		"\u0005\u0011\u0000\u0000\u00a1\u00a3\u0003\u001a\r\u0000\u00a2\u00a1\u0001"+
		"\u0000\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000\u0000\u00a3\u00a4\u0001"+
		"\u0000\u0000\u0000\u00a4\u00a5\u0005\u0001\u0000\u0000\u00a5\u0019\u0001"+
		"\u0000\u0000\u0000\u00a6\u00af\u0003\u001c\u000e\u0000\u00a7\u00af\u0003"+
		"\u001e\u000f\u0000\u00a8\u00af\u0003 \u0010\u0000\u00a9\u00af\u0003\""+
		"\u0011\u0000\u00aa\u00ab\u0005\u0003\u0000\u0000\u00ab\u00ac\u0003\u001a"+
		"\r\u0000\u00ac\u00ad\u0005\u0004\u0000\u0000\u00ad\u00af\u0001\u0000\u0000"+
		"\u0000\u00ae\u00a6\u0001\u0000\u0000\u0000\u00ae\u00a7\u0001\u0000\u0000"+
		"\u0000\u00ae\u00a8\u0001\u0000\u0000\u0000\u00ae\u00a9\u0001\u0000\u0000"+
		"\u0000\u00ae\u00aa\u0001\u0000\u0000\u0000\u00af\u001b\u0001\u0000\u0000"+
		"\u0000\u00b0\u00b1\u0005.\u0000\u0000\u00b1\u00b2\u0007\u0000\u0000\u0000"+
		"\u00b2\u00b3\u0003\u001a\r\u0000\u00b3\u001d\u0001\u0000\u0000\u0000\u00b4"+
		"\u00ba\u0003\"\u0011\u0000\u00b5\u00b6\u0005\u0003\u0000\u0000\u00b6\u00b7"+
		"\u0003\u001a\r\u0000\u00b7\u00b8\u0005\u0004\u0000\u0000\u00b8\u00ba\u0001"+
		"\u0000\u0000\u0000\u00b9\u00b4\u0001\u0000\u0000\u0000\u00b9\u00b5\u0001"+
		"\u0000\u0000\u0000\u00ba\u00bb\u0001\u0000\u0000\u0000\u00bb\u00bc\u0007"+
		"\u0001\u0000\u0000\u00bc\u00bd\u0003\u001a\r\u0000\u00bd\u001f\u0001\u0000"+
		"\u0000\u0000\u00be\u00bf\u0007\u0002\u0000\u0000\u00bf\u00c0\u0003\u001a"+
		"\r\u0000\u00c0!\u0001\u0000\u0000\u0000\u00c1\u00c6\u0003$\u0012\u0000"+
		"\u00c2\u00c6\u0003,\u0016\u0000\u00c3\u00c6\u0003.\u0017\u0000\u00c4\u00c6"+
		"\u00030\u0018\u0000\u00c5\u00c1\u0001\u0000\u0000\u0000\u00c5\u00c2\u0001"+
		"\u0000\u0000\u0000\u00c5\u00c3\u0001\u0000\u0000\u0000\u00c5\u00c4\u0001"+
		"\u0000\u0000\u0000\u00c6#\u0001\u0000\u0000\u0000\u00c7\u00cb\u0003(\u0014"+
		"\u0000\u00c8\u00cb\u0003&\u0013\u0000\u00c9\u00cb\u0003*\u0015\u0000\u00ca"+
		"\u00c7\u0001\u0000\u0000\u0000\u00ca\u00c8\u0001\u0000\u0000\u0000\u00ca"+
		"\u00c9\u0001\u0000\u0000\u0000\u00cb%\u0001\u0000\u0000\u0000\u00cc\u00cd"+
		"\u0005-\u0000\u0000\u00cd\'\u0001\u0000\u0000\u0000\u00ce\u00cf\u0005"+
		",\u0000\u0000\u00cf)\u0001\u0000\u0000\u0000\u00d0\u00d1\u0007\u0003\u0000"+
		"\u0000\u00d1+\u0001\u0000\u0000\u0000\u00d2\u00d3\u0005.\u0000\u0000\u00d3"+
		"-\u0001\u0000\u0000\u0000\u00d4\u00d5\u0005.\u0000\u0000\u00d5\u00d6\u0005"+
		"\u0003\u0000\u0000\u00d6\u00d7\u0003\n\u0005\u0000\u00d7\u00d8\u0005\u0004"+
		"\u0000\u0000\u00d8/\u0001\u0000\u0000\u0000\u00d9\u00e2\u0005%\u0000\u0000"+
		"\u00da\u00df\u0003\u001a\r\u0000\u00db\u00dc\u0005\u0006\u0000\u0000\u00dc"+
		"\u00de\u0003\u001a\r\u0000\u00dd\u00db\u0001\u0000\u0000\u0000\u00de\u00e1"+
		"\u0001\u0000\u0000\u0000\u00df\u00dd\u0001\u0000\u0000\u0000\u00df\u00e0"+
		"\u0001\u0000\u0000\u0000\u00e0\u00e3\u0001\u0000\u0000\u0000\u00e1\u00df"+
		"\u0001\u0000\u0000\u0000\u00e2\u00da\u0001\u0000\u0000\u0000\u00e2\u00e3"+
		"\u0001\u0000\u0000\u0000\u00e3\u00e4\u0001\u0000\u0000\u0000\u00e4\u00e5"+
		"\u0005&\u0000\u0000\u00e51\u0001\u0000\u0000\u0000\u00e6\u00e9\u00036"+
		"\u001b\u0000\u00e7\u00e9\u00034\u001a\u0000\u00e8\u00e6\u0001\u0000\u0000"+
		"\u0000\u00e8\u00e7\u0001\u0000\u0000\u0000\u00e93\u0001\u0000\u0000\u0000"+
		"\u00ea\u00eb\u0005%\u0000\u0000\u00eb\u00ec\u00032\u0019\u0000\u00ec\u00ed"+
		"\u0005\u0001\u0000\u0000\u00ed\u00ee\u0003(\u0014\u0000\u00ee\u00ef\u0005"+
		"&\u0000\u0000\u00ef5\u0001\u0000\u0000\u0000\u00f0\u00f1\u0007\u0004\u0000"+
		"\u0000\u00f17\u0001\u0000\u0000\u0000\u0012;AOXadorx\u009b\u00a2\u00ae"+
		"\u00b9\u00c5\u00ca\u00df\u00e2\u00e8";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}