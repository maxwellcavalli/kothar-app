package br.com.kotar.web.configuration;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.validation.XmlValidator;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.xml.xsd.XsdSchemaCollection;
import org.xml.sax.SAXException;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
	@Bean
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean(servlet, "/ws/*");
	}

	@Bean(name = "kothar")
	public DefaultWsdl11Definition produtoWsdl11Definition() {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("KotharPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://kotar.com.br/web/soap/schema/kothar");
		wsdl11Definition.setSchemaCollection(getXsdCollection());
		return wsdl11Definition;
	}

	@Bean
	public XsdSchemaCollection getXsdCollection() {
		XsdSchemaCollection collection = new XsdSchemaCollection() {

			@Override
			public XsdSchema[] getXsdSchemas() {
				SimpleXsdSchema _prod = new SimpleXsdSchema(new ClassPathResource("schema/produtos.xsd"));
				SimpleXsdSchema _token = new SimpleXsdSchema(new ClassPathResource("schema/usuario.xsd"));
				SimpleXsdSchema _fornec = new SimpleXsdSchema(new ClassPathResource("schema/fornecedor.xsd"));
				SimpleXsdSchema _cotac = new SimpleXsdSchema(new ClassPathResource("schema/cotacao.xsd"));
				SimpleXsdSchema _common = new SimpleXsdSchema(new ClassPathResource("schema/common.xsd"));

				try {
					_prod.afterPropertiesSet();
					_token.afterPropertiesSet();
					_fornec.afterPropertiesSet();
					_cotac.afterPropertiesSet();
					_common.afterPropertiesSet();
				} catch (ParserConfigurationException | IOException | SAXException e) {
					e.printStackTrace();
				}

				return new XsdSchema[] {_common, _prod, _token, _fornec, _cotac };
			}

			@Override
			public XmlValidator createValidator() {
				return null;
			}
		};

		return collection;
	}
}