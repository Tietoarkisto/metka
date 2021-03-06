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

package fi.uta.fsd.metka.storage.collecting;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.reference.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReferenceCollector {
    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ReferencePathHandler pathHandler;

    // TODO: Remove this request type and only support the path request type
    public Pair<ReturnResult, List<ReferenceOption>> handleReferenceRequest(ReferenceOptionsRequest request) {
        List<ReferenceOption> options = new ArrayList<>();

        Configuration configuration;
        Long start;
        if(request.getConfiguration() == null) {
            start = System.currentTimeMillis();
            Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(ConfigurationType.fromValue(request.getConfType()), request.getConfVersion());
            if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                Logger.error(getClass(), "Reference request made for configuration ["+request.getConfType()+","+request.getConfVersion()+"] that could not be found with result "+configPair.getLeft());
                return new ImmutablePair<>(configPair.getLeft(), options);
            }

            configuration = configPair.getRight();
            if(System.currentTimeMillis()-start > 0) {
                Logger.debug(getClass(), "Getting configuration "+configuration.getKey()+" took "+(System.currentTimeMillis()-start)+"ms");
            }
        } else {
            configuration = request.getConfiguration();
        }

        start = System.currentTimeMillis();

        ReferencePath root = formReferencePath(request.getKey(), request, configuration, null);
        if(root == null) {
            return new ImmutablePair<ReturnResult, List<ReferenceOption>>(ReturnResult.NO_RESULTS, new ArrayList<ReferenceOption>());
        }
        if(root.getReference() == null) {
            // Top value has no reference but has a provided value. This means that the whole stack will collapse to returning the provided value
            ReferenceOption option = new ReferenceOption(root.getValue(), new ReferenceOptionTitle(ReferenceTitleType.LITERAL, root.getValue()));
            options.add(option);
            return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, options);
        }

        if(System.currentTimeMillis()-start > 0) {
            Logger.debug(getClass(), "Forming reference path took "+(System.currentTimeMillis()-start)+"ms");
        }
        start = System.currentTimeMillis();

        root.setConfiguration(configuration);

        Pair<ReturnResult, List<ReferenceOption>> result = pathHandler.handleReferencePath(root, options, request.getLanguage(), request.getReturnFirst());

        if(System.currentTimeMillis()-start > 0) {
            Logger.debug(getClass(), "Handling reference path took "+(System.currentTimeMillis()-start)+"ms");
        }

        return result;
    }

    public Pair<ReturnResult, List<ReferenceOption>> handleReferenceRequest(ReferencePathRequest request) {
        List<ReferenceOption> options = new ArrayList<>();
        return pathHandler.handleReferencePath(request.getRoot(), options, request.getLanguage(), request.getReturnFirst());
    }

    private ReferencePath formReferencePath(String key, ReferenceOptionsRequest request, Configuration configuration, ReferencePath previous) {
        if(previous != null && !StringUtils.hasText(request.getFieldValues().get(key))) {
            return null;
        }
        Field field = configuration.getField(key);
        if(field == null) {
            Logger.error(getClass(), "Field with key ["+key+"] was referenced but could not be found from configuration "+configuration.toString());
            return null;
        }

        Reference reference = getReference(field, configuration);
        if(reference == null && !StringUtils.hasText(request.getFieldValues().get(key))) {
            Logger.error(getClass(), "Field "+field.toString()+" did not contain a reference or reference was not found from "+configuration.toString());
            return null;
        }

        ReferencePath path = new ReferencePath(reference, request.getFieldValues().get(key));
        path.setEmptyEqualsNone(request.getEmptyEqualsNone());
        if(previous != null) {
            // We have a previous value, make sure there's no circular reference
            ReferencePath current = previous;
            // Since IDEA didn't like this as a do - while loop let's do it this way instead
            while(true) {
                if(current.getReference().getKey().equals(path.getReference().getKey())) {
                    // We have circularity, return null;
                    return null;
                }
                if(current.getPrev() == null) {
                    break;
                }
                current = current.getPrev();
            }

            // No circularity, extend path
            path.setNext(previous);
            previous.setPrev(path);
        }
        if(reference != null && reference.getType() == ReferenceType.DEPENDENCY) {
            return formReferencePath(reference.getTarget(), request, configuration, path);
        } else {
            return path;
        }
    }

    private Reference getReference(Field field, Configuration configuration) {
        String referenceKey = null;
        Reference reference = null;
        switch(field.getType()) {
            case SELECTION:
                SelectionList list = configuration.getRootSelectionList(field.getSelectionList());
                if(list != null && list.getType() == SelectionListType.REFERENCE) {
                    referenceKey = list.getReference();
                }
                break;
            case REFERENCE:
            case REFERENCECONTAINER:
                referenceKey = field.getReference();
                break;
        }
        if(referenceKey != null) {
            reference = configuration.getReference(referenceKey);
        }
        return reference;
    }
}
