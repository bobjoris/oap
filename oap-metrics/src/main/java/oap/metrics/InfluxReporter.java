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

package oap.metrics;

import oap.net.Inet;

import java.util.concurrent.TimeUnit;

public class InfluxReporter {
    protected String host;
    protected int port;
    protected String database;
    protected String login;
    protected String password;

    protected long period = 60 * 1000;

    protected Metrics metrics;

    private InfluxDBReporter reporter;

    public void start() {
        reporter = InfluxDBReporter.forRegistry( metrics.registry )
            .withTag( "host", Inet.HOSTNAME )
            .convertRatesTo( TimeUnit.MINUTES )
            .convertDurationsTo( TimeUnit.MICROSECONDS )
            .withConnect( host, port, database, login, password )
            .build();
        reporter.start( period, TimeUnit.MILLISECONDS );
    }

    public void stop() {
        reporter.stop();
    }
}
