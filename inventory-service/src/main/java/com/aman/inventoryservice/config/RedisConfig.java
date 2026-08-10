package com.aman.inventoryservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisConfig {

	@Bean
	public DefaultRedisScript<Long> releaseInventoryScript() {

		DefaultRedisScript<Long> script = new DefaultRedisScript<>();
		script.setLocation(
				new ClassPathResource(
						"scripts/release_inventory.lua"
				)
		);
		script.setResultType(Long.class);
		return script;
	}

}
