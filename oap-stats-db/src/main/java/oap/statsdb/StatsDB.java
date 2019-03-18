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

package oap.statsdb;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import oap.storage.Storage;
import oap.util.Stream;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
public abstract class StatsDB<T extends StatsDB.Database> {
    protected final Storage<IdNode> storage;
    protected final KeySchema schema;

    public StatsDB( KeySchema schema, Storage<IdNode> storage ) {
        this.schema = schema;
        this.storage = storage;
    }

    @SuppressWarnings( "unchecked" )
    protected static void updateAggregates( Node mnode ) {
        for( val node : mnode.db.values() ) {
            updateAggregates( node );
        }

        final Node.Value value = mnode.v;
        if( value instanceof Node.Container ) {
            ( ( Node.Container ) value ).aggregate( Stream.of( mnode.db.values() )
                .map( n -> n.v )
                .filter( Objects::nonNull )
                .collect( toList())
            );
        }
    }

    protected abstract T toDatabase( ConcurrentHashMap<String, Node> db );

    protected <V extends Node.Value<V>> void update( String[] key, Consumer<V> update, Supplier<V> create ) {
        storage.update( key[0],
            node -> updateNode( key, update, create, node, schema ),
            () -> updateNode( key, update, create, new IdNode( key[0] ), schema )
        );
    }

    protected <V extends Node.Value<V>>
    void update( String key1, Consumer<V> update, Supplier<V> create ) {
        update( new String[] { key1 }, update, create );
    }

    protected <V extends Node.Value<V>>
    void update( String key1, String key2, Consumer<V> update, Supplier<V> create ) {
        update( new String[] { key1, key2 }, update, create );
    }

    protected <V extends Node.Value<V>>
    void update( String key1, String key2, String key3, Consumer<V> update, Supplier<V> create ) {
        update( new String[] { key1, key2, key3 }, update, create );
    }

    protected <V extends Node.Value<V>>
    void update( String key1, String key2, String key3, String key4, Consumer<V> update, Supplier<V> create ) {
        update( new String[] { key1, key2, key3, key4 }, update, create );
    }

    protected <V extends Node.Value<V>>
    void update( String key1, String key2, String key3, String key4, String key5, Consumer<V> update, Supplier<V> create ) {
        update( new String[] { key1, key2, key3, key4, key5 }, update, create );
    }

    @SuppressWarnings( "unchecked" )
    public <V extends Node.Value<V>> V get( String... key ) {
        val node = getNode( key );
        return node != null ? ( V ) node.v : null;
    }

    public Node _getNode( String[] key, int position, Node node ) {
        if( node == null ) return null;
        if( position >= key.length ) return node;

        return _getNode( key, position + 1, node.db.get( key[position] ) );
    }

    protected Node getNode( String... key ) {
        if( key.length == 0 ) return null;

        return _getNode( key, 1, storage.get( key[0] ).orElse( null ) );
    }

    public <V extends Node.Value<V>> Stream<V> children( String... key ) {
        if( key.length == 0 ) return Stream.empty();

        return _children( key, 1, storage.get( key[0] ).orElse( null ) );
    }

    @SuppressWarnings( "unchecked" )
    private <V extends Node.Value<V>> Stream<V> _children( String[] key, int position, Node node ) {
        if( node == null ) return Stream.empty();
        if( position >= key.length ) return Stream.of( node.db.values() ).map( n -> ( V ) n.v );

        return _children( key, position + 1, node.db.get( key[position] ) );
    }

    public <N extends Node, V extends Node.Value<V>> N updateNode(
        String[] key, Consumer<V> update, Supplier<V> create, N node, KeySchema schema ) {
        Node tNode = node;

        for( int i = 1; i < key.length; i++ ) {
            val keyItem = key[i];
            tNode = tNode.db.computeIfAbsent( keyItem, ( k ) -> new Node() );
        }

        tNode.updateValue( update, create );

        return node;
    }

    protected void start( T database ) {

    }

    public synchronized void removeAll() {
        storage.deleteAll();
    }

    @SuppressWarnings( "unchecked" )
    public <T1 extends Node.Value<T1>, T2 extends Node.Value<T2>> Stream<Select2<T1, T2>> select2() {
        return
            storage.select()
                .flatMap( n1 -> n1.db.values().stream().map(
                    n2 -> new Select2<>( ( T1 ) n1.v, ( T2 ) n2.v ) ) );
    }

    @SuppressWarnings( "unchecked" )
    public <T1 extends Node.Value<T1>, T2 extends Node.Value<T2>, T3 extends Node.Value<T3>> Stream<Select3<T1, T2, T3>> select3() {
        return
            storage.select()
                .flatMap( n1 -> n1.db.values().stream().flatMap(
                    n2 -> n2.db.values().stream().map(
                        n3 -> new Select3<>( ( T1 ) n1.v, ( T2 ) n2.v, ( T3 ) n3.v ) ) ) );
    }

    @SuppressWarnings( "unchecked" )
    public <T1 extends Node.Value<T1>, T2 extends Node.Value<T2>, T3 extends Node.Value<T3>, T4 extends Node.Value<T4>> Stream<Select4<T1, T2, T3, T4>> select4() {
        return
            storage.select()
                .flatMap( n1 -> n1.db.values().stream().flatMap(
                    n2 -> n2.db.values().stream().flatMap(
                        n3 -> n3.db.values().stream().map(
                            n4 -> new Select4<>( ( T1 ) n1.v, ( T2 ) n2.v, ( T3 ) n3.v, ( T4 ) n4.v ) ) ) ) );
    }

    @SuppressWarnings( "unchecked" )
    public <T1 extends Node.Value<T1>, T2 extends Node.Value<T2>, T3 extends Node.Value<T3>, T4 extends Node.Value<T4>, T5 extends Node.Value<T5>> Stream<Select5<T1, T2, T3, T4, T5>> select5() {
        return
            storage.select()
                .flatMap( n1 -> n1.db.values().stream().flatMap(
                    n2 -> n2.db.values().stream().flatMap(
                        n3 -> n3.db.values().stream().flatMap(
                            n4 -> n4.db.values().stream().map(
                                n5 -> new Select5<>( ( T1 ) n1.v, ( T2 ) n2.v, ( T3 ) n3.v, ( T4 ) n4.v, ( T5 ) n5.v ) ) ) ) ) );
    }

    public void clear() {
        storage.deleteAll();
    }

    public static class Database implements Serializable {
        private static final long serialVersionUID = 20816260507748956L;

        public ConcurrentHashMap<String, Node> db;

        public Database() {
        }

        public Database( ConcurrentHashMap<String, Node> db ) {
            this.db = db;
        }
    }

    @ToString
    @AllArgsConstructor
    public static class Select2<T1 extends Node.Value<T1>, T2 extends Node.Value<T2>> {
        public final T1 v1;
        public final T2 v2;
    }

    @ToString
    @AllArgsConstructor
    public static class Select3<T1 extends Node.Value<T1>, T2 extends Node.Value<T2>, T3 extends Node.Value<T3>> implements Serializable {
        private static final long serialVersionUID = 3812951337765151702L;

        public final T1 v1;
        public final T2 v2;
        public final T3 v3;
    }

    @ToString
    @AllArgsConstructor
    public static class Select4<T1 extends Node.Value<T1>, T2 extends Node.Value<T2>, T3 extends Node.Value<T3>, T4 extends Node.Value<T4>> implements Serializable {
        private static final long serialVersionUID = 7466796137360157099L;

        public final T1 v1;
        public final T2 v2;
        public final T3 v3;
        public final T4 v4;
    }

    @ToString
    @AllArgsConstructor
    public static class Select5<T1 extends Node.Value<T1>, T2 extends Node.Value<T2>, T3 extends Node.Value<T3>, T4 extends Node.Value<T4>, T5 extends Node.Value<T5>> implements Serializable {
        private static final long serialVersionUID = -8184723490764842795L;

        public final T1 v1;
        public final T2 v2;
        public final T3 v3;
        public final T4 v4;
        public final T5 v5;
    }
}
