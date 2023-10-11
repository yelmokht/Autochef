package ulb.infof307.g02.util.import_export;

import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;
import ulb.infof307.g02.model.sql_model.user_model.User;
import ulb.infof307.g02.util.AlertUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class EmailUtils {
    private static final String SMTP_USERNAME = "noreply.autochef@gmail.com";
    private static final String SMTP_PASSWORD = "hsfnxfoajivzgxer";

    private EmailUtils(){}

    public static Session getEmailSession() {
        // Setting properties to get session
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host","smtp.gmail.com");
        properties.setProperty("mail.smtp.port","587");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.starttls.required", "true");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Getting smtp session
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });
    }

    public static void sendMessage(Session session, String fileToSend, String nameOfFile, String destinationEmail) throws MessagingException {
        try {
            // Creating Message
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinationEmail));
            message.setSubject(nameOfFile);
            message.setContent(preparedMessage(fileToSend, nameOfFile));
            // Sending Message
            Transport.send(message);
            AlertUtils.alertInformationMessage("Email", nameOfFile +" envoy\u00e9e avec succ\u00e8s");
            Autochef.getLogger().info("Email SENT");

        } catch (MessagingException messagingException){
            Autochef.getLogger().error("Error sending the Email");
            throw new MessagingException("email was not sent");
        }
    }

    /**
     * sends an email to destinationEmail with the listName as email subject and the file specified by filePath attached to it
     * @param fileToSend the file we want to send by email
     * @param fileName the name to giv to the file before sending it
     */
    public static void sendEmail(File fileToSend, String fileName) throws MessagingException {
        Session session = getEmailSession();
        sendMessage(session, fileToSend.getAbsolutePath(), fileName, User.getInstance().getEmail());
        // Destination is always the login email, can't send to someone else from app (not an option)
    }

    private static Multipart preparedMessage(String file, String listName) throws MessagingException{
        Multipart messageCore = new MimeMultipart();

        BodyPart textPart = new MimeBodyPart();
        textPart.setText("Votre liste de course:");
        messageCore.addBodyPart(textPart);

        BodyPart filePart = new MimeBodyPart();
        FileDataSource fileSource = new FileDataSource(file);
        filePart.setDataHandler(new DataHandler(fileSource));
        filePart.setFileName(listName+".pdf");
        messageCore.addBodyPart(filePart);
        
        return messageCore;
    }

}
