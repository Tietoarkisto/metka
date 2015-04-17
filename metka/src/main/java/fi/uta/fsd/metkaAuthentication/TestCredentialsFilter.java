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

        //setReaderAttributes(request);
        //setUserAttributes(request);
        //setTranslatorAttributes(request);
        setAdminAttributes(request);

        filterChain.doFilter(request, response);
    }

    private void setUnknownAttributes(HttpServletRequest request) {
        setRequestAttributes(request, "unknown", "Tuntematon", "metka:unk");
    }

    private void setReaderAttributes(HttpServletRequest request) {
        setRequestAttributes(request, "reader", "Luku Pena", "metka:reader");
    }

    private void setUserAttributes(HttpServletRequest request) {
        setRequestAttributes(request, "user", "Perus Pena", "metka:basic-user");
    }

    private void setTranslatorAttributes(HttpServletRequest request) {
        setRequestAttributes(request, "translator", "Käännös Pena", "metka:translator");
    }

    private void setDataAdminAttributes(HttpServletRequest request) {
        setRequestAttributes(request, "data-admin", "Data Pena", "metka:data-administrator");
    }

    private void setAdminAttributes(HttpServletRequest request) {
        setRequestAttributes(request, "admin", "Admin Pena", "metka:administrator");
    }

    private void setRequestAttributes(HttpServletRequest request, String user, String name, String role) {
        request.setAttribute("Shib-Session-ID", "Metka-session-"+RANDOM.nextInt(Integer.MAX_VALUE));

        request.setAttribute("Shib-UserName", user);
        request.setAttribute("Shib-DisplayName", name);

        request.setAttribute("Shib-Roles", role);
    }
}
