package org.gaiga.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//이걸 config로 쓰려면, application.yml에 있는 라우팅 설정을 빼주어야 함. 
//configuration 어노테이션을 가장 먼저 모아서 메모리에 올림. 
//RouteLocater 등록하면, 자바코드에 의해 라우팅 처리 가능. (이전에 yml 파일에서 한 거)

//안됨 ㅜㅜ 이유가 뭘까 
//@Configuration
public class FilterConfig {
	// first-service 들어오면 .filters 및 .uri 두가지 작업 해주겠다는 것
	// filter 함수를 거치고 나면 uri로 이동하겠다~ 는 것
	// 람다 함수를 통해 표현. 
//	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		
		return builder.routes()
				.route(r -> r.path("/first-service/**")
							.filters(f -> f.addRequestHeader("first-request", "first-request-header")
											.addResponseHeader("first-response", "first-response-header"))
							.uri("http://localhost:8081"))
				.route(r -> r.path("/second-service/**")
							.filters(f -> f.addRequestHeader("second-request", "second-request-header")
											.addResponseHeader("second-response", "second-response-header"))
							.uri("http://localhost:8082"))
				.build();
	}
}
