package com.example.SpringMessage2;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@SpringBootApplication
public class MessagingRedisApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessagingRedisApplication.class);

	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,MessageListenerAdapter listenerAdapter)
	{
		LOGGER.info("bean3..");
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("chat"));
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		LOGGER.info("bean2..");
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	@Bean
	Receiver receiver() {
		LOGGER.info("bean1..");
		return new Receiver();
	}

	@Bean
	StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
		LOGGER.info("bean4...");
		return new StringRedisTemplate(connectionFactory);
	}

	public static void main(String[] args) throws InterruptedException {
		LOGGER.info("main.");
		ApplicationContext ctx = SpringApplication.run(MessagingRedisApplication.class, args);
		
		StringRedisTemplate template = ctx.getBean(StringRedisTemplate.class);
		Receiver receiver = ctx.getBean(Receiver.class);
		LOGGER.info("Sending message 1...");

		while (receiver.getCount() == 0) {

			LOGGER.info("Sending message...");
			template.convertAndSend("chat", "Hello from Redis  2!");
			
			Thread.sleep(50000L);
			
			template.convertAndSend("chat", "Hello from Redis  2 !");
			Thread.sleep(50000L);
		}

		System.exit(0);
	}
}