package fi.uta.fsd.metka.automation;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.RevisionableRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAmqp.payloads.ContractTriggerPayload;
import fi.uta.fsd.metkaAmqp.payloads.RevisionPayload;
import fi.uta.fsd.metkaAmqp.payloads.TestPayload;
import org.apache.commons.lang3.tuple.Pair;
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
        Calendar today = Calendar.getInstance();
        for (int i = 0; i < ids.length; i++) {
            Pair<ReturnResult, RevisionData> pair = revisionRepository.getRevisionData(Long.toString(ids[i]));
            if (pair.getRight().getField("triggerdate") != null && pair.getRight().getField("triggerpro") != null) {
                ValueDataField triggerdate = (ValueDataField) pair.getRight().getField("triggerdate");
                String[] triggerDateArray = triggerdate.getActualValueFor(Language.DEFAULT).split("-");
                if (today.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(triggerDateArray[2])
                        && today.get(Calendar.MONTH) + 1 == Integer.parseInt(triggerDateArray[1])
                        && today.get(Calendar.YEAR) == Integer.parseInt(triggerDateArray[0])) {
                    if (Boolean.parseBoolean(triggersActive)){
                        sendEmailAlert(pair.getRight());
                    }
                    sendAMQPAlert(pair.getRight());
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

    private void sendAMQPAlert(RevisionData revision){
        messenger.sendAmqpMessage(messenger.FB_CONTRACT_TRIGGER, new ContractTriggerPayload(revision));
    }

}
