package com.simply.Cinema.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RediesConfig {

    @Value("${REDIS_HOST}")
    private String redisHost;

    @Value("${REDIS_PORT}")
    private int redisPort;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//
//        //connection Factory
//        template.setConnectionFactory(redisConnectionFactory());
//
//        //key serializer
//        template.setKeySerializer(new StringRedisSerializer());
//
//        // value serializer
//        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        return template;
//    }

     @Bean
     public RedisTemplate<String, Object> redisTemplate() {

          RedisTemplate<String, Object> template = new RedisTemplate<>();
          template.setConnectionFactory(redisConnectionFactory());

          // 🔥 Custom ObjectMapper with JavaTimeModule
          ObjectMapper mapper = new ObjectMapper();
          mapper.registerModule(new JavaTimeModule());
          mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO format

          // Apply to serializer
          GenericJackson2JsonRedisSerializer serializer =
                  new GenericJackson2JsonRedisSerializer(mapper);

          // 🔐 Key & Value serializers
          template.setKeySerializer(new StringRedisSerializer());
          template.setValueSerializer(serializer);
          template.setHashKeySerializer(new StringRedisSerializer());
          template.setHashValueSerializer(serializer);

          template.afterPropertiesSet();
          return template;
     }


}
