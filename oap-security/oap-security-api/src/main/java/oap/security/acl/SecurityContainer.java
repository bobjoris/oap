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

package oap.security.acl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.google.common.base.Preconditions;
import lombok.ToString;
import oap.json.TypeIdFactory;
import oap.util.Id;
import oap.util.IdBean;

import java.io.Serializable;

/**
 * Created by igor.petrenko on 10.01.2018.
 */
@ToString
public class SecurityContainer<T extends IdBean> implements Serializable {
    private static final long serialVersionUID = 8477473083613051099L;
    public AclObject acl;
    public String id;
    @JsonTypeIdResolver( TypeIdFactory.class )
    @JsonTypeInfo( use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "object:type" )
    public T object;

    @JsonCreator
    public SecurityContainer( String id, T object, AclObject acl ) {
        Preconditions.checkNotNull( object );
        Preconditions.checkNotNull( acl );

        this.id = id;
        this.acl = acl;
        this.object = object;

        this.acl.id = this.id;
        this.object.setId( this.id );
    }

    public SecurityContainer( T object, AclObject acl ) {
        this( null, object, acl );
    }

    @Id
    public String getId() {
        return id;
    }

    @Id
    public void setId( String id ) {
        this.id = id;
        this.object.setId( id );
        this.acl.id = id;
    }
}
