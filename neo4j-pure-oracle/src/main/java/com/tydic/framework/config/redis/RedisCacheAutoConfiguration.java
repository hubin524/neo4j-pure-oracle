package com.tydic.framework.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.UnknownHostException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.tydic.framework.spring.security.SecurityConstants.LOGIN_USER_CACHE_NAME;
import static com.tydic.framework.spring.security.SecurityConstants.REFRESHED_JWT_TOKEN_CACHE_NAME;
import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019/5/9 15:52
 */
@Configuration
@EnableCaching
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisCacheAutoConfiguration {

	@Value(value = "${redis.cache.ttl.common}")
	private Long commonTtl;

	@Value(value = "${redis.cache.ttl.ticket}")
	private Long ticketTtl;


	@Value("${authorization.jwt.refresh.refreshed-token-cache-time-ms}")
	private Long refreshedTokenCacheTimeMs;

	/**
	 * 缓存key的分隔符.
	 */
	private static final String CACHE_KEY_SEPARATOR = "::";

	@Value("${redis.cache-name.prefix}")
	private String cacheNamePrefix;


	private final CacheKeyPrefix cacheNameAddPrefix = cacheName -> {
		cacheName = cacheNamePrefix + CACHE_KEY_SEPARATOR + cacheName;
		if (!cacheName.endsWith(CACHE_KEY_SEPARATOR)) {
			cacheName += CACHE_KEY_SEPARATOR;
		}
		return cacheName;
	};


	@Bean
	public StringRedisSerializer stringRedisSerializer() {
		return new StringRedisSerializer();
	}

	@Bean
	public Jackson2JsonRedisSerializer jsonRedisSerializer() {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		//当json中出现一些无法识别的属性时，忽略它们;
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		return jackson2JsonRedisSerializer;
	}

	@Bean
	public RedisTemplate<Object, Object> redisTemplate(
			LettuceConnectionFactory redisConnectionFactory) throws UnknownHostException {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setKeySerializer(stringRedisSerializer());
		template.setValueSerializer(jsonRedisSerializer());

		//设置hash Serializer
		template.setHashKeySerializer(stringRedisSerializer());
		template.setHashValueSerializer(jsonRedisSerializer());

		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory redisConnectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setKeySerializer(stringRedisSerializer());
		template.setValueSerializer(stringRedisSerializer());

		//设置hash Serializer
		template.setHashKeySerializer(stringRedisSerializer());
		template.setHashValueSerializer(stringRedisSerializer());
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	@Bean
	public CacheManager cacheManager(LettuceConnectionFactory redisConnectionFactory) {
		RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(commonTtl))
				.disableCachingNullValues()
				.serializeKeysWith(fromSerializer(stringRedisSerializer()))
				.serializeValuesWith(fromSerializer(jsonRedisSerializer())
				).computePrefixWith(cacheNameAddPrefix);


		Map<String, RedisCacheConfiguration> customerCacheConfigurations = new HashMap<>();
		customerCacheConfigurations.put(LOGIN_USER_CACHE_NAME, RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(ticketTtl))
				.disableCachingNullValues()
				.serializeKeysWith(fromSerializer(stringRedisSerializer()))
				.serializeValuesWith(fromSerializer(jsonRedisSerializer()))
				.computePrefixWith(cacheNameAddPrefix)
		);

		customerCacheConfigurations.put(REFRESHED_JWT_TOKEN_CACHE_NAME, RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMillis(refreshedTokenCacheTimeMs))
				.disableCachingNullValues()
				.serializeKeysWith(fromSerializer(stringRedisSerializer()))
				.serializeValuesWith(fromSerializer(stringRedisSerializer()))
				.computePrefixWith(cacheNameAddPrefix)
		);


		RedisCacheManager cacheManager = RedisCacheManager
				.builder(RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory))
				.cacheDefaults(defaultCacheConfig)
				.withInitialCacheConfigurations(customerCacheConfigurations)
				.transactionAware()
				.build();

		return cacheManager;
	}
}

