package io.khaminfo.ppmtool.security;

import java.io.IOException;
import java.lang.System.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.khaminfo.ppmtool.domain.User;
import io.khaminfo.ppmtool.services.CostumUserDetailsService;

public class JWTAuthFilter extends OncePerRequestFilter {
	@Autowired
	private JWTTokenProvider jwtTokenProvider;
	@Autowired
	private CostumUserDetailsService costumUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = getJWTFromRequest(request);
			if (jwtTokenProvider.validateToken(jwt)) {
				Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
				User user = costumUserDetailsService.loadUserbyId(userId);
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, null);
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		} catch (Exception ex) {
			System.out.println("We Could not set token");

		}
		filterChain.doFilter(request, response);

	}

	private String getJWTFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader(SecurityConstants.HEADER_STRING);
		if (bearerToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			return bearerToken.replace(SecurityConstants.TOKEN_PREFIX, "");
		}
		return null;
	}

}
