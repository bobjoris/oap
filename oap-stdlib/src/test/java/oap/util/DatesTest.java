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

package oap.util;

import oap.testng.Fixtures;
import oap.testng.SystemTimerFixture;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.testng.annotations.Test;

import static oap.testng.Asserts.assertString;
import static oap.util.Dates.d;
import static oap.util.Dates.h;
import static oap.util.Dates.m;
import static oap.util.Dates.s;
import static oap.util.Dates.w;
import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.DateTimeZone.UTC;

public class DatesTest extends Fixtures {
    {
        fixture( SystemTimerFixture.FIXTURE );
    }

    @Test
    public void parseIsoDate() {
        assertThat( Dates.parseDateWithMillis( "2016-01-01T00:00:00.000" ).successValue )
            .isEqualTo( new DateTime( 2016, 1, 1, 0, 0, 0, UTC ) );
        assertThat( Dates.parseDate( "2016-01-01T00:00:00" ).successValue )
            .isEqualTo( new DateTime( 2016, 1, 1, 0, 0, 0, UTC ) );
    }

    @Test
    public void currentTimeDay() {
        DateTimeUtils.setCurrentMillisFixed( new DateTime( 1970, 1, 1, 0, 0, UTC ).getMillis() );
        assertThat( Dates.currentTimeDay() ).isEqualTo( 0 );

        DateTimeUtils.setCurrentMillisFixed( new DateTime( 1970, 2, 10, 0, 0, UTC ).getMillis() );
        assertThat( Dates.currentTimeDay() ).isEqualTo( 40 );
    }

    @Test
    public void testDurationToString() {
        assertString( Dates.durationToString( 1 ) ).isEqualTo( "0.001s" );
        assertString( Dates.durationToString( 1000 ) ).isEqualTo( "1s" );
        assertString( Dates.durationToString( 1001 ) ).isEqualTo( "1.001s" );
        assertString( Dates.durationToString( m( 2 ) + s( 4 ) + 567 ) ).isEqualTo( "2m 4.567s" );
        assertString( Dates.durationToString( h( 4 ) + m( 2 ) + s( 4 ) + 567 ) ).isEqualTo( "4h 2m 4.567s" );
        assertString( Dates.durationToString( d( 6 ) + h( 4 ) + m( 2 ) + s( 4 ) + 567 ) ).isEqualTo( "6d 4h 2m 4.567s" );
        assertString( Dates.durationToString( w( 4 ) + d( 6 ) + h( 4 ) + m( 2 ) + s( 4 ) + 567 ) )
            .isEqualTo( "4w 6d 4h 2m 4.567s" );

        assertString( Dates.durationToString( w( 1 ) + m( 5 ) ) ).isEqualTo( "1w 5m" );
    }
}
