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

package oap.application.remote;

import oap.application.ApplicationConfiguration;
import oap.application.ApplicationException;
import oap.application.Kernel;
import oap.application.module.Module;
import oap.testng.EnvFixture;
import oap.testng.Fixtures;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Optional;

import static oap.testng.Asserts.pathOfTestResource;
import static oap.testng.Asserts.urlOfTestResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class RemoteTest extends Fixtures {

    private final EnvFixture envFixture;

    public RemoteTest() {
        envFixture = fixture( new EnvFixture() )
            .definePort( "HTTP_PORT" );
    }

    @Test
    public void invoke() {
        var port = envFixture.portFor( "HTTP_PORT" );
        var modules = Module.CONFIGURATION.urlsFromClassPath();
        modules.add( urlOfTestResource( getClass(), "module.conf" ) );
        try( var kernel = new Kernel( modules ) ) {
            kernel.start( ApplicationConfiguration.load( pathOfTestResource( RemoteTest.class, "application.conf" ) ) );

            assertThat( kernel.<RemoteClient>service( "*.remote-client" ) )
                .isPresent()
                .get()
                .satisfies( remote -> {
                    assertThat( remote.accessible() ).isTrue();
                    //this tests local methods of Object.class
                    assertThat( remote.toString() ).isEqualTo( "remote:remote-service(retry=5)@http://localhost:" + port + "/remote/" );
                } );

            assertThat( kernel.<RemoteClient>service( "*.remote-client" ) )
                .isPresent()
                .get()
                .satisfies( remote -> assertThatThrownBy( remote::erroneous ).isInstanceOf( IllegalStateException.class ) );

            assertThat( kernel.<RemoteClient>service( "*.remote-client" ) )
                .isPresent()
                .get()
                .satisfies( RemoteClient::testRetry );

            assertThat( kernel.<RemoteClient>service( "*.remote-client-unreachable" ) )
                .isPresent()
                .get()
                .satisfies( remote -> assertThatThrownBy( remote::accessible ).isInstanceOf( RemoteInvocationException.class ) );
        }
    }

    @Test
    public void testStream() {
        var modules = Module.CONFIGURATION.urlsFromClassPath();
        modules.add( urlOfTestResource( getClass(), "module.conf" ) );

        try( var kernel = new Kernel( modules ) ) {
            kernel.start( ApplicationConfiguration.load( pathOfTestResource( RemoteTest.class, "application.conf" ) ) );

            assertThat( kernel.<RemoteClient>service( "*.remote-client" ).get().testStream( "1", "2", "3" ) )
                .contains( Optional.of( "1" ), Optional.of( "2" ), Optional.of( "3" ) );
            assertThat( kernel.<RemoteClient>service( "*.remote-client" ).get().testStream( "1", "2", "3" ) )
                .contains( Optional.of( "1" ), Optional.of( "2" ), Optional.of( "3" ) );
        }
    }

    @Test
    public void testEmptyStream() {
        var modules = Module.CONFIGURATION.urlsFromClassPath();
        modules.add( urlOfTestResource( getClass(), "module.conf" ) );

        try( var kernel = new Kernel( modules ) ) {
            kernel.start( ApplicationConfiguration.load( pathOfTestResource( RemoteTest.class, "application.conf" ) ) );

            assertThat( kernel.<RemoteClient>service( "*.remote-client" ).get().testStream() ).isEmpty();
        }
    }

    @Test
    public void testRemotingUri() {
        var modules = Module.CONFIGURATION.urlsFromClassPath();
        modules.add( urlOfTestResource( getClass(), "invalid_remote.conf" ) );

        try( var kernel = new Kernel( modules ) ) {
            assertThatCode( () -> kernel.start( Map.of( "boot.main", "oap-module-with-invalid-remoting" ) ) )
                .isInstanceOf( ApplicationException.class )
                .hasMessage( "remoting: uri == null, services [oap-module-with-invalid-remoting:remote-client]" );
        }
    }

}
