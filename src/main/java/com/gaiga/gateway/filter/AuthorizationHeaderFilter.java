package com.gaiga.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.net.HttpHeaders;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>{
	
	Environment env;
	
	public AuthorizationHeaderFilter(Environment env) {
		super(Config.class);	//뭔지는 모르겠고, 이거 안하면 실행 시 캐스팅 에러 뜸. 
		this.env = env;
	}
	
	public static class Config {
		
	}
	
	//사용자가 어떠한 요청을 했을 때 헤더에 토큰이 잘 있는지 확인
	@Override
	public GatewayFilter apply(Config config) {
		// Global Pre Filter
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			
			if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
			}
			
			//인증에 문제 없다면, Bearer 토큰을 가져옴. 
			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authorizationHeader.replace("Bearer", "");
			
			//jwt 유효한 것 맞는지 체크
			if(!isJwtValid(jwt)) {
				return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
			}
			
			return chain.filter(exchange);
		};
	}

	private boolean isJwtValid(String jwt) {
		boolean returnValue = true;
		
		//토큰을 문자열로 파싱할 것. 
		String subject = null;
		
		//토큰 생성 시 HS512 로 암호화를 했었음. 흠.. 암튼 이렇게 문자열 추출. 
		try {
			subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
						.parseClaimsJws(jwt).getBody()
						.getSubject();
		} catch (Exception e) {
			returnValue = false;
		}
		
		if(subject == null || subject.isEmpty()) {
			returnValue = false;
		}
		
		return returnValue;
	}

	//해당 프로젝트는 Gateway Service.
	//여기서는 기존에 알고 있는 바와 같이 스프링MVC로 구성하지 않음.
	//스프링 WebFlux라 하여, Functional API 를 쓰며 비동기 방식으로 데이터 처리
	//WebFlux에서 데이터 처리하는 단위 중 하나가 Mono이며 단일값을 반환 (다중은 Flux)
	//클라이언트 요청에 대해 반환해주는 단위. 스프링 5.0부터! 
	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		
		log.error(err);
		return response.setComplete();
	}
	
	
	
}
