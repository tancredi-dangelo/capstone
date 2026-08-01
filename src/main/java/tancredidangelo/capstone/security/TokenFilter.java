
package tancredidangelo.capstone.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.account.stack.AccountService;
import tancredidangelo.capstone.exceptions.UnauthorizedException;

import java.io.IOException;

@Component
public class TokenFilter extends OncePerRequestFilter {

    /// dependency injection
    private final JWTTools jwtTools;
    private final AccountService accountService;
    private final HandlerExceptionResolver resolver;

    public TokenFilter(JWTTools jwtTools, AccountService accountService, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtTools = jwtTools;
        this.accountService = accountService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                throw new UnauthorizedException("Token is missing or malformed.");
            }
            // extract and verify token
            String token = header.replace("Bearer ", "");
            this.jwtTools.verifyToken(token);

            // extract id
            Long accountId = this.jwtTools.extractId(token);

            // add id to AuthenticationPrincipal
            Account authUser = this.accountService.findById(accountId);
            Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());

            System.out.println("DEBUG: Setting authentication for account " + authUser.getUsername() + " with authorities " + authUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ex) {

            resolver.resolveException(request, response, null, ex);
            return;
        }

        filterChain.doFilter(request, response);
    }



    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new AntPathMatcher().match("/auth/**", request.getServletPath());
    }

}



