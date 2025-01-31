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

package oap.testng;


import com.typesafe.config.impl.ConfigImpl;
import lombok.extern.slf4j.Slf4j;
import oap.concurrent.Threads;
import oap.io.Sockets;
import oap.util.Strings;

import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static oap.testng.Asserts.locationOfTestResource;

@Slf4j
public abstract class AbstractEnvFixture<Self extends AbstractEnvFixture<Self>> extends AbstractScopeFixture<Self> {
    public static final String NO_PREFIX = "";
    public static final AtomicInteger LAST_PORT = new AtomicInteger( 20000 );
    protected final String prefix;
    private final ConcurrentHashMap<String, Integer> ports = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> properties = new ConcurrentHashMap<>();

    public AbstractEnvFixture() {
        this( NO_PREFIX );
    }

    public AbstractEnvFixture( String prefix ) {
        this.prefix = prefix;
    }

    @SuppressWarnings( "unchecked" )
    public Self define( String property, Object value ) {
        properties.put( prefix + property, value );

        return ( Self ) this;
    }

    public Self definePort( String property ) throws UncheckedIOException {
        return define( property, portFor( property ) );
    }

    public Self defineLocalClasspath( String property, Class<?> clazz, String resourceName ) {
        return define( property, "classpath(" + locationOfTestResource( clazz, resourceName ) + ")" );
    }

    public Self defineClasspath( String property, Class<?> clazz, String resourceLocation ) {
        return define( property, "classpath(" + resourceLocation + ")" );
    }

    public Self definePath( String property, Path path ) {
        return define( property, "path(" + path + ")" );
    }

    public Self defineURL( String property, URL url ) {
        return define( property, "url(" + url + ")" );
    }

    @Override
    protected void before() {
        properties.forEach( ( variableName, v ) -> {
            var value = Strings.substitute( String.valueOf( v ),
                k -> System.getenv( k ) == null ? System.getProperty( k ) : System.getenv( k ) );
            System.setProperty( variableName, value );
            ConfigImpl.reloadSystemPropertiesConfig();
        } );
    }

    public int portFor( Class<?> clazz ) throws UncheckedIOException {
        return portFor( clazz.getName() );
    }

    public int portFor( String key ) throws UncheckedIOException {
        return Threads.withThreadName( getUniqueName(), () -> {
            synchronized( LAST_PORT ) {
                return ports.computeIfAbsent( prefix + key, k -> {
                    int port;
                    do {
                        port = LAST_PORT.incrementAndGet();
                    } while( !Sockets.isTcpPortAvailable( port ) );

                    log.debug( "{} finding port for key={}... port={}", this.getClass().getSimpleName(), k, port );
                    return port;
                } );
            }
        } );
    }
}
