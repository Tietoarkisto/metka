package fi.uta.fsd.metka.automation;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.RevisionableRepository;
import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAmqp.payloads.ApprovalDelayedPayload;
import fi.uta.fsd.metkaAmqp.payloads.ContractTriggerPayload;
import fi.uta.fsd.metkaAmqp.payloads.ErrorTriggerPayload;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

/**
 * Created by juusoko on 13.2.2017.
 * juuso.korhonen@visma.com
 */
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
            RevisionData data = revisionRepository.getRevisionData(Long.toString(ids[i])).getRight();
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
