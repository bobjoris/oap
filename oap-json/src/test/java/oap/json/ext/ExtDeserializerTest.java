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

package oap.json.ext;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import oap.json.Binder;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings( "unused" )
public class ExtDeserializerTest {
    @Test
    public void ext() {
        Bean aaa = new Bean( new Ext( "aaa" ) );
        String json = "{\"ext\":{\"value\":\"aaa\"}}";
        assertThat( Binder.json.marshal( aaa ) ).isEqualTo( json );
        assertThat( Binder.json.<Bean>unmarshal( Bean.class, json ) )
            .isEqualTo( aaa );
    }

    @EqualsAndHashCode
    @ToString
    public static class Bean {
        @JsonDeserialize( using = Deserializer.class )
        BeanExt ext;

        public Bean() {
        }

        public Bean( BeanExt ext ) {
            this.ext = ext;
        }

        interface BeanExt {}

        public static class Deserializer extends ExtDeserealizer<BeanExt> {
            public Deserializer() {
                super( BeanExt.class );
            }
        }
    }

    @EqualsAndHashCode
    @ToString
    public static class Ext implements Bean.BeanExt {
        String value;

        public Ext() {
        }

        public Ext( String value ) {
            this.value = value;
        }
    }
}
