package org.example.smidatesttask.config;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.service.JwtService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * class for jwt token filtering
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * creates a filter function to process incoming requests.
     *
     * @param request - the request from the user.
     * @param response - the response to the user.
     * @param filterChain - the filter chain.
     * @throws ServletException if an error occurs related to servlets.
     * @throws IOException if an error occurs related to input/output operations.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String userEmail;
        String jwt = null;

        /* if you use bearer from cookies */
        final Cookie[] authCookies = request.getCookies();

        if (authCookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        for (var cookie : authCookies) {
            if (cookie.getName().equals("bearer")) {
                jwt = cookie.getValue();
                break;
            }
        }

        if (jwt == null || jwt.isEmpty() || jwt.equals("null")) {
            filterChain.doFilter(request, response);
            return;
        }

        // get email
        userEmail = jwtService.extractUsername(jwt);

        // check token and user data
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
