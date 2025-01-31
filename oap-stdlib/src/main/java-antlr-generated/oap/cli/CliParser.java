/*
 * The MIT License (MIT)
 *
 * Copyright (c) Open Application Platform Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

// Generated from Cli.g4 by ANTLR 4.5

package oap.cli;

import oap.util.Pair;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

import static oap.util.Pair.__;

@SuppressWarnings( { "all", "warnings", "unchecked", "unused", "cast" } )
public class CliParser extends Parser {
    static {
        RuntimeMetaData.checkVersion( "4.5", RuntimeMetaData.VERSION );
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
        new PredictionContextCache();
    public static final int
        NAME = 1, VALUE = 2, STRVALUE = 3, WS = 4;
    public static final int
        RULE_parameters = 0, RULE_parameter = 1, RULE_value = 2;
    public static final String[] ruleNames = {
        "parameters", "parameter", "value"
    };

    private static final String[] _LITERAL_NAMES = {
    };
    private static final String[] _SYMBOLIC_NAMES = {
        null, "NAME", "VALUE", "STRVALUE", "WS"
    };
    public static final Vocabulary VOCABULARY = new VocabularyImpl( _LITERAL_NAMES, _SYMBOLIC_NAMES );

    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;

    static {
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for( int i = 0; i < tokenNames.length; i++ ) {
            tokenNames[i] = VOCABULARY.getLiteralName( i );
            if( tokenNames[i] == null ) {
                tokenNames[i] = VOCABULARY.getSymbolicName( i );
            }

            if( tokenNames[i] == null ) {
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
    public String getGrammarFileName() { return "Cli.g4"; }

    @Override
    public String[] getRuleNames() { return ruleNames; }

    @Override
    public String getSerializedATN() { return _serializedATN; }

    @Override
    public ATN getATN() { return _ATN; }

    public CliParser( TokenStream input ) {
        super( input );
        _interp = new ParserATNSimulator( this, _ATN, _decisionToDFA, _sharedContextCache );
    }

    public static class ParametersContext extends ParserRuleContext {
        public List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
        public ParameterContext p;

        public TerminalNode EOF() { return getToken( CliParser.EOF, 0 ); }

        public List<ParameterContext> parameter() {
            return getRuleContexts( ParameterContext.class );
        }

        public ParameterContext parameter( int i ) {
            return getRuleContext( ParameterContext.class, i );
        }

        public ParametersContext( ParserRuleContext parent, int invokingState ) {
            super( parent, invokingState );
        }

        @Override
        public int getRuleIndex() { return RULE_parameters; }

        @Override
        public void enterRule( ParseTreeListener listener ) {
            if( listener instanceof CliListener ) ( ( CliListener ) listener ).enterParameters( this );
        }

        @Override
        public void exitRule( ParseTreeListener listener ) {
            if( listener instanceof CliListener ) ( ( CliListener ) listener ).exitParameters( this );
        }
    }

    public final ParametersContext parameters() throws RecognitionException {
        ParametersContext _localctx = new ParametersContext( _ctx, getState() );
        enterRule( _localctx, 0, RULE_parameters );
        int _la;
        try {
            enterOuterAlt( _localctx, 1 );
            {
                setState( 11 );
                _errHandler.sync( this );
                _la = _input.LA( 1 );
                while( _la == NAME ) {
                    {
                        {
                            setState( 6 );
                            ( ( ParametersContext ) _localctx ).p = parameter();
                            _localctx.list.add( ( ( ParametersContext ) _localctx ).p.p );
                        }
                    }
                    setState( 13 );
                    _errHandler.sync( this );
                    _la = _input.LA( 1 );
                }
                setState( 14 );
                match( EOF );
            }
        } catch( RecognitionException re ) {
            _localctx.exception = re;
            _errHandler.reportError( this, re );
            _errHandler.recover( this, re );
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ParameterContext extends ParserRuleContext {
        public Pair<String, String> p = null;
        public Token n;
        public ValueContext v;

        public TerminalNode NAME() { return getToken( CliParser.NAME, 0 ); }

        public ValueContext value() {
            return getRuleContext( ValueContext.class, 0 );
        }

        public ParameterContext( ParserRuleContext parent, int invokingState ) {
            super( parent, invokingState );
        }

        @Override
        public int getRuleIndex() { return RULE_parameter; }

        @Override
        public void enterRule( ParseTreeListener listener ) {
            if( listener instanceof CliListener ) ( ( CliListener ) listener ).enterParameter( this );
        }

        @Override
        public void exitRule( ParseTreeListener listener ) {
            if( listener instanceof CliListener ) ( ( CliListener ) listener ).exitParameter( this );
        }
    }

    public final ParameterContext parameter() throws RecognitionException {
        ParameterContext _localctx = new ParameterContext( _ctx, getState() );
        enterRule( _localctx, 2, RULE_parameter );
        int _la;
        try {
            enterOuterAlt( _localctx, 1 );
            {
                setState( 16 );
                ( ( ParameterContext ) _localctx ).n = match( NAME );
                setState( 18 );
                _la = _input.LA( 1 );
                if( _la == VALUE || _la == STRVALUE ) {
                    {
                        setState( 17 );
                        ( ( ParameterContext ) _localctx ).v = value();
                    }
                }

                ( ( ParameterContext ) _localctx ).p = __( ( ( ( ParameterContext ) _localctx ).n != null
                    ? ( ( ParameterContext ) _localctx ).n.getText() : null ), (
                    ( ( ParameterContext ) _localctx ).v != null
                        ? _input.getText( ( ( ParameterContext ) _localctx ).v.start, ( ( ParameterContext ) _localctx ).v.stop )
                        : null ) );
            }
        } catch( RecognitionException re ) {
            _localctx.exception = re;
            _errHandler.reportError( this, re );
            _errHandler.recover( this, re );
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ValueContext extends ParserRuleContext {
        public TerminalNode VALUE() { return getToken( CliParser.VALUE, 0 ); }

        public TerminalNode STRVALUE() { return getToken( CliParser.STRVALUE, 0 ); }

        public ValueContext( ParserRuleContext parent, int invokingState ) {
            super( parent, invokingState );
        }

        @Override
        public int getRuleIndex() { return RULE_value; }

        @Override
        public void enterRule( ParseTreeListener listener ) {
            if( listener instanceof CliListener ) ( ( CliListener ) listener ).enterValue( this );
        }

        @Override
        public void exitRule( ParseTreeListener listener ) {
            if( listener instanceof CliListener ) ( ( CliListener ) listener ).exitValue( this );
        }
    }

    public final ValueContext value() throws RecognitionException {
        ValueContext _localctx = new ValueContext( _ctx, getState() );
        enterRule( _localctx, 4, RULE_value );
        int _la;
        try {
            enterOuterAlt( _localctx, 1 );
            {
                setState( 22 );
                _la = _input.LA( 1 );
                if( !( _la == VALUE || _la == STRVALUE ) ) {
                    _errHandler.recoverInline( this );
                } else {
                    consume();
                }
            }
        } catch( RecognitionException re ) {
            _localctx.exception = re;
            _errHandler.reportError( this, re );
            _errHandler.recover( this, re );
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static final String _serializedATN =
        "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\6\33\4\2\t\2\4\3"
            + "\t\3\4\4\t\4\3\2\3\2\3\2\7\2\f\n\2\f\2\16\2\17\13\2\3\2\3\2\3\3\3\3\5"
            + "\3\25\n\3\3\3\3\3\3\4\3\4\3\4\2\2\5\2\4\6\2\3\3\2\4\5\31\2\r\3\2\2\2\4"
            + "\22\3\2\2\2\6\30\3\2\2\2\b\t\5\4\3\2\t\n\b\2\1\2\n\f\3\2\2\2\13\b\3\2"
            + "\2\2\f\17\3\2\2\2\r\13\3\2\2\2\r\16\3\2\2\2\16\20\3\2\2\2\17\r\3\2\2\2"
            + "\20\21\7\2\2\3\21\3\3\2\2\2\22\24\7\3\2\2\23\25\5\6\4\2\24\23\3\2\2\2"
            + "\24\25\3\2\2\2\25\26\3\2\2\2\26\27\b\3\1\2\27\5\3\2\2\2\30\31\t\2\2\2"
            + "\31\7\3\2\2\2\4\r\24";
    public static final ATN _ATN =
        new ATNDeserializer().deserialize( _serializedATN.toCharArray() );

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for( int i = 0; i < _ATN.getNumberOfDecisions(); i++ ) {
            _decisionToDFA[i] = new DFA( _ATN.getDecisionState( i ), i );
        }
    }
}
