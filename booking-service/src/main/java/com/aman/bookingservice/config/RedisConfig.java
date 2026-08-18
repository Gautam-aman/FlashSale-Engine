package com.aman.bookingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisConfig {


	@Bean
	public DefaultRedisScript<Long> reserveInventoryScript() {
		String script = """
                local stock = redis.call('GET', KEYS[1])

                if not stock then
                    return -1
                end

                if tonumber(stock) < tonumber(ARGV[1]) then
                    return 0
                end

                redis.call('DECRBY', KEYS[1], ARGV[1])

                return 1
                """;

		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
		redisScript.setScriptText(script);
		redisScript.setResultType(Long.class);
		return redisScript;
	}

	@Bean
	LettuceConnectionFactory connectionFactory() {
		return new LettuceConnectionFactory();
	}

	@Bean
	StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}


}
