// Generated from Snail.g4 by ANTLR 4.13.2

package io.github.snaill.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SnailParser extends Parser {
    public static final int
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, T__7 = 8, T__8 = 9,
            T__9 = 10, T__10 = 11, T__11 = 12, T__12 = 13, T__13 = 14, T__14 = 15, T__15 = 16, T__16 = 17,
            T__17 = 18, T__18 = 19, T__19 = 20, T__20 = 21, T__21 = 22, T__22 = 23, T__23 = 24,
            T__24 = 25, T__25 = 26, T__26 = 27, T__27 = 28, T__28 = 29, T__29 = 30, T__30 = 31,
            T__31 = 32, T__32 = 33, T__33 = 34, T__34 = 35, T__35 = 36, T__36 = 37, NUMBER = 38,
            STRING = 39, PRIMITIVE_TYPE = 40, IDENTIFIER = 41, WS = 42, LINE_COMMENT = 43, BLOCK_COMMENT = 44;
    public static final int
            RULE_program = 0, RULE_statement = 1, RULE_funcDeclaration = 2, RULE_paramList = 3,
            RULE_param = 4, RULE_argumentList = 5, RULE_scope = 6, RULE_variableDeclaration = 7,
            RULE_forLoop = 8, RULE_whileLoop = 9, RULE_ifCondition = 10, RULE_breakStatement = 11,
            RULE_returnStatement = 12, RULE_expression = 13, RULE_binaryExpression = 14,
            RULE_unaryExpression = 15, RULE_assignmentOperator = 16, RULE_primaryExpression = 17,
            RULE_literal = 18, RULE_identifier = 19, RULE_functionCall = 20, RULE_arrayLiteral = 21,
            RULE_type = 22;
    public static final String[] ruleNames = makeRuleNames();
    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN =
            "\u0004\u0001,\u00de\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002" +
                    "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f" +
                    "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012" +
                    "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015" +
                    "\u0002\u0016\u0007\u0016\u0001\u0000\u0005\u00000\b\u0000\n\u0000\f\u0000" +
                    "3\t\u0000\u0001\u0000\u0004\u00006\b\u0000\u000b\u0000\f\u00007\u0001" +
                    "\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003" +
                    "\u0001F\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0003\u0002O\b\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003V\b\u0003\n\u0003" +
                    "\f\u0003Y\t\u0003\u0003\u0003[\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005d\b\u0005" +
                    "\n\u0005\f\u0005g\t\u0005\u0003\u0005i\b\u0005\u0001\u0006\u0001\u0006" +
                    "\u0005\u0006m\b\u0006\n\u0006\f\u0006p\t\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\f\u0001\f\u0003\f\u0096\b\f\u0001\f\u0001\f\u0001\r" +
                    "\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0003\r\u00a5\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001" +
                    "\u000e\u0001\u000e\u0003\u000e\u00ac\b\u000e\u0001\u000e\u0001\u000e\u0001" +
                    "\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001" +
                    "\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u00bb" +
                    "\b\u0011\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001" +
                    "\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001" +
                    "\u0015\u0001\u0015\u0005\u0015\u00ca\b\u0015\n\u0015\f\u0015\u00cd\t\u0015" +
                    "\u0003\u0015\u00cf\b\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0003\u0016\u00dc\b\u0016\u0001\u0016\u0000\u0000\u0017\u0000" +
                    "\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c" +
                    "\u001e \"$&(*,\u0000\u0004\u0001\u0000\u0011\u001c\u0002\u0000\u001a\u001a" +
                    "\u001d\u001d\u0002\u0000\u000b\u000b\u001e!\u0001\u0000&\'\u00e4\u0000" +
                    "1\u0001\u0000\u0000\u0000\u0002E\u0001\u0000\u0000\u0000\u0004G\u0001" +
                    "\u0000\u0000\u0000\u0006Z\u0001\u0000\u0000\u0000\b\\\u0001\u0000\u0000" +
                    "\u0000\nh\u0001\u0000\u0000\u0000\fj\u0001\u0000\u0000\u0000\u000es\u0001" +
                    "\u0000\u0000\u0000\u0010{\u0001\u0000\u0000\u0000\u0012\u0084\u0001\u0000" +
                    "\u0000\u0000\u0014\u008a\u0001\u0000\u0000\u0000\u0016\u0090\u0001\u0000" +
                    "\u0000\u0000\u0018\u0093\u0001\u0000\u0000\u0000\u001a\u00a4\u0001\u0000" +
                    "\u0000\u0000\u001c\u00ab\u0001\u0000\u0000\u0000\u001e\u00b0\u0001\u0000" +
                    "\u0000\u0000 \u00b3\u0001\u0000\u0000\u0000\"\u00ba\u0001\u0000\u0000" +
                    "\u0000$\u00bc\u0001\u0000\u0000\u0000&\u00be\u0001\u0000\u0000\u0000(" +
                    "\u00c0\u0001\u0000\u0000\u0000*\u00c5\u0001\u0000\u0000\u0000,\u00db\u0001" +
                    "\u0000\u0000\u0000.0\u0003\u000e\u0007\u0000/.\u0001\u0000\u0000\u0000" +
                    "03\u0001\u0000\u0000\u00001/\u0001\u0000\u0000\u000012\u0001\u0000\u0000" +
                    "\u000025\u0001\u0000\u0000\u000031\u0001\u0000\u0000\u000046\u0003\u0004" +
                    "\u0002\u000054\u0001\u0000\u0000\u000067\u0001\u0000\u0000\u000075\u0001" +
                    "\u0000\u0000\u000078\u0001\u0000\u0000\u000089\u0001\u0000\u0000\u0000" +
                    "9:\u0005\u0000\u0000\u0001:\u0001\u0001\u0000\u0000\u0000;F\u0003\u000e" +
                    "\u0007\u0000<F\u0003\u0010\b\u0000=F\u0003\u0004\u0002\u0000>F\u0003\u0012" +
                    "\t\u0000?F\u0003\u0014\n\u0000@F\u0003\u0016\u000b\u0000AF\u0003\u0018" +
                    "\f\u0000BC\u0003\u001a\r\u0000CD\u0005\u0001\u0000\u0000DF\u0001\u0000" +
                    "\u0000\u0000E;\u0001\u0000\u0000\u0000E<\u0001\u0000\u0000\u0000E=\u0001" +
                    "\u0000\u0000\u0000E>\u0001\u0000\u0000\u0000E?\u0001\u0000\u0000\u0000" +
                    "E@\u0001\u0000\u0000\u0000EA\u0001\u0000\u0000\u0000EB\u0001\u0000\u0000" +
                    "\u0000F\u0003\u0001\u0000\u0000\u0000GH\u0005\u0002\u0000\u0000HI\u0005" +
                    ")\u0000\u0000IJ\u0005\u0003\u0000\u0000JK\u0003\u0006\u0003\u0000KN\u0005" +
                    "\u0004\u0000\u0000LM\u0005\u0005\u0000\u0000MO\u0003,\u0016\u0000NL\u0001" +
                    "\u0000\u0000\u0000NO\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000" +
                    "PQ\u0003\f\u0006\u0000Q\u0005\u0001\u0000\u0000\u0000RW\u0003\b\u0004" +
                    "\u0000ST\u0005\u0006\u0000\u0000TV\u0003\b\u0004\u0000US\u0001\u0000\u0000" +
                    "\u0000VY\u0001\u0000\u0000\u0000WU\u0001\u0000\u0000\u0000WX\u0001\u0000" +
                    "\u0000\u0000X[\u0001\u0000\u0000\u0000YW\u0001\u0000\u0000\u0000ZR\u0001" +
                    "\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[\u0007\u0001\u0000\u0000" +
                    "\u0000\\]\u0005)\u0000\u0000]^\u0005\u0007\u0000\u0000^_\u0003,\u0016" +
                    "\u0000_\t\u0001\u0000\u0000\u0000`e\u0003\u001a\r\u0000ab\u0005\u0006" +
                    "\u0000\u0000bd\u0003\u001a\r\u0000ca\u0001\u0000\u0000\u0000dg\u0001\u0000" +
                    "\u0000\u0000ec\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000fi\u0001" +
                    "\u0000\u0000\u0000ge\u0001\u0000\u0000\u0000h`\u0001\u0000\u0000\u0000" +
                    "hi\u0001\u0000\u0000\u0000i\u000b\u0001\u0000\u0000\u0000jn\u0005\b\u0000" +
                    "\u0000km\u0003\u0002\u0001\u0000lk\u0001\u0000\u0000\u0000mp\u0001\u0000" +
                    "\u0000\u0000nl\u0001\u0000\u0000\u0000no\u0001\u0000\u0000\u0000oq\u0001" +
                    "\u0000\u0000\u0000pn\u0001\u0000\u0000\u0000qr\u0005\t\u0000\u0000r\r" +
                    "\u0001\u0000\u0000\u0000st\u0005\n\u0000\u0000tu\u0005)\u0000\u0000uv" +
                    "\u0005\u0007\u0000\u0000vw\u0003,\u0016\u0000wx\u0005\u000b\u0000\u0000" +
                    "xy\u0003\u001a\r\u0000yz\u0005\u0001\u0000\u0000z\u000f\u0001\u0000\u0000" +
                    "\u0000{|\u0005\f\u0000\u0000|}\u0005\u0003\u0000\u0000}~\u0003\u000e\u0007" +
                    "\u0000~\u007f\u0003\u001a\r\u0000\u007f\u0080\u0005\u0001\u0000\u0000" +
                    "\u0080\u0081\u0003\u001a\r\u0000\u0081\u0082\u0005\u0004\u0000\u0000\u0082" +
                    "\u0083\u0003\f\u0006\u0000\u0083\u0011\u0001\u0000\u0000\u0000\u0084\u0085" +
                    "\u0005\r\u0000\u0000\u0085\u0086\u0005\u0003\u0000\u0000\u0086\u0087\u0003" +
                    "\u001a\r\u0000\u0087\u0088\u0005\u0004\u0000\u0000\u0088\u0089\u0003\f" +
                    "\u0006\u0000\u0089\u0013\u0001\u0000\u0000\u0000\u008a\u008b\u0005\u000e" +
                    "\u0000\u0000\u008b\u008c\u0005\u0003\u0000\u0000\u008c\u008d\u0003\u001a" +
                    "\r\u0000\u008d\u008e\u0005\u0004\u0000\u0000\u008e\u008f\u0003\f\u0006" +
                    "\u0000\u008f\u0015\u0001\u0000\u0000\u0000\u0090\u0091\u0005\u000f\u0000" +
                    "\u0000\u0091\u0092\u0005\u0001\u0000\u0000\u0092\u0017\u0001\u0000\u0000" +
                    "\u0000\u0093\u0095\u0005\u0010\u0000\u0000\u0094\u0096\u0003\u001a\r\u0000" +
                    "\u0095\u0094\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000\u0000" +
                    "\u0096\u0097\u0001\u0000\u0000\u0000\u0097\u0098\u0005\u0001\u0000\u0000" +
                    "\u0098\u0019\u0001\u0000\u0000\u0000\u0099\u009a\u0005)\u0000\u0000\u009a" +
                    "\u009b\u0003 \u0010\u0000\u009b\u009c\u0003\u001a\r\u0000\u009c\u00a5" +
                    "\u0001\u0000\u0000\u0000\u009d\u00a5\u0003\u001c\u000e\u0000\u009e\u00a5" +
                    "\u0003\u001e\u000f\u0000\u009f\u00a5\u0003\"\u0011\u0000\u00a0\u00a1\u0005" +
                    "\u0003\u0000\u0000\u00a1\u00a2\u0003\u001a\r\u0000\u00a2\u00a3\u0005\u0004" +
                    "\u0000\u0000\u00a3\u00a5\u0001\u0000\u0000\u0000\u00a4\u0099\u0001\u0000" +
                    "\u0000\u0000\u00a4\u009d\u0001\u0000\u0000\u0000\u00a4\u009e\u0001\u0000" +
                    "\u0000\u0000\u00a4\u009f\u0001\u0000\u0000\u0000\u00a4\u00a0\u0001\u0000" +
                    "\u0000\u0000\u00a5\u001b\u0001\u0000\u0000\u0000\u00a6\u00ac\u0003\"\u0011" +
                    "\u0000\u00a7\u00a8\u0005\u0003\u0000\u0000\u00a8\u00a9\u0003\u001a\r\u0000" +
                    "\u00a9\u00aa\u0005\u0004\u0000\u0000\u00aa\u00ac\u0001\u0000\u0000\u0000" +
                    "\u00ab\u00a6\u0001\u0000\u0000\u0000\u00ab\u00a7\u0001\u0000\u0000\u0000" +
                    "\u00ac\u00ad\u0001\u0000\u0000\u0000\u00ad\u00ae\u0007\u0000\u0000\u0000" +
                    "\u00ae\u00af\u0003\u001a\r\u0000\u00af\u001d\u0001\u0000\u0000\u0000\u00b0" +
                    "\u00b1\u0007\u0001\u0000\u0000\u00b1\u00b2\u0003\u001a\r\u0000\u00b2\u001f" +
                    "\u0001\u0000\u0000\u0000\u00b3\u00b4\u0007\u0002\u0000\u0000\u00b4!\u0001" +
                    "\u0000\u0000\u0000\u00b5\u00bb\u0003$\u0012\u0000\u00b6\u00bb\u0003\u001e" +
                    "\u000f\u0000\u00b7\u00bb\u0003&\u0013\u0000\u00b8\u00bb\u0003(\u0014\u0000" +
                    "\u00b9\u00bb\u0003*\u0015\u0000\u00ba\u00b5\u0001\u0000\u0000\u0000\u00ba" +
                    "\u00b6\u0001\u0000\u0000\u0000\u00ba\u00b7\u0001\u0000\u0000\u0000\u00ba" +
                    "\u00b8\u0001\u0000\u0000\u0000\u00ba\u00b9\u0001\u0000\u0000\u0000\u00bb" +
                    "#\u0001\u0000\u0000\u0000\u00bc\u00bd\u0007\u0003\u0000\u0000\u00bd%\u0001" +
                    "\u0000\u0000\u0000\u00be\u00bf\u0005)\u0000\u0000\u00bf\'\u0001\u0000" +
                    "\u0000\u0000\u00c0\u00c1\u0005)\u0000\u0000\u00c1\u00c2\u0005\u0003\u0000" +
                    "\u0000\u00c2\u00c3\u0003\n\u0005\u0000\u00c3\u00c4\u0005\u0004\u0000\u0000" +
                    "\u00c4)\u0001\u0000\u0000\u0000\u00c5\u00ce\u0005\"\u0000\u0000\u00c6" +
                    "\u00cb\u0003\u001a\r\u0000\u00c7\u00c8\u0005\u0006\u0000\u0000\u00c8\u00ca" +
                    "\u0003\u001a\r\u0000\u00c9\u00c7\u0001\u0000\u0000\u0000\u00ca\u00cd\u0001" +
                    "\u0000\u0000\u0000\u00cb\u00c9\u0001\u0000\u0000\u0000\u00cb\u00cc\u0001" +
                    "\u0000\u0000\u0000\u00cc\u00cf\u0001\u0000\u0000\u0000\u00cd\u00cb\u0001" +
                    "\u0000\u0000\u0000\u00ce\u00c6\u0001\u0000\u0000\u0000\u00ce\u00cf\u0001" +
                    "\u0000\u0000\u0000\u00cf\u00d0\u0001\u0000\u0000\u0000\u00d0\u00d1\u0005" +
                    "#\u0000\u0000\u00d1+\u0001\u0000\u0000\u0000\u00d2\u00dc\u0005(\u0000" +
                    "\u0000\u00d3\u00dc\u0005$\u0000\u0000\u00d4\u00dc\u0005%\u0000\u0000\u00d5" +
                    "\u00d6\u0005\"\u0000\u0000\u00d6\u00d7\u0003,\u0016\u0000\u00d7\u00d8" +
                    "\u0005\u0001\u0000\u0000\u00d8\u00d9\u0005&\u0000\u0000\u00d9\u00da\u0005" +
                    "#\u0000\u0000\u00da\u00dc\u0001\u0000\u0000\u0000\u00db\u00d2\u0001\u0000" +
                    "\u0000\u0000\u00db\u00d3\u0001\u0000\u0000\u0000\u00db\u00d4\u0001\u0000" +
                    "\u0000\u0000\u00db\u00d5\u0001\u0000\u0000\u0000\u00dc-\u0001\u0000\u0000" +
                    "\u0000\u001017ENWZehn\u0095\u00a4\u00ab\u00ba\u00cb\u00ce\u00db";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    private static final String[] _LITERAL_NAMES = makeLiteralNames();
    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

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

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }

    public SnailParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    private static String[] makeRuleNames() {
        return new String[]{
                "program", "statement", "funcDeclaration", "paramList", "param", "argumentList",
                "scope", "variableDeclaration", "forLoop", "whileLoop", "ifCondition",
                "breakStatement", "returnStatement", "expression", "binaryExpression",
                "unaryExpression", "assignmentOperator", "primaryExpression", "literal",
                "identifier", "functionCall", "arrayLiteral", "type"
        };
    }

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "';'", "'fn'", "'('", "')'", "'->'", "','", "':'", "'{'", "'}'",
                "'let'", "'='", "'for'", "'while'", "'if'", "'break'", "'return'", "'||'",
                "'&&'", "'=='", "'!='", "'>'", "'<'", "'>='", "'<='", "'+'", "'-'", "'*'",
                "'/'", "'!'", "'+='", "'-='", "'*='", "'/='", "'['", "']'", "'void'",
                "'string'"
        };
    }

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, "NUMBER", "STRING", "PRIMITIVE_TYPE", "IDENTIFIER", "WS",
                "LINE_COMMENT", "BLOCK_COMMENT"
        };
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
    public String getGrammarFileName() {
        return "Snail.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public final ProgramContext program() throws RecognitionException {
        ProgramContext _localctx = new ProgramContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_program);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(49);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == T__9) {
                    {
                        {
                            setState(46);
                            variableDeclaration();
                        }
                    }
                    setState(51);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(53);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(52);
                            funcDeclaration();
                        }
                    }
                    setState(55);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == T__1);
                setState(57);
                match(EOF);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final StatementContext statement() throws RecognitionException {
        StatementContext _localctx = new StatementContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_statement);
        try {
            setState(69);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case T__9:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(59);
                    variableDeclaration();
                }
                break;
                case T__11:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(60);
                    forLoop();
                }
                break;
                case T__1:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(61);
                    funcDeclaration();
                }
                break;
                case T__12:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(62);
                    whileLoop();
                }
                break;
                case T__13:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(63);
                    ifCondition();
                }
                break;
                case T__14:
                    enterOuterAlt(_localctx, 6);
                {
                    setState(64);
                    breakStatement();
                }
                break;
                case T__15:
                    enterOuterAlt(_localctx, 7);
                {
                    setState(65);
                    returnStatement();
                }
                break;
                case T__2:
                case T__25:
                case T__28:
                case T__33:
                case NUMBER:
                case STRING:
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 8);
                {
                    setState(66);
                    expression();
                    setState(67);
                    match(T__0);
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final FuncDeclarationContext funcDeclaration() throws RecognitionException {
        FuncDeclarationContext _localctx = new FuncDeclarationContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_funcDeclaration);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(71);
                match(T__1);
                setState(72);
                match(IDENTIFIER);
                setState(73);
                match(T__2);
                setState(74);
                paramList();
                setState(75);
                match(T__3);
                setState(78);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == T__4) {
                    {
                        setState(76);
                        match(T__4);
                        setState(77);
                        type();
                    }
                }

                setState(80);
                scope();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final ParamListContext paramList() throws RecognitionException {
        ParamListContext _localctx = new ParamListContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_paramList);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(90);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == IDENTIFIER) {
                    {
                        setState(82);
                        param();
                        setState(87);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == T__5) {
                            {
                                {
                                    setState(83);
                                    match(T__5);
                                    setState(84);
                                    param();
                                }
                            }
                            setState(89);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final ParamContext param() throws RecognitionException {
        ParamContext _localctx = new ParamContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_param);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(92);
                match(IDENTIFIER);
                setState(93);
                match(T__6);
                setState(94);
                type();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final ArgumentListContext argumentList() throws RecognitionException {
        ArgumentListContext _localctx = new ArgumentListContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_argumentList);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(104);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 3041440825352L) != 0)) {
                    {
                        setState(96);
                        expression();
                        setState(101);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == T__5) {
                            {
                                {
                                    setState(97);
                                    match(T__5);
                                    setState(98);
                                    expression();
                                }
                            }
                            setState(103);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final ScopeContext scope() throws RecognitionException {
        ScopeContext _localctx = new ScopeContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_scope);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(106);
                match(T__7);
                setState(110);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 3041440953356L) != 0)) {
                    {
                        {
                            setState(107);
                            statement();
                        }
                    }
                    setState(112);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(113);
                match(T__8);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final VariableDeclarationContext variableDeclaration() throws RecognitionException {
        VariableDeclarationContext _localctx = new VariableDeclarationContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_variableDeclaration);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(115);
                match(T__9);
                setState(116);
                match(IDENTIFIER);
                setState(117);
                match(T__6);
                setState(118);
                type();
                setState(119);
                match(T__10);
                setState(120);
                expression();
                setState(121);
                match(T__0);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final ForLoopContext forLoop() throws RecognitionException {
        ForLoopContext _localctx = new ForLoopContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_forLoop);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(123);
                match(T__11);
                setState(124);
                match(T__2);
                setState(125);
                variableDeclaration();
                setState(126);
                expression();
                setState(127);
                match(T__0);
                setState(128);
                expression();
                setState(129);
                match(T__3);
                setState(130);
                scope();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final WhileLoopContext whileLoop() throws RecognitionException {
        WhileLoopContext _localctx = new WhileLoopContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_whileLoop);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(132);
                match(T__12);
                setState(133);
                match(T__2);
                setState(134);
                expression();
                setState(135);
                match(T__3);
                setState(136);
                scope();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final IfConditionContext ifCondition() throws RecognitionException {
        IfConditionContext _localctx = new IfConditionContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_ifCondition);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(138);
                match(T__13);
                setState(139);
                match(T__2);
                setState(140);
                expression();
                setState(141);
                match(T__3);
                setState(142);
                scope();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final BreakStatementContext breakStatement() throws RecognitionException {
        BreakStatementContext _localctx = new BreakStatementContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_breakStatement);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(144);
                match(T__14);
                setState(145);
                match(T__0);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final ReturnStatementContext returnStatement() throws RecognitionException {
        ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_returnStatement);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(147);
                match(T__15);
                setState(149);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 3041440825352L) != 0)) {
                    {
                        setState(148);
                        expression();
                    }
                }

                setState(151);
                match(T__0);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final ExpressionContext expression() throws RecognitionException {
        ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_expression);
        try {
            setState(164);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 10, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(153);
                    match(IDENTIFIER);
                    setState(154);
                    assignmentOperator();
                    setState(155);
                    expression();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(157);
                    binaryExpression();
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(158);
                    unaryExpression();
                }
                break;
                case 4:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(159);
                    primaryExpression();
                }
                break;
                case 5:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(160);
                    match(T__2);
                    setState(161);
                    expression();
                    setState(162);
                    match(T__3);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final BinaryExpressionContext binaryExpression() throws RecognitionException {
        BinaryExpressionContext _localctx = new BinaryExpressionContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_binaryExpression);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(171);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case T__25:
                    case T__28:
                    case T__33:
                    case NUMBER:
                    case STRING:
                    case IDENTIFIER: {
                        setState(166);
                        primaryExpression();
                    }
                    break;
                    case T__2: {
                        setState(167);
                        match(T__2);
                        setState(168);
                        expression();
                        setState(169);
                        match(T__3);
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                setState(173);
                ((BinaryExpressionContext) _localctx).binaryOperator = _input.LT(1);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 536739840L) != 0))) {
                    ((BinaryExpressionContext) _localctx).binaryOperator = (Token) _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }
                setState(174);
                expression();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final UnaryExpressionContext unaryExpression() throws RecognitionException {
        UnaryExpressionContext _localctx = new UnaryExpressionContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_unaryExpression);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(176);
                ((UnaryExpressionContext) _localctx).unaryOperator = _input.LT(1);
                _la = _input.LA(1);
                if (!(_la == T__25 || _la == T__28)) {
                    ((UnaryExpressionContext) _localctx).unaryOperator = (Token) _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }
                setState(177);
                expression();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final AssignmentOperatorContext assignmentOperator() throws RecognitionException {
        AssignmentOperatorContext _localctx = new AssignmentOperatorContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_assignmentOperator);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(179);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 16106129408L) != 0))) {
                    _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final PrimaryExpressionContext primaryExpression() throws RecognitionException {
        PrimaryExpressionContext _localctx = new PrimaryExpressionContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_primaryExpression);
        try {
            setState(186);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 12, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(181);
                    literal();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(182);
                    unaryExpression();
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(183);
                    identifier();
                }
                break;
                case 4:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(184);
                    functionCall();
                }
                break;
                case 5:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(185);
                    arrayLiteral();
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final LiteralContext literal() throws RecognitionException {
        LiteralContext _localctx = new LiteralContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_literal);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(188);
                _la = _input.LA(1);
                if (!(_la == NUMBER || _la == STRING)) {
                    _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final IdentifierContext identifier() throws RecognitionException {
        IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_identifier);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(190);
                match(IDENTIFIER);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final FunctionCallContext functionCall() throws RecognitionException {
        FunctionCallContext _localctx = new FunctionCallContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_functionCall);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(192);
                match(IDENTIFIER);
                setState(193);
                match(T__2);
                setState(194);
                argumentList();
                setState(195);
                match(T__3);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final ArrayLiteralContext arrayLiteral() throws RecognitionException {
        ArrayLiteralContext _localctx = new ArrayLiteralContext(_ctx, getState());
        enterRule(_localctx, 42, RULE_arrayLiteral);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(197);
                match(T__33);
                setState(206);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 3041440825352L) != 0)) {
                    {
                        setState(198);
                        expression();
                        setState(203);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == T__5) {
                            {
                                {
                                    setState(199);
                                    match(T__5);
                                    setState(200);
                                    expression();
                                }
                            }
                            setState(205);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(208);
                match(T__34);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final TypeContext type() throws RecognitionException {
        TypeContext _localctx = new TypeContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_type);
        try {
            setState(219);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case PRIMITIVE_TYPE:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(210);
                    match(PRIMITIVE_TYPE);
                }
                break;
                case T__35:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(211);
                    match(T__35);
                }
                break;
                case T__36:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(212);
                    match(T__36);
                }
                break;
                case T__33:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(213);
                    match(T__33);
                    setState(214);
                    type();
                    setState(215);
                    match(T__0);
                    setState(216);
                    match(NUMBER);
                    setState(217);
                    match(T__34);
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ProgramContext extends ParserRuleContext {
        public ProgramContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode EOF() {
            return getToken(SnailParser.EOF, 0);
        }

        public List<VariableDeclarationContext> variableDeclaration() {
            return getRuleContexts(VariableDeclarationContext.class);
        }

        public VariableDeclarationContext variableDeclaration(int i) {
            return getRuleContext(VariableDeclarationContext.class, i);
        }

        public List<FuncDeclarationContext> funcDeclaration() {
            return getRuleContexts(FuncDeclarationContext.class);
        }

        public FuncDeclarationContext funcDeclaration(int i) {
            return getRuleContext(FuncDeclarationContext.class, i);
        }

        @Override
        public int getRuleIndex() {
            return RULE_program;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterProgram(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitProgram(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitProgram(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class StatementContext extends ParserRuleContext {
        public StatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public VariableDeclarationContext variableDeclaration() {
            return getRuleContext(VariableDeclarationContext.class, 0);
        }

        public ForLoopContext forLoop() {
            return getRuleContext(ForLoopContext.class, 0);
        }

        public FuncDeclarationContext funcDeclaration() {
            return getRuleContext(FuncDeclarationContext.class, 0);
        }

        public WhileLoopContext whileLoop() {
            return getRuleContext(WhileLoopContext.class, 0);
        }

        public IfConditionContext ifCondition() {
            return getRuleContext(IfConditionContext.class, 0);
        }

        public BreakStatementContext breakStatement() {
            return getRuleContext(BreakStatementContext.class, 0);
        }

        public ReturnStatementContext returnStatement() {
            return getRuleContext(ReturnStatementContext.class, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_statement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitStatement(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FuncDeclarationContext extends ParserRuleContext {
        public FuncDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(SnailParser.IDENTIFIER, 0);
        }

        public ParamListContext paramList() {
            return getRuleContext(ParamListContext.class, 0);
        }

        public ScopeContext scope() {
            return getRuleContext(ScopeContext.class, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_funcDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterFuncDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitFuncDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor)
                return ((SnailVisitor<? extends T>) visitor).visitFuncDeclaration(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ParamListContext extends ParserRuleContext {
        public ParamListContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public List<ParamContext> param() {
            return getRuleContexts(ParamContext.class);
        }

        public ParamContext param(int i) {
            return getRuleContext(ParamContext.class, i);
        }

        @Override
        public int getRuleIndex() {
            return RULE_paramList;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterParamList(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitParamList(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitParamList(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ParamContext extends ParserRuleContext {
        public ParamContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(SnailParser.IDENTIFIER, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_param;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterParam(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitParam(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitParam(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ArgumentListContext extends ParserRuleContext {
        public ArgumentListContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        @Override
        public int getRuleIndex() {
            return RULE_argumentList;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterArgumentList(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitArgumentList(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitArgumentList(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ScopeContext extends ParserRuleContext {
        public ScopeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public List<StatementContext> statement() {
            return getRuleContexts(StatementContext.class);
        }

        public StatementContext statement(int i) {
            return getRuleContext(StatementContext.class, i);
        }

        @Override
        public int getRuleIndex() {
            return RULE_scope;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterScope(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitScope(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitScope(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class VariableDeclarationContext extends ParserRuleContext {
        public VariableDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(SnailParser.IDENTIFIER, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_variableDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterVariableDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitVariableDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor)
                return ((SnailVisitor<? extends T>) visitor).visitVariableDeclaration(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ForLoopContext extends ParserRuleContext {
        public ForLoopContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public VariableDeclarationContext variableDeclaration() {
            return getRuleContext(VariableDeclarationContext.class, 0);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public ScopeContext scope() {
            return getRuleContext(ScopeContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_forLoop;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterForLoop(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitForLoop(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitForLoop(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class WhileLoopContext extends ParserRuleContext {
        public WhileLoopContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public ScopeContext scope() {
            return getRuleContext(ScopeContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_whileLoop;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterWhileLoop(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitWhileLoop(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitWhileLoop(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class IfConditionContext extends ParserRuleContext {
        public IfConditionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public ScopeContext scope() {
            return getRuleContext(ScopeContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_ifCondition;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterIfCondition(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitIfCondition(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitIfCondition(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class BreakStatementContext extends ParserRuleContext {
        public BreakStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_breakStatement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterBreakStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitBreakStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitBreakStatement(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ReturnStatementContext extends ParserRuleContext {
        public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_returnStatement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterReturnStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitReturnStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor)
                return ((SnailVisitor<? extends T>) visitor).visitReturnStatement(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ExpressionContext extends ParserRuleContext {
        public ExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(SnailParser.IDENTIFIER, 0);
        }

        public AssignmentOperatorContext assignmentOperator() {
            return getRuleContext(AssignmentOperatorContext.class, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public BinaryExpressionContext binaryExpression() {
            return getRuleContext(BinaryExpressionContext.class, 0);
        }

        public UnaryExpressionContext unaryExpression() {
            return getRuleContext(UnaryExpressionContext.class, 0);
        }

        public PrimaryExpressionContext primaryExpression() {
            return getRuleContext(PrimaryExpressionContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class BinaryExpressionContext extends ParserRuleContext {
        public Token binaryOperator;

        public BinaryExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public PrimaryExpressionContext primaryExpression() {
            return getRuleContext(PrimaryExpressionContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_binaryExpression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterBinaryExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitBinaryExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor)
                return ((SnailVisitor<? extends T>) visitor).visitBinaryExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class UnaryExpressionContext extends ParserRuleContext {
        public Token unaryOperator;

        public UnaryExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_unaryExpression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterUnaryExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitUnaryExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor)
                return ((SnailVisitor<? extends T>) visitor).visitUnaryExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class AssignmentOperatorContext extends ParserRuleContext {
        public AssignmentOperatorContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_assignmentOperator;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterAssignmentOperator(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitAssignmentOperator(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor)
                return ((SnailVisitor<? extends T>) visitor).visitAssignmentOperator(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PrimaryExpressionContext extends ParserRuleContext {
        public PrimaryExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public LiteralContext literal() {
            return getRuleContext(LiteralContext.class, 0);
        }

        public UnaryExpressionContext unaryExpression() {
            return getRuleContext(UnaryExpressionContext.class, 0);
        }

        public IdentifierContext identifier() {
            return getRuleContext(IdentifierContext.class, 0);
        }

        public FunctionCallContext functionCall() {
            return getRuleContext(FunctionCallContext.class, 0);
        }

        public ArrayLiteralContext arrayLiteral() {
            return getRuleContext(ArrayLiteralContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_primaryExpression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterPrimaryExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitPrimaryExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor)
                return ((SnailVisitor<? extends T>) visitor).visitPrimaryExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class LiteralContext extends ParserRuleContext {
        public LiteralContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode NUMBER() {
            return getToken(SnailParser.NUMBER, 0);
        }

        public TerminalNode STRING() {
            return getToken(SnailParser.STRING, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_literal;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterLiteral(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitLiteral(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitLiteral(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class IdentifierContext extends ParserRuleContext {
        public IdentifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(SnailParser.IDENTIFIER, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_identifier;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterIdentifier(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitIdentifier(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitIdentifier(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FunctionCallContext extends ParserRuleContext {
        public FunctionCallContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(SnailParser.IDENTIFIER, 0);
        }

        public ArgumentListContext argumentList() {
            return getRuleContext(ArgumentListContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_functionCall;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterFunctionCall(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitFunctionCall(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitFunctionCall(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ArrayLiteralContext extends ParserRuleContext {
        public ArrayLiteralContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        @Override
        public int getRuleIndex() {
            return RULE_arrayLiteral;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterArrayLiteral(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitArrayLiteral(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitArrayLiteral(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TypeContext extends ParserRuleContext {
        public TypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode PRIMITIVE_TYPE() {
            return getToken(SnailParser.PRIMITIVE_TYPE, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(SnailParser.NUMBER, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_type;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).enterType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SnailListener) ((SnailListener) listener).exitType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SnailVisitor) return ((SnailVisitor<? extends T>) visitor).visitType(this);
            else return visitor.visitChildren(this);
        }
    }
}