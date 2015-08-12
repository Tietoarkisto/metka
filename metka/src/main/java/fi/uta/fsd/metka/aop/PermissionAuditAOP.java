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

package fi.uta.fsd.metka.aop;

import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAmqp.payloads.AuditPayload;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.AccessDeniedException;

@Aspect
public class PermissionAuditAOP {

    @Autowired
    private Messenger messenger;



    // Audit message for user trying to remove a saved search that they don't have right to remove,
    // This is an example since it shouldn't be possible from the client without disabling row filtering on the saved searches container.
    // Search for "NOTE: Change to return true to test denied audit message" to find the place where change needs to be done to test this.
    @AfterThrowing(pointcut = "execution(* fi.uta.fsd.metka.mvc.controller.ExpertSearchController.removeExpertSearch(..)) && args(id)",
        throwing = "ex")
    public void removeExpertSearchDenied(JoinPoint jp, AccessDeniedException ex, Long id) {
        messenger.sendAmqpMessage(messenger.FA_AUDIT, AuditPayload.deny("Käyttäjä [" + AuthenticationUtil.getUserName()+ "] yritti poistaa eksperttihaun: "+id));
    }

    // Audit message for user having successfully removed a saved search
    @AfterReturning(pointcut = "execution(* fi.uta.fsd.metka.mvc.services.ExpertSearchService.removeExpertSearch(..)) && args(id)")
    public void removeExpertSearchAllowed(Long id) {
        messenger.sendAmqpMessage(messenger.FA_AUDIT, AuditPayload.allow("Käyttäjä [" + AuthenticationUtil.getUserName() + "] poisti eksperttihaun: " + id));
    }
}