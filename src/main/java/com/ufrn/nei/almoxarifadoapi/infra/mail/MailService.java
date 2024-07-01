package com.ufrn.nei.almoxarifadoapi.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private MailTemplates mailTemplates;
    @Autowired
    private MimeMessage mime;

    @Async
    public void sendMailUserCreatedAsync(String userEmail, String userName) {
        SimpleMailMessage message = mailTemplates.buildMailMessageUserCreated(userEmail, userName);

        CompletableFuture<Boolean> sender = buildSendEmailAsync(message);

        sender.join();
    }

    @Async
    public void sendMailRequestCreatedAsync(String userEmail, String userName,
                                               String itemName, Timestamp date, Long itemQuantity) {
        SimpleMailMessage message =
                mailTemplates.buildMailMessageRequestCreated(userEmail, userName, itemName, date, itemQuantity);

        CompletableFuture<Boolean> sender = buildSendEmailAsync(message);

        sender.join();
    }

    @Async
    public void sendMailRequestAcceptedAsync(String userEmail, String userName,
                                               String itemName, Timestamp date, Long itemQuantity) {
        SimpleMailMessage message =
                mailTemplates.buildMailMessageRequestAccepted(userEmail, userName, itemName, date, itemQuantity);

        CompletableFuture<Boolean> sender = buildSendEmailAsync(message);

        sender.join();
    }

    @Async
    public void sendMailRequestDeniedAsync(String userEmail, String userName,
                                             String itemName, Timestamp date, Long itemQuantity) {
        SimpleMailMessage message =
                mailTemplates.buildMailMessageRequestDenied(userEmail, userName, itemName, date, itemQuantity);

        CompletableFuture<Boolean> sender = buildSendEmailAsync(message);

        sender.join();
    }

    @Async
    public void sendMailRequestCanceledAsync(String userEmail, String userName,
                                           String itemName, Timestamp date, Long itemQuantity) {
        SimpleMailMessage message =
                mailTemplates.buildMailMessageRequestCanceled(userEmail, userName, itemName, date, itemQuantity);

        CompletableFuture<Boolean> sender = buildSendEmailAsync(message);

        sender.join();
    }

    @Async
    public void sendMailForgotPassword(String userEmail, String token) throws MessagingException {
        try {
          mailTemplates.buildMailMessageForgotPassword(userEmail, token, mime);

          CompletableFuture<Boolean> sender = buildSendEmailAsync(mime);

          sender.join();
        } catch (MessagingException e) {
          e.printStackTrace();
        }
    }

    @Async
    public void sendMailLowStock(String[] sendTo, String itemName, Integer currentQuantity,
                                 Integer idealAmount) {
        try {
            mailTemplates.buildMailMessageLowStock(sendTo, itemName, currentQuantity, idealAmount, mime);

            CompletableFuture<Boolean> sender = buildSendEmailAsync(mime);

            sender.join();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    private CompletableFuture<Boolean> buildSendEmailAsync(MimeMessage mimeMessage) throws MessagingException {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        try {
            javaMailSender.send(mimeMessage);

            log.info("Email enviado com sucesso para {}", mimeMessage.getRecipients(Message.RecipientType.TO));
            future.complete(Boolean.TRUE);
        } catch (MailAuthenticationException ex) {
            log.error("Erro na autenticação do email - {}", ex.getMessage());
            future.completeExceptionally(new MailAuthenticationException("Erro na autenticação do email"));
        } catch (Exception ex) {
            log.error("Erro ao enviar email para {}: {}", mimeMessage.getRecipients(Message.RecipientType.TO), ex.getMessage());
            future.completeExceptionally(new MailSendException("Erro ao enviar email"));
        }

        return future;
    }

    @Async
    private CompletableFuture<Boolean> buildSendEmailAsync(SimpleMailMessage simpleMailMessage) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        try {
            javaMailSender.send(simpleMailMessage);

            log.info("Email enviado com sucesso para {}", simpleMailMessage.getTo());
            future.complete(Boolean.TRUE);
        } catch (MailSendException ex) {
            log.error("Erro ao enviar email para {}: {}", simpleMailMessage.getTo(), ex.getMessage());
            future.completeExceptionally(new MailSendException("Erro ao enviar email"));
        } catch (MailAuthenticationException ex) {
            log.error("Erro na autenticação do email - {}", ex.getMessage());
            future.completeExceptionally(new MailAuthenticationException("Erro na autenticação do email"));
        }

        return future;
    }
}
