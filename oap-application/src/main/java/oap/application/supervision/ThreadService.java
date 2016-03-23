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
package oap.application.supervision;

import oap.concurrent.SynchronizedThread;
import oap.io.Closeables;
import org.slf4j.Logger;

import java.io.Closeable;

import static org.slf4j.LoggerFactory.getLogger;

public class ThreadService implements Runnable, Supervised {
    private SynchronizedThread thread = new SynchronizedThread( this );
    private Runnable supervisee;
    private final Supervisor supervisor;
    private static Logger logger = getLogger( ThreadService.class );
    private int maxFailures = 100;

    public ThreadService( String name, Runnable supervisee, Supervisor supervisor ) {
        this.supervisee = supervisee;
        this.supervisor = supervisor;
        this.thread.setName( name );
    }


    @Override
    public void run() {
        while( thread.isRunning() && maxFailures > 0 ) try {
            supervisee.run();
        } catch( Exception e ) {
            maxFailures--;
            logger.error( "Crushed unexpectedly with message: " + e.getMessage() + ". Restarting...", e );
        }
        if( maxFailures <= 0 ) {
            logger.error( this + " constantly crushing. Requesting shutdown..." );
            supervisor.stop();
        }
    }


    public synchronized void start() {
        logger.debug( "starting " + thread.getName() );
        thread.start();
    }

    public synchronized void stop() {
        logger.debug( "stopping " + thread.getName() );
        if( thread instanceof Closeable ) Closeables.close( ( Closeable ) thread );
        thread.stop();
    }
}
