package fi.uta.fsd.metkaAuthentication;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

public class TestCredentialsFilter extends OncePerRequestFilter {
    private static final Random RANDOM = new Random();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        request.setAttribute("Shib-Session-ID", "Metka-session-"+RANDOM.nextInt(Integer.MAX_VALUE));


        request.setAttribute("Shib-UserName", "Metka-test-user-"/*+RANDOM.nextInt(100)*/);
        request.setAttribute("Shib-DisplayName", "Virpi Varis");

        request.setAttribute("Shib-Roles", "metka:basic-user");

        filterChain.doFilter(request, response);
    }
}
