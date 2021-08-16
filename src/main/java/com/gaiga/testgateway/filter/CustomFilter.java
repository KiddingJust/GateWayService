package com.gaiga.testgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config>{

	//생성자부터 
	public CustomFilter() {
		super(Config.class);
	}
	
	@Override
	public GatewayFilter apply(Config config) {
		// Custom Pre Filter
		//exchange 와 chain에 대한 건, OrderedGatewayFilter 클래스에서 확인 가능. 그건 ctrl+클릭 해서 봐봐 
		//해당 람다식은 LoggingFilter.java에서 풀어줌. 
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();
			
			log.info("Custom Pre filter: request id -> {}", request.getId());
			
			//Custom Post Filter
			return chain.filter(exchange).then(Mono.fromRunnable(()-> {
				log.info("Custom Post filter: response code -> {}", response.getStatusCode());
			}));
		};
	}


	public static class Config{
		//put configuration properties
	}
}
