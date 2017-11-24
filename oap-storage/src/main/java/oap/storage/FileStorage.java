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
package oap.storage;

import oap.storage.migration.FileStorageMigration;
import oap.util.Lists;
import oap.util.Try;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static oap.util.Lists.empty;

/**
 * CAUTION: fsResolve should be using STABLE values ONLY. File relocation on the filesystem IS NOT SUPPORTED!
 *
 * @param <T>
 */
public class FileStorage<T> extends MemoryStorage<T> {
    private static final int VERSION = 0;
    private PersistenceBackend<T> persistence;

    public FileStorage( Path path, Identifier<T> identifier, long fsync, int version,
                        List<String> migrations, LockStrategy lockStrategy ) {
        this( path, ( p, object ) -> p, identifier, fsync, version, migrations, lockStrategy );
    }

    public FileStorage( Path path, BiFunction<Path, T, Path> fsResolve, Identifier<T> identifier,
                        long fsync, int version, List<String> migrations, LockStrategy lockStrategy ) {
        super( identifier, lockStrategy );
        this.persistence = new FsPersistenceBackend<>( path, fsResolve, fsync, version, Lists.map( migrations,
            Try.map( clazz -> ( FileStorageMigration ) Class.forName( clazz ).newInstance() )
        ), this );
    }

    public FileStorage( Path path, BiFunction<Path, T, Path> fsResolve, Identifier<T> identifier, long fsync, LockStrategy lockStrategy ) {
        this( path, fsResolve, identifier, fsync, VERSION, empty(), lockStrategy );
    }

    public FileStorage( Path path, Identifier<T> identifier, long fsync, LockStrategy lockStrategy ) {
        this( path, identifier, fsync, VERSION, empty(), lockStrategy );
    }

    public FileStorage( Path path, Identifier<T> identifier, LockStrategy lockStrategy ) {
        this( path, identifier, VERSION, empty(), lockStrategy );
    }

    public FileStorage( Path path, BiFunction<Path, T, Path> fsResolve, Identifier<T> identifier,
                        int version, List<String> migrations, LockStrategy lockStrategy ) {
        this( path, fsResolve, identifier, 60000, version, migrations, lockStrategy );
    }

    public FileStorage( Path path, Identifier<T> identifier, int version, List<String> migrations,
                        LockStrategy lockStrategy ) {
        this( path, identifier, 60000, version, migrations, lockStrategy );
    }

    @Override
    public Optional<T> delete( String id ) {
        final Optional<T> d = super.delete( id );
        d.ifPresent( persistence::delete );

        return d;
    }

    @Override
    public void fsync() {
        super.fsync();

        persistence.fsync();
    }

    @Override
    public synchronized void close() {
        persistence.close();
        data.clear();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + persistence;
    }
}
