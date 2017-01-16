package towntalk.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import towntalk.model.Email;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by dooseon on 2016. 6. 6..
 */
@Component
public class EmailSender {

    @Autowired
    protected JavaMailSender mailSender;

    public void SendEmail(Email email) throws Exception{
        MimeMessage message = mailSender.createMimeMessage();
        message.setSubject(email.getSubject());
        message.setText(email.getContent());
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getReciver()));

        mailSender.send(message);
    }
}
