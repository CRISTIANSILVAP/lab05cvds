package edu.eci.cvds.project.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.security.KeyStore;

@Configuration
public class RestTemplateConfig {
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            // Conector HTTP en el puerto 8080
            Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
            connector.setScheme("http");
            connector.setSecure(false);
            connector.setPort(8080);
            connector.setRedirectPort(8443);// Redirige a HTTPS
//            // Configuración del certificado SSL (ejemplo)
//            connector.setAttribute("keystoreFile", "path/to/keystore");
//            connector.setAttribute("keystorePass", "yourKeystorePassword");
//            connector.setAttribute("keyAlias", "yourKeyAlias");

            factory.addAdditionalTomcatConnectors(connector);
        };
    }

}
