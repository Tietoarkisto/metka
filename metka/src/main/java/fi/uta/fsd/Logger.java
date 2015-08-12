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

package fi.uta.fsd;

import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class Logger {
    private static final Map<Class<?>, org.slf4j.Logger> loggers = new HashMap<>();

    private static org.slf4j.Logger getLogger(Class<?> c) {
        if(!loggers.containsKey(c)) {
            loggers.put(c, LoggerFactory.getLogger(c));
        }
        return loggers.get(c);
    }

    public static void error(Class<?> c, String message) {
        getLogger(c).error(message);
    }

    public static void error(Class<?> c, String message, Exception e) {
        getLogger(c).error(message, e);
    }

    public static void warning(Class<?> c, String message) {
        getLogger(c).warn(message);
    }

    public static void warning(Class<?> c, String message, Exception e) {
        getLogger(c).warn(message, e);
    }

    public static void info(Class<?> c, String message) {
        getLogger(c).info(message);
    }

    public static void info(Class<?> c, String message, Exception e) {
        getLogger(c).info(message, e);
    }

    public static void debug(Class<?> c, String message) {
        getLogger(c).debug(message);
    }

    public static void debug(Class<?> c, String message, Exception e) {
        getLogger(c).debug(message, e);
    }
}
