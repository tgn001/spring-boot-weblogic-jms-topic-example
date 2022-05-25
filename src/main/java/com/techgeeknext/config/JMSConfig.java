package com.techgeeknext.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;

import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Properties;


@Configuration
@EnableJms
public class JMSConfig {

	@Value("${weblogic.jms.url}")
	private String jmsUrl;

	@Value("${weblogic.jms.username}")
	private String jmsUser;

	@Value("${weblogic.jms.password}")
	private String jmsPassword;

	@Value("${weblogic.jms.clientId}")
	private String clientId;

	@Bean
	public DefaultJmsListenerContainerFactory empJmsContFactory()
			throws NamingException {

		DefaultJmsListenerContainerFactory containerFactory =
				new DefaultJmsListenerContainerFactory();

		JndiDestinationResolver jndiDestinationResolver =
				new JndiDestinationResolver();

		jndiDestinationResolver.setJndiEnvironment(getJNDIProperties());

		containerFactory.setDestinationResolver(jndiDestinationResolver);


		containerFactory.setPubSubDomain(true);
		containerFactory.setConnectionFactory(connectionFactory());
		containerFactory.setSubscriptionDurable(true);
		return containerFactory;
	}

	@Bean
	public CachingConnectionFactory connectionFactory() throws NamingException{
		CachingConnectionFactory factory = new CachingConnectionFactory();
		//get the jndi connection values
		ConnectionFactory connectionFactory = (ConnectionFactory)jndiConnectionFactory().getObject();

		factory.setTargetConnectionFactory(connectionFactory);
		factory.setClientId(clientId);
		return factory;
	}

	public JndiObjectFactoryBean jndiConnectionFactory() throws NamingException {
		JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
		jndiObjectFactoryBean.setJndiTemplate(jndiTemplate());
		jndiObjectFactoryBean.setJndiName("techgeeknextFactory");
		jndiObjectFactoryBean.afterPropertiesSet();
		return jndiObjectFactoryBean;
	}


	private Properties getJNDIProperties(){
		final Properties jndiProps = new Properties();
		jndiProps.setProperty(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		jndiProps.setProperty(Context.PROVIDER_URL, jmsUrl);
		if (jmsUser != null && !jmsUser.isEmpty()) {
			jndiProps.setProperty(Context.SECURITY_PRINCIPAL, jmsUser);
		}
		if (jmsPassword != null && !jmsPassword.isEmpty()) {
			jndiProps.setProperty(Context.SECURITY_CREDENTIALS, jmsPassword);
		}
		return jndiProps;
	}

	@Bean
	public JndiTemplate jndiTemplate() {
		JndiTemplate jndiTemplate = new JndiTemplate();
		jndiTemplate.setEnvironment(getJNDIProperties());
		return jndiTemplate;
	}
}
