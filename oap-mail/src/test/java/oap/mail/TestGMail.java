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
package oap.mail;

import oap.storage.IdentifierBuilder;
import oap.storage.MemoryStorage;

import static oap.storage.Storage.LockStrategy.Lock;

public class TestGMail {

    public static void main( String[] args ) throws MailException {
        Mailman queue = new Mailman( "smtp.gmail.com", 587, true, "", "",
            new MemoryStorage<>( IdentifierBuilder.<Message>identify( m -> m.id, ( m, id ) -> m.id = id ).build(), Lock ) );
        Message message = Template.of( "/xjapanese" ).get().buildMessage();
        message.setFrom( MailAddress.of( "Україна", "vladimir.kirichenko@gmail.com" ) );
        message.setTo( MailAddress.of( "Little Green Mail", "vova@qupletech.com" ) );
        queue.send( message );
    }
}
