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

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;
import com.github.rollingmetrics.histogram.HdrBuilder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static oap.metrics.Metrics.name;

public class Metrics2 {
    static ConcurrentHashMap<String, Timer> hdrTimers = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, Histogram> hdrHistgrams = new ConcurrentHashMap<>();

    public static Timer timer( Name name ) {
        return timer( name.line );
    }

    public static Timer timer( String name ) {
        var builder = new HdrBuilder();
        var timer = builder.resetReservoirOnSnapshot().buildAndRegisterTimer( Metrics.registry, name );
        hdrTimers.put( name, timer );
        return timer;
    }

    private static Timer getOrCreateTimer( String metric ) {
        return hdrTimers.computeIfAbsent( metric, ( name ) -> timer( metric ) );
    }

    private static Histogram getOrCreateHistogram( String metric ) {
        return hdrHistgrams.computeIfAbsent( metric, ( name ) -> histogram( metric ) );
    }

    public static void measureTimer( Name metric, Runnable code ) {
        try( Timer.Context ignored = getOrCreateTimer( metric.line ).time() ) {
            code.run();
        }
    }

    public static void measureTimer( Name metric, long duration, TimeUnit unit ) {
        getOrCreateTimer( metric.line ).update( duration, unit );
    }


    public static <T> T measureTimer( String metric, Supplier<T> code ) {
        return measureTimer( name( metric ), code );
    }

    public static <T> T measureTimer( Name metric, Supplier<T> code ) {
        try( Timer.Context ignored = getOrCreateTimer( metric.line ).time() ) {
            return code.get();
        }
    }

    public static void measureHistogram( String metric, long value ) {
        measureHistogram( name( metric ), value );
    }

    public static void measureHistogram( Name metric, long value ) {
        getOrCreateHistogram( metric.line ).update( value );
    }

    public static Histogram histogram( String name ) {
        var builder = new HdrBuilder();
        var histogram = builder.resetReservoirOnSnapshot().buildAndRegisterHistogram( Metrics.registry, name );
        hdrHistgrams.put( name, histogram );
        return histogram;
    }
}
