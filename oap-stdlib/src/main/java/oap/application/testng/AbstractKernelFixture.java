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

package oap.application.testng;

import com.google.common.base.Preconditions;
import oap.application.ApplicationConfiguration;
import oap.application.Kernel;
import oap.application.module.Module;
import oap.io.Resources;
import oap.json.Binder;
import oap.json.JsonException;
import oap.reflect.TypeRef;
import oap.testng.AbstractEnvFixture;
import oap.testng.TestDirectoryFixture;
import oap.util.Pair;

import javax.annotation.Nonnull;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static oap.http.testng.HttpAsserts.httpPrefix;
import static oap.io.IoStreams.Encoding.PLAIN;
import static oap.testng.TestDirectoryFixture.testDirectory;
import static oap.util.Pair.__;

public abstract class AbstractKernelFixture<Self extends AbstractKernelFixture<Self>> extends AbstractEnvFixture<Self> {
    public static final String ANY = "*";
    public static final String TEST_HTTP_PORT = "TEST_HTTP_PORT";
    public static final String TEST_DIRECTORY = "TEST_DIRECTORY";
    public static final String TEST_RESOURCE_PATH = "TEST_RESOURCE_PATH";
    public static final String TEST_HTTP_PREFIX = "TEST_HTTP_PREFIX";
    private static int kernelN = 0;
    protected final URL applicationConf;
    protected final List<URL> additionalModules = new ArrayList<>();
    private final LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
    private final LinkedHashSet<String> profiles = new LinkedHashSet<>();
    private final ArrayList<Pair<Class<?>, String>> confd = new ArrayList<>();
    private final ArrayList<Pair<Class<?>, String>> conf = new ArrayList<>();
    public Kernel kernel;
    protected Path confdPath;

    public AbstractKernelFixture( String prefix, URL conf ) {
        this( prefix, Scope.METHOD, conf, null, List.of() );
    }

    public AbstractKernelFixture( String prefix, URL conf, Path confd ) {
        this( prefix, Scope.METHOD, conf, confd, List.of() );
    }

    public AbstractKernelFixture( String prefix, URL conf, List<URL> additionalModules ) {
        this( prefix, Scope.METHOD, conf, null, additionalModules );
    }

    public AbstractKernelFixture( String prefix, Scope scope, URL conf, Path confdPath, List<URL> additionalModules ) {
        super( prefix );
        this.scope = scope;
        this.applicationConf = conf;
        this.confdPath = confdPath;
        this.additionalModules.addAll( additionalModules );

        defineDefaults();
    }

    @SuppressWarnings( "unchecked" )
    public Self withFileProperties( URL location ) throws JsonException {
        var map = Binder.Format.of( location, true ).binder.unmarshal( new TypeRef<Map<String, Object>>() {}, location );
        properties.putAll( map );

        return ( Self ) this;
    }

    @SuppressWarnings( "unchecked" )
    public Self withProperties( Map<String, Object> properties ) {
        this.properties.putAll( properties );

        return ( Self ) this;
    }

    public int defaultHttpPort() {
        return portFor( TEST_HTTP_PORT );
    }

    protected void defineDefaults() {
        var testHttpPort = portFor( TEST_HTTP_PORT );
        define( TEST_HTTP_PORT, testHttpPort );
        define( TEST_DIRECTORY, testDirectory() );
        String resourcePath = Resources.path( getClass(), "/" ).orElseThrow();
        define( TEST_RESOURCE_PATH, resourcePath );
        define( TEST_HTTP_PREFIX, httpPrefix( testHttpPort ) );
//        deprecated
        define( "HTTP_PREFIX", httpPrefix( testHttpPort ) );
    }

    @SuppressWarnings( "unchecked" )
    public Self withConfdResources( Class<?> clazz, String confdResource ) throws UncheckedIOException {
        initConfd();

        confd.add( __( clazz, confdResource ) );

        return ( Self ) this;
    }

    public Self withLocalConfdResources( Class<?> clazz, String confdResource ) throws UncheckedIOException {
        var cr = confdResource.startsWith( "/" ) ? confdResource : "/" + confdResource;
        cr = clazz.getSimpleName() + cr;

        return withConfdResources( clazz, cr );
    }

    @SuppressWarnings( "unchecked" )
    public Self withProfile( String profile ) {
        this.profiles.add( profile );
        return ( Self ) this;
    }

    @SuppressWarnings( "unchecked" )
    public Self withConfResource( Class<?> clazz, String confdResource ) throws UncheckedIOException {
        initConfd();

        conf.add( __( clazz, confdResource ) );

        return ( Self ) this;
    }

    public Self withLocalConfResource( Class<?> clazz, String confdResource ) throws UncheckedIOException {
        var cr = confdResource.startsWith( "/" ) ? confdResource : "/" + confdResource;
        cr = clazz.getSimpleName() + cr;

        return withConfResource( clazz, cr );
    }

    private void initConfd() {
        if( this.confdPath == null )
            this.confdPath = TestDirectoryFixture.testPath( "/application.test.confd" + "." + prefix );
    }

    @Nonnull
    public <T> T service( @Nonnull String moduleName, @Nonnull Class<T> clazz ) throws IllegalArgumentException {
        return kernel.serviceOfClass( moduleName, clazz )
            .orElseThrow( () -> new IllegalArgumentException( "unknown service " + moduleName + ":" + clazz ) );
    }

    @Nonnull
    public <T> T service( @Nonnull String moduleName, @Nonnull String serviceName ) throws IllegalArgumentException {
        return kernel.<T>service( moduleName, serviceName )
            .orElseThrow( () -> new IllegalArgumentException( "unknown service " + moduleName + ":" + serviceName ) );
    }

    @Nonnull
    public <T> T service( @Nonnull String reference ) throws IllegalArgumentException {
        return kernel.<T>service( reference )
            .orElseThrow( () -> new IllegalArgumentException( "unknown service " + reference ) );
    }

    public <T> List<T> ofClass( Class<T> clazz ) {
        return kernel.ofClass( clazz );
    }

    public <T> List<T> ofClass( String moduleName, Class<T> clazz ) {
        return kernel.ofClass( moduleName, clazz );
    }

    @Override
    protected void before() {
        defineDefaults();

        Preconditions.checkArgument( this.kernel == null );
        super.before();

        for( var cd : confd ) {
            Resources.filePaths( cd._1, cd._2 )
                .forEach( path -> oap.io.Files.copyDirectory( path, this.confdPath ) );
        }


        for( var cd : conf ) {
            Resources.filePath( cd._1, cd._2 )
                .ifPresent( path -> oap.io.Files.copy( path, PLAIN, this.confdPath.resolve( path.getFileName() ), PLAIN ) );
        }


        var moduleConfigurations = Module.CONFIGURATION.urlsFromClassPath();
        moduleConfigurations.addAll( additionalModules );
        this.kernel = new Kernel( "FixtureKernel#" + kernelN++ + "#" + prefix, moduleConfigurations );

        var confds = ApplicationConfiguration.getConfdUrls( confdPath );

        var applicationConfiguration = ApplicationConfiguration.load( applicationConf, confds, properties );

        for( var newProfile : profiles ) {
            var enabled = !newProfile.startsWith( "-" );
            if( enabled ) applicationConfiguration.profiles.remove( "-" + newProfile );
            else applicationConfiguration.profiles.remove( newProfile.substring( 1 ) );
        }

        applicationConfiguration.profiles.addAll( profiles );
        this.kernel.start( applicationConfiguration );
    }

    @Override
    protected void after() {
        this.kernel.stop();
        this.kernel = null;
        super.after();
    }
}
