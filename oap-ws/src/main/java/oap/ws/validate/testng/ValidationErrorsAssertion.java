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
package oap.ws.validate.testng;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import oap.json.Binder;
import oap.reflect.Reflect;
import oap.reflect.Reflection;
import oap.util.Stream;
import oap.util.Throwables;
import oap.ws.validate.ValidationErrors;
import oap.ws.validate.Validators;
import org.assertj.core.api.AbstractAssert;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static oap.util.Pair.__;
import static org.assertj.core.api.Assertions.assertThat;

public class ValidationErrorsAssertion extends AbstractAssert<ValidationErrorsAssertion, ValidationErrors> {

    protected ValidationErrorsAssertion( ValidationErrors actual ) {
        super( actual, ValidationErrorsAssertion.class );
    }


    public static ValidationErrorsAssertion assertValidationErrors( ValidationErrors actual ) {
        return new ValidationErrorsAssertion( actual );
    }

    public static <I> ValidatedInvocation<I> validating( I instance ) {
        return new ValidatedInvocation<>( instance );
    }

    public ValidationErrorsAssertion hasCode( int code ) {
        assertThat( this.actual.code ).isEqualTo( code );
        return this;
    }

    public ValidationErrorsAssertion containsErrors( String... errors ) {
        assertThat( this.actual.errors ).contains( errors );
        return this;
    }

    public ValidationErrorsAssertion isError( int code, String error ) {
        return hasCode( code ).containsErrors( error );
    }

    public ValidationErrorsAssertion isFailed() {
        assertThat( this.actual.failed() )
            .withFailMessage( "should contain errors" )
            .isTrue();
        return this;
    }

    public ValidationErrorsAssertion isNotFailed() {
        assertThat( this.actual.failed() )
            .withFailMessage( "shouldn't contain errors but contain: " + this.actual )
            .isFalse();
        return this;
    }

    public static class ValidatedInvocation<I> {
        private final I proxy;
        private List<Function<ValidationErrorsAssertion, ValidationErrorsAssertion>> assertions = new ArrayList<>();

        @SuppressWarnings( "unchecked" )
        public ValidatedInvocation( I instance ) {
            try {
                var factory = new ProxyFactory();
                factory.setSuperclass( instance.getClass() );

                MethodHandler handler = ( self, jmethod, proceed, args ) -> {
                    Optional<Reflection.Method> methodOpt = Reflect.reflect( instance.getClass() )
                        .method( jmethod );
                    if( !methodOpt.isPresent() ) throw new NoSuchMethodError( jmethod.toString() );
                    return methodOpt
                        .map( method -> {
                            ValidationErrors paramErrors = ValidationErrors.empty();

                            List<Reflection.Parameter> parameters = method.parameters;

                            Map<Reflection.Parameter, Object> originalValues = Stream.of( parameters )
                                .zipWithIndex()
                                .<Reflection.Parameter, Object>map( ( parameter, i ) -> __( parameter, Binder.json.marshal( args[i] ) ) )
                                .toMap();

                            paramErrors.validateParameters( originalValues, method, instance, true );

                            if( paramErrors.failed() ) {
                                runAsserts( paramErrors );
                                return null;
                            }

                            LinkedHashMap<Reflection.Parameter, Object> values = new LinkedHashMap<>();

                            for( int i = 0; i < parameters.size(); i++ ) values.put( parameters.get( i ), args[i] );

                            paramErrors.validateParameters( values, method, instance, false );

                            if( paramErrors.failed() ) {
                                runAsserts( paramErrors );
                                return null;
                            }

                            ValidationErrors methodErrors = Validators
                                .forMethod( method, instance, false )
                                .validate( args, values );
                            runAsserts( methodErrors );
                            if( methodErrors.failed() ) return null;

                            return method.invoke( instance, args );
                        } )
                        .orElse( null );
                };

                proxy = ( I ) factory.create( new Class<?>[0], new Object[0], handler );
            } catch( NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e ) {
                throw Throwables.propagate( e );
            }
        }

        public ValidatedInvocation<I> hasCode( int code ) {
            assertions.add( a -> a.hasCode( code ) );
            return this;
        }

        public ValidatedInvocation<I> containsErrors( String... errors ) {
            assertions.add( a -> a.containsErrors( errors ) );
            return this;
        }

        public ValidatedInvocation<I> isError( int code, String error ) {
            return hasCode( code ).containsErrors( error );
        }

        public ValidatedInvocation<I> isFailed() {
            assertions.add( ValidationErrorsAssertion::isFailed );
            return this;
        }

        public ValidatedInvocation<I> isNotFailed() {
            assertions.add( ValidationErrorsAssertion::isNotFailed );
            return this;
        }

        private void runAsserts( ValidationErrors errors ) {
            ValidationErrorsAssertion assertion = ValidationErrorsAssertion.assertValidationErrors( errors );
            assertions.forEach( f -> f.apply( assertion ) );
        }

        public I build() {
            return proxy;
        }
    }
}
