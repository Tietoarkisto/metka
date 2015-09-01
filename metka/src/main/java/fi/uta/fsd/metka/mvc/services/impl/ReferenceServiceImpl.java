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

package fi.uta.fsd.metka.mvc.services.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.general.ApproveInfo;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.collecting.ReferenceCollector;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.reference.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Contains communication pertaining to Reference objects
 */
@Service
public class ReferenceServiceImpl implements ReferenceService {

    @Autowired
    private ReferenceCollector references;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    /**
     * Returns the current option depicting the value and title of given reference.
     * This is mainly used for indexing to get the actual text that needs to be indexed.
     * This works only for reference fields and reference selections, reference containers
     * are handled differently.
     * @param data
     * @param path
     * @return
     */
    // TODO: We should try to move away from this
    @Override public ReferenceOption getCurrentFieldOption(Language language, RevisionData data, Configuration configuration, String path, Boolean emptyEqualsNone) {
        // TODO: Gather dependencies, form request and return single result
        Long start;
        if(configuration == null) {
            start = System.currentTimeMillis();
            Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(data.getConfiguration());
            if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                Logger.error(getClass(), "Couldn't find configuration for " + data.toString());
                return null;
            }
            configuration = configPair.getRight();

            Logger.debug(getClass(), "Getting configuration "+data.getConfiguration()+" took "+(System.currentTimeMillis()-start)+"ms");
        }

        start = System.currentTimeMillis();

        String[] splits = path.split("\\.");
        if(splits.length == 0) {
            splits = new String[1];
            splits[0] = path;
        }
        // Check that the final path element points to a field that can be a reference with a value, deal with reference containers separately with a different call
        Field field = configuration.getField(splits[splits.length-1]);
        if(field == null) {
            return null;
        }
        if(!(field.getType() == FieldType.REFERENCE || field.getType() == FieldType.SELECTION)) {
            return null;
        }
        if(field.getType() == FieldType.SELECTION) {
            SelectionList list = configuration.getRootSelectionList(field.getSelectionList());
            if(list.getType() != SelectionListType.REFERENCE) {
                return null;
            }
        }

        ReferenceOptionsRequest request = null;

        List<String> dependencyStack = formDependencyStack(field, configuration);

        // Let's form a request that we can use to fetch a reference option
        request = formReferenceOptionsRequest(language, splits, dependencyStack, data, configuration, emptyEqualsNone);

        Logger.debug(getClass(), "Forming request took "+(System.currentTimeMillis()-start)+"ms");
        start = System.currentTimeMillis();

        // Perform the request
        List<ReferenceOption> options = references.handleReferenceRequest(request).getRight();
        Logger.debug(getClass(), "Handling request took "+(System.currentTimeMillis()-start)+"ms");

        // Return first option or null if no options were found
        return options.isEmpty() ? null : options.get(0);
    }

    @Override public ReferenceOption getCurrentFieldOption(ReferencePathRequest request) {
        List<ReferenceOption> options = references.handleReferenceRequest(request).getRight();
        return options.isEmpty() ? null : options.get(0);
    }

    @Override public List<ReferenceOption> collectReferenceOptions(ReferenceOptionsRequest request) {
        Pair<ReturnResult, List<ReferenceOption>> optionsPair = references.handleReferenceRequest(request);
        return optionsPair.getRight();
    }

    @Override
    public List<ReferenceOption> collectReferenceOptions(ReferencePathRequest request) {
        Pair<ReturnResult, List<ReferenceOption>> optionsPair = references.handleReferenceRequest(request);
        return optionsPair.getRight();
    }

    @Override
    public ReferenceStatusResponse getReferenceStatus(Long id) {
        Pair<ReturnResult, RevisionableInfo> info = revisions.getRevisionableInfo(id);
        if(info.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            return ReferenceStatusResponse.returnResultResponse(ReturnResult.REVISIONABLE_NOT_FOUND);
        }

        return getReferenceStatus(id, info.getRight().getCurrent(), info);
    }

    @Override
    public ReferenceStatusResponse getReferenceStatus(Long id, Integer no) {
        Pair<ReturnResult, RevisionableInfo> info = revisions.getRevisionableInfo(id);
        if(info.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            return ReferenceStatusResponse.returnResultResponse(ReturnResult.REVISIONABLE_NOT_FOUND);
        }

        return getReferenceStatus(id, no, info);
    }

    private ReferenceStatusResponse getReferenceStatus(Long id, Integer no, Pair<ReturnResult, RevisionableInfo> info) {
        if(id == null || no == null || info == null) {
            return ReferenceStatusResponse.returnResultResponse(ReturnResult.PARAMETERS_MISSING);
        }

        DateTimeUserPair removed = null;
        DateTimeUserPair saved = null;
        Map<Language, ApproveInfo> approved = null;

        if(info.getRight().getRemoved()) {
            removed = new DateTimeUserPair(info.getRight().getRemovedAt(), info.getRight().getRemovedBy());
        }

        Pair<ReturnResult, RevisionData> revPair = revisions.getRevisionData(id, no);
        if(revPair.getLeft() == ReturnResult.REVISION_FOUND) {
            return new ReferenceStatusResponse(
                    ReturnResult.REVISION_FOUND.name()
                    , true
                    , revPair.getRight().getConfiguration().getType()
                    , removed
                    , revPair.getRight().getSaved()
                    , revPair.getRight().getApproved()
                    , (info.getRight().getRemoved()
                            ? UIRevisionState.REMOVED
                            : UIRevisionState.fromRevisionState(revPair.getRight().getState())));
        } else {
            return new ReferenceStatusResponse(
                    ReturnResult.REVISION_NOT_FOUND.name()
                    , true
                    , null
                    , removed
                    , null
                    , null
                    , (info.getRight().getRemoved()
                        ? UIRevisionState.REMOVED
                        : null));
        }
    }

    private List<String> formDependencyStack(Field field, Configuration config) {
        List<String> stack = new ArrayList<>();
        Reference reference;

        do {
            if(field == null) {
                break;
            }

            // If the field is not writable then there's no point in adding it to the stack. It will either be an end point or function as a forwarder
            if(field.getWritable()) {
                stack.add(field.getKey());
            }
            reference = getFieldReference(field, config);
            if(reference != null) {
                if(reference.getType() == ReferenceType.DEPENDENCY) {
                    field = config.getField(reference.getTarget());
                } else {
                    reference = null;
                }
            }
        } while(reference != null);

        return stack;
    }

    private Reference getFieldReference(Field field, Configuration config) {
        if(field == null) {
            return null;
        }
        Reference reference = null;
        if(field.getType() == FieldType.REFERENCE || field.getType() == FieldType.REFERENCECONTAINER) {
            reference = config.getReference(field.getReference());
        } else if(field.getType() == FieldType.SELECTION) {
            SelectionList list = config.getRootSelectionList(field.getSelectionList());
            if(list.getType() == SelectionListType.REFERENCE) {
                reference = config.getReference(list.getReference());
            }
        }
        return reference;
    }

    private ReferenceOptionsRequest formReferenceOptionsRequest(Language language, String[] path, List<String> stack, RevisionData data, Configuration config, Boolean emptyEqualsNone) {
        ReferenceOptionsRequest request = new ReferenceOptionsRequest();
        request.setLanguage(language);
        request.setConfType(data.getConfiguration().getType().toValue());
        request.setConfVersion(data.getConfiguration().getVersion());
        request.setKey(path[path.length-1]);
        request.setConfiguration(config);
        request.setEmptyEqualsNone(emptyEqualsNone);
        List<String> pathList = new ArrayList<>();
        Collections.addAll(pathList, path);
        parsePath(request, pathList, stack, data.getFields(), config);

        return request;
    }

    private void parsePath(ReferenceOptionsRequest request, List<String> path, List<String> stack, Map<String, DataField> fieldMap, Configuration config) {
        if(path.isEmpty() || stack.isEmpty()) {
            return;
        }
        DataField field = fieldMap.get(path.get(0));
        if(field != null && field instanceof ContainerDataField) {
            path.remove(0);
            if(!path.isEmpty()) {
                Integer rowId = Integer.parseInt(path.get(0));
                path.remove(0);
                if(!path.isEmpty()) {
                    // Path doesn't terminate so let's try to continue
                    ContainerDataField container = (ContainerDataField) field;
                    DataRow row = container.getRowWithId(rowId).getRight();
                    if (row != null && !row.getFields().isEmpty()) {
                        parsePath(request, path, stack, row.getFields(), config);
                    }
                }
            }
        } else if(field != null && field instanceof ReferenceContainerDataField) {
            path.remove(0);
            if(!path.isEmpty()) {
                Integer rowId = Integer.parseInt(path.get(0));
                path.remove(0);
                ReferenceContainerDataField ref = (ReferenceContainerDataField)field;
                ReferenceRow row = ref.getReferenceWithId(rowId).getRight();
                if(row != null && stack.get(0).equals(ref.getKey())) {
                    request.getFieldValues().put(ref.getKey(), row.getReference().getValue());
                    stack.remove(0);
                }
            }
        }
        while(!stack.isEmpty()) {
            String key = stack.get(0);
            field = fieldMap.get(key);
            if(field != null) {
                if(field instanceof ValueDataField) {
                    stack.remove(0);
                    Field fieldConf = config.getField(field.getKey());
                    Language l = fieldConf.getTranslatable() ? request.getLanguage() : Language.DEFAULT;
                    request.getFieldValues().put(key, ((ValueDataField)field).getActualValueFor(l));
                } else {
                    // In this case we will naturally fail since the same key can't be found on earlier steps in the hierarchy
                    // This is however what should happen since if we found a nonsensical situation and there's no point in continuing.
                    return;
                }
            } else {
                return;
            }
        }
    }
}
