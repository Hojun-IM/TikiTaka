package com.trillion.tikitaka.global.security.filter;

import static com.trillion.tikitaka.global.security.constant.AuthenticationConstants.*;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.trillion.tikitaka.domain.member.infrastructure.MemberRepository;
import com.trillion.tikitaka.global.exception.ErrorCode;
import com.trillion.tikitaka.global.security.jwt.JwtTokenProvider;
import com.trillion.tikitaka.global.security.jwt.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;

	@Override
	protected void doFilterInternal(
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {

		try {
			String accessToken = request.getHeader(TOKEN_HEADER);
			if (accessToken == null || !accessToken.startsWith(TOKEN_PREFIX)) {
				filterChain.doFilter(request, response);
				return;
			}

			accessToken = accessToken.substring(7).trim();
			validateTokenExpirationAndType(accessToken);

			String username = jwtTokenProvider.getUsername(accessToken);
			String role = jwtTokenProvider.getRole(accessToken);
			SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

			UsernamePasswordAuthenticationToken authToken =
				new UsernamePasswordAuthenticationToken(username, null, List.of(authority));
			SecurityContextHolder.getContext().setAuthentication(authToken);

			filterChain.doFilter(request, response);
		} catch (MalformedJwtException e) {
			log.error("[JWT 필터] 토큰 검증 실패: 잘못된 토큰 형식");
			request.setAttribute("JWT_ERROR_CODE", ErrorCode.INVALID_TOKEN);
			throw new MalformedJwtException("잘못된 토큰 형식");
		} catch (ExpiredJwtException e) {
			log.error("[JWT 필터] 토큰 검증 실패: 만료된 토큰");
			request.setAttribute("JWT_ERROR_CODE", ErrorCode.EXPIRED_TOKEN);
			throw new ExpiredJwtException(null, null, "만료된 토큰");
		} catch (SignatureException e) {
			log.error("[JWT 필터] 토큰 검증 실패: 잘못된 서명");
			request.setAttribute("JWT_ERROR_CODE", ErrorCode.INVALID_SIGNATURE);
			throw new SignatureException("잘못된 서명");
		} catch (Exception e) {
			log.error("[JWT 필터] 토큰 검증 실패: 인증 실패");
			request.setAttribute("JWT_ERROR_CODE", ErrorCode.INTERNAL_SERVER_ERROR);
			throw new InsufficientAuthenticationException("인증 실패");
		}
	}

	private void validateTokenExpirationAndType(String token) {
		if (jwtUtil.isExpired(token)) {
			log.error("[JWT 필터] 토큰 검증 실패: 만료된 토큰");
			throw new ExpiredJwtException(null, null, "만료된 토큰");
		}
		String type = jwtTokenProvider.getType(token);
		if (!TOKEN_TYPE_ACCESS.equals(type)) {
			log.error("[JWT 필터] 토큰 검증 실패: 잘못된 토큰 타입");
			throw new MalformedJwtException("잘못된 토큰 타입");
		}
	}
}
