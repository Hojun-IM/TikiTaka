package com.trillion.tikitaka.global.security.filter;

import static com.trillion.tikitaka.global.security.constant.AuthenticationConstants.*;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.trillion.tikitaka.global.security.jwt.JwtTokenProvider;
import com.trillion.tikitaka.global.security.jwt.JwtTokenRepository;
import com.trillion.tikitaka.global.security.jwt.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

	private final JwtUtil jwtUtil;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtTokenRepository jwtTokenRepository;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
		throws IOException, ServletException {

		doFilter((HttpServletRequest)request, (HttpServletResponse)response, filterChain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws IOException, ServletException {

		String requestUri = request.getRequestURI();
		if (!requestUri.matches(LOGOUT_PATH)) {
			filterChain.doFilter(request, response);
			return;
		}

		log.info("[로그아웃 요청]");
		String requestMethod = request.getMethod();
		if (!requestMethod.equals(LOGOUT_HTTP_METHOD)) {
			log.error("[로그아웃 요청] 잘못된 요청입니다.");
			filterChain.doFilter(request, response);
			return;
		}

		String refreshToken = null;
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			log.error("[로그아웃 요청] 쿠키가 존재하지 않습니다.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(TOKEN_TYPE_REFRESH)) {
				refreshToken = cookie.getValue();
			}
		}

		if (refreshToken == null) {
			log.error("[로그아웃 요청] 리프레시 토큰이 존재하지 않습니다.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			jwtUtil.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			log.error("[로그아웃 요청] 리프레시 토큰이 만료되었습니다.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String type = jwtTokenProvider.getType(refreshToken);
		if (!type.equals(TOKEN_TYPE_REFRESH)) {
			log.error("[로그아웃 요청] 잘못된 리프레시 토큰입니다.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		Boolean isRefreshTokenExist = jwtTokenRepository.existsByRefreshToken(refreshToken);
		if (!isRefreshTokenExist) {
			log.error("[로그아웃 요청] 리프레시 토큰이 존재하지 않습니다.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		jwtTokenRepository.deleteByRefreshToken(refreshToken);

		Cookie cookie = new Cookie(TOKEN_TYPE_REFRESH, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");

		response.addCookie(cookie);
		response.setStatus(HttpServletResponse.SC_OK);
		log.info("[로그아웃 요청] 완료: {}", jwtTokenProvider.getUsername(refreshToken));
	}
}
