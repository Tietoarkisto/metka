package fi.uta.fsd.metkaAuthentication;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TestCredentialsFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        request.setAttribute("Shib-Session-Id", "123");
        request.setAttribute("Shib-UserName", "test");
        request.setAttribute("Shib-FirstName", "Virpi");
        request.setAttribute("Shib-LastName", "Varis");
        request.setAttribute("Shib-Roles", "admin,tester");

        filterChain.doFilter(request, response);
    }
}
