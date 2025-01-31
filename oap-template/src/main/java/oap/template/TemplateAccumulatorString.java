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

package oap.template;

import java.util.Collection;

import static java.util.stream.Collectors.joining;

public class TemplateAccumulatorString implements TemplateAccumulator<String, StringBuilder, TemplateAccumulatorString> {
    protected final StringBuilder sb;

    public TemplateAccumulatorString( StringBuilder sb ) {
        this.sb = sb;
    }

    public TemplateAccumulatorString() {
        this( new StringBuilder() );
    }

    @Override
    public void acceptText( String text ) {
        if( text != null )
            sb.append( text );
    }

    @Override
    public void accept( String text ) {
        if( text != null ) {
            sb.append( text );
        }
    }

    @Override
    public void accept( boolean b ) {
        sb.append( b );
    }

    @Override
    public void accept( char ch ) {
        sb.append( ch );
    }

    @Override
    public void accept( byte b ) {
        sb.append( b );
    }

    @Override
    public void accept( short s ) {
        sb.append( s );
    }

    @Override
    public void accept( int i ) {
        sb.append( i );
    }

    @Override
    public void accept( long l ) {
        sb.append( l );
    }

    @Override
    public void accept( float f ) {
        sb.append( f );
    }

    @Override
    public void accept( double d ) {
        sb.append( d );
    }

    @Override
    public void accept( Enum<?> e ) {
        sb.append( e.name() );
    }

    @Override
    public void accept( Collection<?> list ) {
        if( list != null )
            sb.append( list.stream().map( String::valueOf ).collect( joining( ",", "[", "]" ) ) );
    }

    @Override
    public void accept( TemplateAccumulatorString acc ) {
        sb.append( acc.sb );
    }

    @Override
    public void accept( Object obj ) {
        accept( String.valueOf( obj ) );
    }

    @Override
    public boolean isEmpty() {
        return sb.length() == 0;
    }

    @Override
    public TemplateAccumulatorString newInstance() {
        return new TemplateAccumulatorString();
    }

    @Override
    public TemplateAccumulatorString newInstance( StringBuilder mutable ) {
        return new TemplateAccumulatorString( mutable );
    }

    @Override
    public String getTypeName() {
        return "String";
    }

    @Override
    public String get() {
        return sb.toString();
    }
}
