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

import oap.testng.AbstractTest;
import oap.testng.Env;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;

import static oap.testng.Asserts.assertFile;

public class SafeFileOutputStreamTest extends AbstractTest {
    @Test
    public void rename() throws IOException {
        Path path = Env.tmpPath( "1" );
        SafeFileOutputStream stream = new SafeFileOutputStream( path, false );
        stream.write( "111".getBytes() );
        stream.flush();
        assertFile( path ).doesNotExist();
        stream.close();
        assertFile( path ).hasContent( "111" );
    }

    @Test
    public void removeIfEmpty() throws IOException {
        Path path = Env.tmpPath( "1" );
        SafeFileOutputStream stream1 = new SafeFileOutputStream( path, false, true );
        stream1.flush();
        stream1.close();
        assertFile( path ).doesNotExist();

        SafeFileOutputStream stream2 = new SafeFileOutputStream( path, false, false );
        stream2.flush();
        stream2.close();
        assertFile( path ).exists();
    }
}
