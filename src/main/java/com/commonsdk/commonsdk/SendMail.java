package com.commonsdk.commonsdk;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
public class SendMail {

    static String apiKey;

    public SendMail(@Value("${app.sendgrid.apikey}") String apiKey){
        this.apiKey = apiKey;
        System.out.println("api key => " +  this.apiKey);
        log.info("API key  ===== >>> {}", this.apiKey);
    }

    public static void sendMail(String fromEmail, String toEmail, String subject, String content, List<File> attachmentFiles) throws IOException {
        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        Content body = new Content("text/html", content);
        Mail mail = new Mail(from, subject, to, body);

        log.info("\n\n\n\n\n\n\n\n\n\n\n from => {} to => {} subject => {} content => {} \n\n\n\n\n\n",fromEmail, toEmail, subject, content);

        System.out.println("api key => " +  apiKey);
        log.info("API key  ===== >>> {}", apiKey);

        if(attachmentFiles != null) {
            Attachments attachment = new Attachments();

            for(File file: attachmentFiles){
                byte[] fileContent = FileUtils.readFileToByteArray(file);
                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                attachment.setContent(encodedString);
                attachment.setType("application/" + FilenameUtils.getExtension(file.getPath()));
                attachment.setFilename(file.getName());
                attachment.setDisposition("inline");
                attachment.setContentId("inline");
                mail.addAttachments(attachment);
            }
        }

        System.out.println("api key => " + apiKey);
        log.info("API key  ===== >>> {}", apiKey);

        System.out.println("\n\n\n\n\n\n\n\n\n\n\n" + fromEmail + toEmail + subject +  content + "\n\n\n\n\n\n\n");
        log.info("\n\n\n\n\n\n\n\n\n\n\n from => {} to => {} subject => {} content => {} \n\n\n\n\n\n",fromEmail, toEmail, subject, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
        } catch (IOException ex) {
            throw ex;
        }
    }
}