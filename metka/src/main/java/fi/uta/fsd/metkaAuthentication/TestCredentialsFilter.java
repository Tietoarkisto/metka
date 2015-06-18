/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

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
