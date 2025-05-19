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
		RULE_identifier = 22, RULE_arrayElement = 23, RULE_functionCall = 24, 
		RULE_arrayLiteral = 25, RULE_type = 26, RULE_arrayType = 27, RULE_primitiveType = 28;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "statement", "funcDeclaration", "paramList", "param", "argumentList", 
			"scope", "variableDeclaration", "forLoop", "whileLoop", "ifCondition", 
			"breakStatement", "returnStatement", "expression", "assigmentExpression", 
			"binaryExpression", "unaryExpression", "primaryExpression", "literal", 
			"stringLiteral", "numberLiteral", "booleanLiteral", "identifier", "arrayElement", 
			"functionCall", "arrayLiteral", "type", "arrayType", "primitiveType"
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
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__9) {
				{
				{
				setState(58);
				variableDeclaration();
				}
				}
				setState(63);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(65); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(64);
				funcDeclaration();
				}
				}
				setState(67); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__1 );
			setState(69);
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
			setState(81);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__9:
				enterOuterAlt(_localctx, 1);
				{
				setState(71);
				variableDeclaration();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 2);
				{
				setState(72);
				forLoop();
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 3);
				{
				setState(73);
				funcDeclaration();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 4);
				{
				setState(74);
				whileLoop();
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 5);
				{
				setState(75);
				ifCondition();
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 6);
				{
				setState(76);
				breakStatement();
				}
				break;
			case T__16:
				enterOuterAlt(_localctx, 7);
				{
				setState(77);
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
				setState(78);
				expression();
				setState(79);
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
			setState(83);
			match(T__1);
			setState(84);
			match(IDENTIFIER);
			setState(85);
			match(T__2);
			setState(86);
			paramList();
			setState(87);
			match(T__3);
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(88);
				match(T__4);
				setState(89);
				type();
				}
			}

			setState(92);
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
			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(94);
				param();
				setState(99);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(95);
					match(T__5);
					setState(96);
					param();
					}
					}
					setState(101);
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
			setState(104);
			match(IDENTIFIER);
			setState(105);
			match(T__6);
			setState(106);
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
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 123405147832328L) != 0)) {
				{
				setState(108);
				expression();
				setState(113);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(109);
					match(T__5);
					setState(110);
					expression();
					}
					}
					setState(115);
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
			setState(118);
			match(T__7);
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 123405148058636L) != 0)) {
				{
				{
				setState(119);
				statement();
				}
				}
				setState(124);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(125);
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
			setState(127);
			match(T__9);
			setState(128);
			match(IDENTIFIER);
			setState(129);
			match(T__6);
			setState(130);
			type();
			setState(131);
			match(T__10);
			setState(132);
			expression();
			setState(133);
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
			setState(135);
			match(T__11);
			setState(136);
			match(T__2);
			setState(137);
			variableDeclaration();
			setState(138);
			expression();
			setState(139);
			match(T__0);
			setState(140);
			expression();
			setState(141);
			match(T__3);
			setState(142);
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
			setState(144);
			match(T__12);
			setState(145);
			match(T__2);
			setState(146);
			expression();
			setState(147);
			match(T__3);
			setState(148);
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
			setState(150);
			match(T__13);
			setState(151);
			match(T__2);
			setState(152);
			expression();
			setState(153);
			match(T__3);
			setState(154);
			scope();
			setState(157);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(155);
				match(T__14);
				setState(156);
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
			setState(159);
			match(T__15);
			setState(160);
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
			setState(162);
			match(T__16);
			setState(164);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 123405147832328L) != 0)) {
				{
				setState(163);
				expression();
				}
			}

			setState(166);
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
			setState(176);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(168);
				assigmentExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(169);
				binaryExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(170);
				unaryExpression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(171);
				primaryExpression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(172);
				match(T__2);
				setState(173);
				expression();
				setState(174);
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
			setState(178);
			match(IDENTIFIER);
			setState(179);
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
			setState(180);
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
			setState(187);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__34:
			case T__35:
			case T__36:
			case NUMBER:
			case STRING:
			case IDENTIFIER:
				{
				setState(182);
				primaryExpression();
				}
				break;
			case T__2:
				{
				setState(183);
				match(T__2);
				setState(184);
				expression();
				setState(185);
				match(T__3);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(189);
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
			setState(190);
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
			setState(192);
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
			setState(193);
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
		public ArrayElementContext arrayElement() {
			return getRuleContext(ArrayElementContext.class,0);
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
			setState(200);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(195);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(196);
				identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(197);
				arrayElement(0);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(198);
				functionCall();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(199);
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
			setState(205);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(202);
				numberLiteral();
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(203);
				stringLiteral();
				}
				break;
			case T__34:
			case T__35:
				enterOuterAlt(_localctx, 3);
				{
				setState(204);
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
			setState(207);
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
			setState(209);
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
			setState(211);
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
			setState(213);
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
	public static class ArrayElementContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ArrayElementContext arrayElement() {
			return getRuleContext(ArrayElementContext.class,0);
		}
		public NumberLiteralContext numberLiteral() {
			return getRuleContext(NumberLiteralContext.class,0);
		}
		public ArrayElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).enterArrayElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnailListener ) ((SnailListener)listener).exitArrayElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnailVisitor ) return ((SnailVisitor<? extends T>)visitor).visitArrayElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayElementContext arrayElement() throws RecognitionException {
		return arrayElement(0);
	}

	private ArrayElementContext arrayElement(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ArrayElementContext _localctx = new ArrayElementContext(_ctx, _parentState);
		ArrayElementContext _prevctx = _localctx;
		int _startState = 46;
		enterRecursionRule(_localctx, 46, RULE_arrayElement, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(216);
			identifier();
			}
			_ctx.stop = _input.LT(-1);
			setState(225);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ArrayElementContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_arrayElement);
					setState(218);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(219);
					match(T__36);
					setState(220);
					numberLiteral();
					setState(221);
					match(T__37);
					}
					} 
				}
				setState(227);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
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
		enterRule(_localctx, 48, RULE_functionCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(228);
			match(IDENTIFIER);
			setState(229);
			match(T__2);
			setState(230);
			argumentList();
			setState(231);
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
		enterRule(_localctx, 50, RULE_arrayLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(233);
			match(T__36);
			setState(242);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 123405147832328L) != 0)) {
				{
				setState(234);
				expression();
				setState(239);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(235);
					match(T__5);
					setState(236);
					expression();
					}
					}
					setState(241);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(244);
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
		enterRule(_localctx, 52, RULE_type);
		try {
			setState(248);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__38:
			case T__39:
			case T__40:
			case T__41:
			case T__42:
				enterOuterAlt(_localctx, 1);
				{
				setState(246);
				primitiveType();
				}
				break;
			case T__36:
				enterOuterAlt(_localctx, 2);
				{
				setState(247);
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
		enterRule(_localctx, 54, RULE_arrayType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(250);
			match(T__36);
			setState(251);
			type();
			setState(252);
			match(T__0);
			setState(253);
			numberLiteral();
			setState(254);
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
		enterRule(_localctx, 56, RULE_primitiveType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(256);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 23:
			return arrayElement_sempred((ArrayElementContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean arrayElement_sempred(ArrayElementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u00011\u0103\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0001\u0000\u0005\u0000<\b\u0000\n\u0000\f\u0000"+
		"?\t\u0000\u0001\u0000\u0004\u0000B\b\u0000\u000b\u0000\f\u0000C\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003"+
		"\u0001R\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0003\u0002[\b\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003b\b\u0003\n\u0003"+
		"\f\u0003e\t\u0003\u0003\u0003g\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005p\b\u0005"+
		"\n\u0005\f\u0005s\t\u0005\u0003\u0005u\b\u0005\u0001\u0006\u0001\u0006"+
		"\u0005\u0006y\b\u0006\n\u0006\f\u0006|\t\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u009e"+
		"\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0003\f\u00a5"+
		"\b\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0003\r\u00b1\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003"+
		"\u000f\u00bc\b\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0003\u0011\u00c9\b\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0003"+
		"\u0012\u00ce\b\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001"+
		"\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0005"+
		"\u0017\u00e0\b\u0017\n\u0017\f\u0017\u00e3\t\u0017\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0005\u0019\u00ee\b\u0019\n\u0019\f\u0019\u00f1\t\u0019\u0003"+
		"\u0019\u00f3\b\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0003"+
		"\u001a\u00f9\b\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0000\u0001.\u001d"+
		"\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a"+
		"\u001c\u001e \"$&(*,.02468\u0000\u0005\u0002\u0000\u000b\u000b\u0012\u0015"+
		"\u0001\u0000\u0016!\u0002\u0000\u001f\u001f\"\"\u0001\u0000#$\u0001\u0000"+
		"\'+\u0105\u0000=\u0001\u0000\u0000\u0000\u0002Q\u0001\u0000\u0000\u0000"+
		"\u0004S\u0001\u0000\u0000\u0000\u0006f\u0001\u0000\u0000\u0000\bh\u0001"+
		"\u0000\u0000\u0000\nt\u0001\u0000\u0000\u0000\fv\u0001\u0000\u0000\u0000"+
		"\u000e\u007f\u0001\u0000\u0000\u0000\u0010\u0087\u0001\u0000\u0000\u0000"+
		"\u0012\u0090\u0001\u0000\u0000\u0000\u0014\u0096\u0001\u0000\u0000\u0000"+
		"\u0016\u009f\u0001\u0000\u0000\u0000\u0018\u00a2\u0001\u0000\u0000\u0000"+
		"\u001a\u00b0\u0001\u0000\u0000\u0000\u001c\u00b2\u0001\u0000\u0000\u0000"+
		"\u001e\u00bb\u0001\u0000\u0000\u0000 \u00c0\u0001\u0000\u0000\u0000\""+
		"\u00c8\u0001\u0000\u0000\u0000$\u00cd\u0001\u0000\u0000\u0000&\u00cf\u0001"+
		"\u0000\u0000\u0000(\u00d1\u0001\u0000\u0000\u0000*\u00d3\u0001\u0000\u0000"+
		"\u0000,\u00d5\u0001\u0000\u0000\u0000.\u00d7\u0001\u0000\u0000\u00000"+
		"\u00e4\u0001\u0000\u0000\u00002\u00e9\u0001\u0000\u0000\u00004\u00f8\u0001"+
		"\u0000\u0000\u00006\u00fa\u0001\u0000\u0000\u00008\u0100\u0001\u0000\u0000"+
		"\u0000:<\u0003\u000e\u0007\u0000;:\u0001\u0000\u0000\u0000<?\u0001\u0000"+
		"\u0000\u0000=;\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000>A\u0001"+
		"\u0000\u0000\u0000?=\u0001\u0000\u0000\u0000@B\u0003\u0004\u0002\u0000"+
		"A@\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000\u0000CA\u0001\u0000\u0000"+
		"\u0000CD\u0001\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000EF\u0005\u0000"+
		"\u0000\u0001F\u0001\u0001\u0000\u0000\u0000GR\u0003\u000e\u0007\u0000"+
		"HR\u0003\u0010\b\u0000IR\u0003\u0004\u0002\u0000JR\u0003\u0012\t\u0000"+
		"KR\u0003\u0014\n\u0000LR\u0003\u0016\u000b\u0000MR\u0003\u0018\f\u0000"+
		"NO\u0003\u001a\r\u0000OP\u0005\u0001\u0000\u0000PR\u0001\u0000\u0000\u0000"+
		"QG\u0001\u0000\u0000\u0000QH\u0001\u0000\u0000\u0000QI\u0001\u0000\u0000"+
		"\u0000QJ\u0001\u0000\u0000\u0000QK\u0001\u0000\u0000\u0000QL\u0001\u0000"+
		"\u0000\u0000QM\u0001\u0000\u0000\u0000QN\u0001\u0000\u0000\u0000R\u0003"+
		"\u0001\u0000\u0000\u0000ST\u0005\u0002\u0000\u0000TU\u0005.\u0000\u0000"+
		"UV\u0005\u0003\u0000\u0000VW\u0003\u0006\u0003\u0000WZ\u0005\u0004\u0000"+
		"\u0000XY\u0005\u0005\u0000\u0000Y[\u00034\u001a\u0000ZX\u0001\u0000\u0000"+
		"\u0000Z[\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000\\]\u0003\f"+
		"\u0006\u0000]\u0005\u0001\u0000\u0000\u0000^c\u0003\b\u0004\u0000_`\u0005"+
		"\u0006\u0000\u0000`b\u0003\b\u0004\u0000a_\u0001\u0000\u0000\u0000be\u0001"+
		"\u0000\u0000\u0000ca\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000"+
		"dg\u0001\u0000\u0000\u0000ec\u0001\u0000\u0000\u0000f^\u0001\u0000\u0000"+
		"\u0000fg\u0001\u0000\u0000\u0000g\u0007\u0001\u0000\u0000\u0000hi\u0005"+
		".\u0000\u0000ij\u0005\u0007\u0000\u0000jk\u00034\u001a\u0000k\t\u0001"+
		"\u0000\u0000\u0000lq\u0003\u001a\r\u0000mn\u0005\u0006\u0000\u0000np\u0003"+
		"\u001a\r\u0000om\u0001\u0000\u0000\u0000ps\u0001\u0000\u0000\u0000qo\u0001"+
		"\u0000\u0000\u0000qr\u0001\u0000\u0000\u0000ru\u0001\u0000\u0000\u0000"+
		"sq\u0001\u0000\u0000\u0000tl\u0001\u0000\u0000\u0000tu\u0001\u0000\u0000"+
		"\u0000u\u000b\u0001\u0000\u0000\u0000vz\u0005\b\u0000\u0000wy\u0003\u0002"+
		"\u0001\u0000xw\u0001\u0000\u0000\u0000y|\u0001\u0000\u0000\u0000zx\u0001"+
		"\u0000\u0000\u0000z{\u0001\u0000\u0000\u0000{}\u0001\u0000\u0000\u0000"+
		"|z\u0001\u0000\u0000\u0000}~\u0005\t\u0000\u0000~\r\u0001\u0000\u0000"+
		"\u0000\u007f\u0080\u0005\n\u0000\u0000\u0080\u0081\u0005.\u0000\u0000"+
		"\u0081\u0082\u0005\u0007\u0000\u0000\u0082\u0083\u00034\u001a\u0000\u0083"+
		"\u0084\u0005\u000b\u0000\u0000\u0084\u0085\u0003\u001a\r\u0000\u0085\u0086"+
		"\u0005\u0001\u0000\u0000\u0086\u000f\u0001\u0000\u0000\u0000\u0087\u0088"+
		"\u0005\f\u0000\u0000\u0088\u0089\u0005\u0003\u0000\u0000\u0089\u008a\u0003"+
		"\u000e\u0007\u0000\u008a\u008b\u0003\u001a\r\u0000\u008b\u008c\u0005\u0001"+
		"\u0000\u0000\u008c\u008d\u0003\u001a\r\u0000\u008d\u008e\u0005\u0004\u0000"+
		"\u0000\u008e\u008f\u0003\f\u0006\u0000\u008f\u0011\u0001\u0000\u0000\u0000"+
		"\u0090\u0091\u0005\r\u0000\u0000\u0091\u0092\u0005\u0003\u0000\u0000\u0092"+
		"\u0093\u0003\u001a\r\u0000\u0093\u0094\u0005\u0004\u0000\u0000\u0094\u0095"+
		"\u0003\f\u0006\u0000\u0095\u0013\u0001\u0000\u0000\u0000\u0096\u0097\u0005"+
		"\u000e\u0000\u0000\u0097\u0098\u0005\u0003\u0000\u0000\u0098\u0099\u0003"+
		"\u001a\r\u0000\u0099\u009a\u0005\u0004\u0000\u0000\u009a\u009d\u0003\f"+
		"\u0006\u0000\u009b\u009c\u0005\u000f\u0000\u0000\u009c\u009e\u0003\f\u0006"+
		"\u0000\u009d\u009b\u0001\u0000\u0000\u0000\u009d\u009e\u0001\u0000\u0000"+
		"\u0000\u009e\u0015\u0001\u0000\u0000\u0000\u009f\u00a0\u0005\u0010\u0000"+
		"\u0000\u00a0\u00a1\u0005\u0001\u0000\u0000\u00a1\u0017\u0001\u0000\u0000"+
		"\u0000\u00a2\u00a4\u0005\u0011\u0000\u0000\u00a3\u00a5\u0003\u001a\r\u0000"+
		"\u00a4\u00a3\u0001\u0000\u0000\u0000\u00a4\u00a5\u0001\u0000\u0000\u0000"+
		"\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u00a7\u0005\u0001\u0000\u0000"+
		"\u00a7\u0019\u0001\u0000\u0000\u0000\u00a8\u00b1\u0003\u001c\u000e\u0000"+
		"\u00a9\u00b1\u0003\u001e\u000f\u0000\u00aa\u00b1\u0003 \u0010\u0000\u00ab"+
		"\u00b1\u0003\"\u0011\u0000\u00ac\u00ad\u0005\u0003\u0000\u0000\u00ad\u00ae"+
		"\u0003\u001a\r\u0000\u00ae\u00af\u0005\u0004\u0000\u0000\u00af\u00b1\u0001"+
		"\u0000\u0000\u0000\u00b0\u00a8\u0001\u0000\u0000\u0000\u00b0\u00a9\u0001"+
		"\u0000\u0000\u0000\u00b0\u00aa\u0001\u0000\u0000\u0000\u00b0\u00ab\u0001"+
		"\u0000\u0000\u0000\u00b0\u00ac\u0001\u0000\u0000\u0000\u00b1\u001b\u0001"+
		"\u0000\u0000\u0000\u00b2\u00b3\u0005.\u0000\u0000\u00b3\u00b4\u0007\u0000"+
		"\u0000\u0000\u00b4\u00b5\u0003\u001a\r\u0000\u00b5\u001d\u0001\u0000\u0000"+
		"\u0000\u00b6\u00bc\u0003\"\u0011\u0000\u00b7\u00b8\u0005\u0003\u0000\u0000"+
		"\u00b8\u00b9\u0003\u001a\r\u0000\u00b9\u00ba\u0005\u0004\u0000\u0000\u00ba"+
		"\u00bc\u0001\u0000\u0000\u0000\u00bb\u00b6\u0001\u0000\u0000\u0000\u00bb"+
		"\u00b7\u0001\u0000\u0000\u0000\u00bc\u00bd\u0001\u0000\u0000\u0000\u00bd"+
		"\u00be\u0007\u0001\u0000\u0000\u00be\u00bf\u0003\u001a\r\u0000\u00bf\u001f"+
		"\u0001\u0000\u0000\u0000\u00c0\u00c1\u0007\u0002\u0000\u0000\u00c1\u00c2"+
		"\u0003\u001a\r\u0000\u00c2!\u0001\u0000\u0000\u0000\u00c3\u00c9\u0003"+
		"$\u0012\u0000\u00c4\u00c9\u0003,\u0016\u0000\u00c5\u00c9\u0003.\u0017"+
		"\u0000\u00c6\u00c9\u00030\u0018\u0000\u00c7\u00c9\u00032\u0019\u0000\u00c8"+
		"\u00c3\u0001\u0000\u0000\u0000\u00c8\u00c4\u0001\u0000\u0000\u0000\u00c8"+
		"\u00c5\u0001\u0000\u0000\u0000\u00c8\u00c6\u0001\u0000\u0000\u0000\u00c8"+
		"\u00c7\u0001\u0000\u0000\u0000\u00c9#\u0001\u0000\u0000\u0000\u00ca\u00ce"+
		"\u0003(\u0014\u0000\u00cb\u00ce\u0003&\u0013\u0000\u00cc\u00ce\u0003*"+
		"\u0015\u0000\u00cd\u00ca\u0001\u0000\u0000\u0000\u00cd\u00cb\u0001\u0000"+
		"\u0000\u0000\u00cd\u00cc\u0001\u0000\u0000\u0000\u00ce%\u0001\u0000\u0000"+
		"\u0000\u00cf\u00d0\u0005-\u0000\u0000\u00d0\'\u0001\u0000\u0000\u0000"+
		"\u00d1\u00d2\u0005,\u0000\u0000\u00d2)\u0001\u0000\u0000\u0000\u00d3\u00d4"+
		"\u0007\u0003\u0000\u0000\u00d4+\u0001\u0000\u0000\u0000\u00d5\u00d6\u0005"+
		".\u0000\u0000\u00d6-\u0001\u0000\u0000\u0000\u00d7\u00d8\u0006\u0017\uffff"+
		"\uffff\u0000\u00d8\u00d9\u0003,\u0016\u0000\u00d9\u00e1\u0001\u0000\u0000"+
		"\u0000\u00da\u00db\n\u0001\u0000\u0000\u00db\u00dc\u0005%\u0000\u0000"+
		"\u00dc\u00dd\u0003(\u0014\u0000\u00dd\u00de\u0005&\u0000\u0000\u00de\u00e0"+
		"\u0001\u0000\u0000\u0000\u00df\u00da\u0001\u0000\u0000\u0000\u00e0\u00e3"+
		"\u0001\u0000\u0000\u0000\u00e1\u00df\u0001\u0000\u0000\u0000\u00e1\u00e2"+
		"\u0001\u0000\u0000\u0000\u00e2/\u0001\u0000\u0000\u0000\u00e3\u00e1\u0001"+
		"\u0000\u0000\u0000\u00e4\u00e5\u0005.\u0000\u0000\u00e5\u00e6\u0005\u0003"+
		"\u0000\u0000\u00e6\u00e7\u0003\n\u0005\u0000\u00e7\u00e8\u0005\u0004\u0000"+
		"\u0000\u00e81\u0001\u0000\u0000\u0000\u00e9\u00f2\u0005%\u0000\u0000\u00ea"+
		"\u00ef\u0003\u001a\r\u0000\u00eb\u00ec\u0005\u0006\u0000\u0000\u00ec\u00ee"+
		"\u0003\u001a\r\u0000\u00ed\u00eb\u0001\u0000\u0000\u0000\u00ee\u00f1\u0001"+
		"\u0000\u0000\u0000\u00ef\u00ed\u0001\u0000\u0000\u0000\u00ef\u00f0\u0001"+
		"\u0000\u0000\u0000\u00f0\u00f3\u0001\u0000\u0000\u0000\u00f1\u00ef\u0001"+
		"\u0000\u0000\u0000\u00f2\u00ea\u0001\u0000\u0000\u0000\u00f2\u00f3\u0001"+
		"\u0000\u0000\u0000\u00f3\u00f4\u0001\u0000\u0000\u0000\u00f4\u00f5\u0005"+
		"&\u0000\u0000\u00f53\u0001\u0000\u0000\u0000\u00f6\u00f9\u00038\u001c"+
		"\u0000\u00f7\u00f9\u00036\u001b\u0000\u00f8\u00f6\u0001\u0000\u0000\u0000"+
		"\u00f8\u00f7\u0001\u0000\u0000\u0000\u00f95\u0001\u0000\u0000\u0000\u00fa"+
		"\u00fb\u0005%\u0000\u0000\u00fb\u00fc\u00034\u001a\u0000\u00fc\u00fd\u0005"+
		"\u0001\u0000\u0000\u00fd\u00fe\u0003(\u0014\u0000\u00fe\u00ff\u0005&\u0000"+
		"\u0000\u00ff7\u0001\u0000\u0000\u0000\u0100\u0101\u0007\u0004\u0000\u0000"+
		"\u01019\u0001\u0000\u0000\u0000\u0013=CQZcfqtz\u009d\u00a4\u00b0\u00bb"+
		"\u00c8\u00cd\u00e1\u00ef\u00f2\u00f8";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}