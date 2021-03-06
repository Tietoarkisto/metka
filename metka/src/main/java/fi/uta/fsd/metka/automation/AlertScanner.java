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

package fi.uta.fsd.metka.automation;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.RevisionableRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAmqp.payloads.ApprovalDelayedPayload;
import fi.uta.fsd.metkaAmqp.payloads.ContractTriggerPayload;
import fi.uta.fsd.metkaAmqp.payloads.ErrorTriggerPayload;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Component
public class AlertScanner {

    @Autowired
    RevisionRepository revisionRepository;

    @Autowired
    RevisionableRepository revisionableRepository;

    @Autowired
    Messenger messenger;

    @Value("${email.sender.address}")
    String senderAddress;

    @Value("${email.sender.password}")
    String senderPassword;

    @Value("${email.smtp.port}")
    String smtpPort;

    @Value("${email.smtp.address}")
    String smtpAddr;

    @Value("${email.smtp.auth}")
    String smtpAuth;

    @Value("${email.triggers.send}")
    String triggersActive;

    static Properties mailServerProperties;
    static Session getMailSession;
    static MimeMessage generateMailMessage;

    @Scheduled(cron="${trigger.cron}")
    public void run() throws MessagingException {
        long[] ids = revisionableRepository.getAllRevisionableIds();
        LocalDate today = new LocalDate();
        for (int i = 0; i < ids.length; i++) {
            Pair<ReturnResult, RevisionData> dataPair = revisionRepository.getRevisionData(Long.toString(ids[i]));
            if(!dataPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
                continue;
            }
            RevisionData data = dataPair.getRight();
            if (data.getField("triggerdate") != null && data.getField("triggerpro") != null) {
                if (today.toString().equals(((ValueDataField)data.getField("triggerdate")).getActualValueFor(Language.DEFAULT))) {
                    if (Boolean.parseBoolean(triggersActive)){
                        sendEmailAlert(data);
                    }
                    sendAMQPAlert(data, "B7");
                }
            }
            if (data.getField("errortriggerdate") != null && data.getField("errortriggerpro") != null){
                if (today.toString().equals(((ValueDataField)data.getField("errortriggerdate")).getActualValueFor(Language.DEFAULT))) {
                    if (Boolean.parseBoolean(triggersActive)){
                        sendEmailAlert(data);
                    }
                    sendAMQPAlert(data, "B8");
                }
            }
            if (data.getSaved() != null && data.getState().toString().equals("DRAFT") && data.getConfiguration().getType().toValue().equals("STUDY")){
                if (today.minusDays(100).equals(data.getSaved().getTime().toLocalDate())) {
                    sendAMQPAlert(data, "threshold-100");
                } else if (today.minusDays(200).equals(data.getSaved().getTime().toLocalDate())){
                    sendAMQPAlert(data, "threshold-200");
                } else if (today.minusDays(300).equals(data.getSaved().getTime().toLocalDate())){
                    sendAMQPAlert(data, "threshold-300");
                } else if (today.minusDays(400).equals(data.getSaved().getTime().toLocalDate())){
                    sendAMQPAlert(data, "threshold-400");
                } else if (today.minusDays(500).equals(data.getSaved().getTime().toLocalDate())){
                    sendAMQPAlert(data, "threshold-500");
                }
            }
        }

    }

    private void sendEmailAlert(RevisionData revision) throws MessagingException {
        PasswordAuthentication authenticator = new PasswordAuthentication(senderAddress, senderPassword);
        ValueDataField triggerTarget = (ValueDataField) revision.getField("triggerpro");
        Map<String, DataField> fields = new TreeMap<>(revision.getFields());
        String mailto = triggerTarget.getActualValueFor(Language.DEFAULT);
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", smtpPort);
        mailServerProperties.put("mail.smtp.auth", smtpAuth);
        mailServerProperties.put("mail.smtp.starttls.enable", "true");

        ValueDataField field = (ValueDataField)fields.get("title");

        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        generateMailMessage = new MimeMessage(getMailSession);
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(mailto));
        generateMailMessage.setSubject("Metka aineiston heräte");
        generateMailMessage.setContent("Title: " + field.getActualValueFor(Language.DEFAULT) +
                "<br>Abstract: " + ((ValueDataField)fields.get("abstract")).getActualValueFor(Language.DEFAULT) +
                "<br>URL: localhost:8080/metka/web/revision/view/" + revision.getConfiguration().getType().toValue() + "/" +
                revision.getKey().getId().toString() + "/" + revision.getKey().getNo().toString(), "text/html");

        Transport transport = getMailSession.getTransport("smtp");
        transport.connect(smtpAddr, authenticator.getUserName(), authenticator.getPassword());
        try {
            transport.sendMessage(generateMailMessage,generateMailMessage.getAllRecipients());
        } catch (SendFailedException ex){
            Logger.error(getClass(), "Could not send alert to address \"" + mailto + "\"");
        }
        transport.close();
    }

    private void sendAMQPAlert(RevisionData revision, String alertCode){
        if (alertCode.equals("B7")) {
            messenger.sendAmqpMessage(messenger.FB_CONTRACT_TRIGGER, new ContractTriggerPayload(revision));
        } else if (alertCode.equals("B8")){
            messenger.sendAmqpMessage(messenger.FB_ERROR_TRIGGER, new ErrorTriggerPayload(revision));
        } else if (alertCode.contains("threshold")){
            messenger.sendAmqpMessage(messenger.FB_APPROVAL_DELAYED, new ApprovalDelayedPayload(revision, alertCode));
        }
    }
}
