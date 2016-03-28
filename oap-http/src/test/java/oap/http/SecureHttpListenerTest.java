package oap.http;

import oap.io.IoStreams;
import oap.testng.Env;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

import static oap.io.IoStreams.Encoding.PLAIN;
import static oap.testng.Asserts.pathOfTestResource;
import static org.testng.Assert.assertEquals;

public class SecureHttpListenerTest {

   private static String KEYSTORE_PASSWORD = "123456";

   private Server server = new Server( 10 );

   @BeforeTest
   public void setUp() {
      server.bind( "test", Cors.DEFAULT, ( request, response ) -> {

         System.out.println( "Base URL " + request.baseUrl );
         System.out.println( "Headers:" );

         for( Header header : request.headers ) {
            System.out.println( header.getName() + " " + header.getValue() );
         }

         response.respond( new HttpResponse( 200 ) );
      }, Protocol.HTTPS );

      new Thread( new SecureHttpListener( server, pathOfTestResource( getClass(), "server_keystore.jks" ),
         KEYSTORE_PASSWORD, Env.port() ) ).start();
   }

   @Test
   public void testShouldVerifySSLCommunication() throws KeyStoreException, IOException, CertificateException,
      NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException {

      KeyStore keyStore = KeyStore.getInstance( "JKS" );
      keyStore.load( IoStreams.in( pathOfTestResource( getClass(), "client_truststore.jks" ), PLAIN ),
         KEYSTORE_PASSWORD.toCharArray() );

      TrustManagerFactory trustManagerFactory =
         TrustManagerFactory.getInstance( TrustManagerFactory
            .getDefaultAlgorithm() );
      trustManagerFactory.init( keyStore );

      SSLContext sslContext = SSLContext.getInstance( "TLS" );
      sslContext.init( null, trustManagerFactory.getTrustManagers(), null );

      CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().setSSLContext( sslContext ).build();

      HttpGet httpGet = new HttpGet( "https://localhost:" + Env.port() + "/test/" );

      CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute( httpGet );

      assertEquals( closeableHttpResponse.getStatusLine().getStatusCode(), 200 );
   }

   @AfterTest
   public void tearDown() {
      server.stop();
   }
}
