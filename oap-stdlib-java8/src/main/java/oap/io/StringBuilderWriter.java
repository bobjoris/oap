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

package oap.io;

import java.io.Writer;

/**
 * Created by igor.petrenko on 06.05.2019.
 */
public class StringBuilderWriter extends Writer {
    private final StringBuilder sb;

    public StringBuilderWriter( StringBuilder sb ) {
        this.sb = sb;
    }

    @Override
    public void write( char[] cbuf, int off, int len ) {
        sb.append( cbuf, off, len );
    }

    @Override
    public void write( int c ) {
        sb.append( ( char ) c );
    }

    @Override
    public void write( char[] cbuf ) {
        sb.append( cbuf );
    }

    @Override
    public void write( String str ) {
        sb.append( str );
    }

    @Override
    public void write( String str, int off, int len ) {
        sb.append( str, off, len );
    }

    @Override
    public Writer append( CharSequence csq ) {
        sb.append( csq );
        return this;
    }

    @Override
    public Writer append( CharSequence csq, int start, int end ) {
        sb.append( csq, start, end );
        return this;
    }

    @Override
    public Writer append( char c ) {
        sb.append( c );

        return this;
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }
}
