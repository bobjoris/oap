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

package oap.message;

import lombok.ToString;
import oap.util.ByteSequence;
import org.joda.time.DateTimeUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

class Messages {
    final LinkedBlockingQueue<MessageInfo> ready = new LinkedBlockingQueue<>();
    final LinkedBlockingQueue<RetryInfo> retry = new LinkedBlockingQueue<>();
    final ConcurrentHashMap<ByteSequence, MessageInfo> inProgress = new ConcurrentHashMap<>();

    public void add( Message message ) {
        ready.add( new MessageInfo( message, DateTimeUtils.currentTimeMillis() ) );
    }

    public void add( MessageInfo messageInfo ) {
        ready.add( messageInfo );
    }

    public void retry( MessageInfo messageInfo, long time ) {
        retry.add( new RetryInfo( messageInfo, DateTimeUtils.currentTimeMillis() + time ) );

    }

    public MessageInfo poll( long timeoutMs ) throws InterruptedException {
        MessageInfo messageInfo = ready.poll( timeoutMs, TimeUnit.MILLISECONDS );
        if( messageInfo != null ) inProgress.put( messageInfo.message.md5, messageInfo );
        return messageInfo;
    }

    public MessageInfo poll() {
        return ready.poll();
    }

    @SuppressWarnings( "checkstyle:OverloadMethodsDeclarationOrder" )
    public void retry() {
        var now = DateTimeUtils.currentTimeMillis();

        var it = retry.iterator();
        while( it.hasNext() ) {
            var retry = it.next();

            if( retry.startTime < now ) {
                it.remove();
                add( retry.messageInfo );
            }
        }
    }

    public boolean isEmpty() {
        return ready.isEmpty();
    }

    public void clear() {
        ready.clear();
    }

    public RetryInfo pollRetry() {
        return retry.poll();
    }

    public int getReadyMessages() {
        return ready.size();
    }

    public int getRetryMessages() {
        return retry.size();
    }

    public MessageInfo pollInProgress() {
        try {
            Iterator<Map.Entry<ByteSequence, MessageInfo>> iterator = inProgress.entrySet().iterator();
            MessageInfo messageInfo = null;
            if( iterator.hasNext() ) {
                messageInfo = iterator.next().getValue();
                iterator.remove();
            }
            return messageInfo;
        } catch( NoSuchElementException e ) {
            return null;
        }
    }

    public void removeInProgress( MessageInfo messageInfo ) {
        inProgress.remove( messageInfo.message.md5 );
    }

    @ToString
    static class MessageInfo {
        public final Message message;
        public final long startTime;
        public int retryCount;

        MessageInfo( Message message, long startTime ) {
            this.message = message;
            this.startTime = startTime;
            this.retryCount = 0;
        }
    }

    @ToString
    static class RetryInfo {
        public final MessageInfo messageInfo;
        public final long startTime;

        RetryInfo( MessageInfo messageInfo, long startTime ) {
            this.messageInfo = messageInfo;
            this.startTime = startTime;
        }
    }
}
