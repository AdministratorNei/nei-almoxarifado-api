package com.ufrn.nei.almoxarifadoapi.infra.mail;

import com.ufrn.nei.almoxarifadoapi.utils.RefactorDate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class MailTemplates {
  @Autowired
  private SimpleMailMessage simpleMailMessage;

  public SimpleMailMessage buildMailMessageUserCreated(String userEmail, String userName) {
    String subject = "Sua conta foi criada com sucesso!";
    String text = String.format(
        """
            Olá %s,

            Seja bem-vindo ao nosso sistema de solicitação de itens do almoxarifado! Estamos muito felizes em tê-lo conosco.

            Com este sistema, você terá acesso a uma variedade de itens disponíveis em nosso almoxarifado, tornando mais fácil e conveniente solicitar o que você precisa para suas atividades.

            Aqui estão algumas coisas que você pode fazer com a sua nova conta:
            - Explorar nosso catálogo de itens disponíveis.
            - Criar solicitações para itens que você precisa.
            - Acompanhar o status das suas solicitações.
            - Receber notificações sobre o progresso das suas solicitações.

            Estamos sempre trabalhando para melhorar nossa plataforma e proporcionar uma experiência mais eficiente para todos os nossos usuários.

            Se precisar de ajuda ou tiver alguma dúvida, não hesite em entrar em contato conosco. Estamos aqui para ajudar!

            Mais uma vez, obrigado por se juntar a nós. Esperamos que você aproveite ao máximo o nosso sistema de solicitação de itens do almoxarifado.

            Atenciosamente,
            Equipe do NEI""",
        userName);

    simpleMailMessage.setTo(userEmail);
    simpleMailMessage.setSubject(subject);
    simpleMailMessage.setSentDate(new Date());
    simpleMailMessage.setText(text);

    return simpleMailMessage;
  }

  public SimpleMailMessage buildMailMessageRequestCreated(String userEmail, String userName,
      String itemName, Timestamp date, Long itemQuantity) {
    String formatDate = RefactorDate.refactorTimestamp(date);

    String subject = "Sua Solicitação foi criada com sucesso!";
    String text = String.format("""
        Olá %s,

        Sua solicitação do item '%s' foi realizada com sucesso!

        Detalhes:
        - Item: %s
        - Quantidade: %d
        - Hora da Solicitação: %s

        Obrigado por utilizar nosso sistema.""",
        userName, itemName, itemName, itemQuantity, formatDate);

    simpleMailMessage.setTo(userEmail);
    simpleMailMessage.setSubject(subject);
    simpleMailMessage.setSentDate(new Date());
    simpleMailMessage.setText(text);

    return simpleMailMessage;
  }

  public SimpleMailMessage buildMailMessageRequestAccepted(String userEmail, String userName,
      String itemName, Timestamp date, Long itemQuantity, String adminComment) {
      String formatDate = RefactorDate.refactorTimestamp(date);

      adminComment = adminComment != null ? adminComment : "";

      String subject = "Sua solicitação foi aceita!";
      String text = String.format("""
          Olá %s,
  
          Sua solicitação do item '%s' foi aceita!
  
          Detalhes:
          - Item: %s
          - Quantidade: %d
          - Hora da Aceitação: %s
          - Comentário da administração: %s
          
          Obrigado por utilizar nosso sistema.""",
          userName, itemName, itemName, itemQuantity, formatDate, adminComment);

      simpleMailMessage.setTo(userEmail);
      simpleMailMessage.setSubject(subject);
      simpleMailMessage.setSentDate(new Date());
      simpleMailMessage.setText(text);

      return simpleMailMessage;
  }

  public SimpleMailMessage buildMailMessageRequestDenied(String userEmail, String userName,
      String itemName, Timestamp date, Long itemQuantity, String adminComment) {
      String formatDate = RefactorDate.refactorTimestamp(date);

      adminComment = adminComment != null ? adminComment : "";

      String subject = "Sua solicitação foi recusada.";
      String text = String.format("""
        Olá %s,
        
        Sua solicitação para o item '%s' foi recusada.
        
        Detalhes:
        - Item: %s
        - Quantidade: %d
        - Hora da recusa: %s
        - Comentário da administração: %s
        Por favor, entre em contato conosco para mais informações.
        
        Atenciosamente,
        Equipe do Almoxarifado""",
        userName, itemName, itemName, itemQuantity, formatDate, "");

      simpleMailMessage.setTo(userEmail);
      simpleMailMessage.setSubject(subject);
      simpleMailMessage.setSentDate(new Date());
      simpleMailMessage.setText(text);

      return simpleMailMessage;
  }

  public SimpleMailMessage buildMailMessageRequestCanceled(String userEmail, String userName,
      String itemName, Timestamp date, Long itemQuantity) {
    String formatDate = RefactorDate.refactorTimestamp(date);

    String subject = "Confirmação de cancelamento.";
    String text = String.format("""
        Olá %s,

        Sua solicitação para o item '%s' foi cancelada com sucesso.

        Detalhes:
        - Item: %s
        - Quantidade: %d
        - Hora do Cancelamento: %s

        Equipe do Almoxarifado.""",
        userName, itemName, itemName, itemQuantity, formatDate);

    simpleMailMessage.setTo(userEmail);
    simpleMailMessage.setSubject(subject);
    simpleMailMessage.setSentDate(new Date());
    simpleMailMessage.setText(text);

    return simpleMailMessage;
  }

  public MimeMessageHelper buildMailMessageForgotPassword(String userEmail, String token, MimeMessage mime)
      throws MessagingException {
    MimeMessageHelper helper = new MimeMessageHelper(mime, true);

    String subject = "Redefinição de senha no NEI Almoxarifado";

    helper.setTo(userEmail);
    helper.setSubject(subject);
    helper.setSentDate(new Date());
    helper.setText(
    "<h3> Código de redefinição de senha </h3> <br> <span> Seu código para a redefinição de senha é <strong>"
            + token + "</strong>. Se você não fez essa solicitação, pode ignorar esse e-mail com segurança.",
            true);

    return helper;
  }

  public MimeMessageHelper buildMailMessageLowStock(String[] sendTo, String itemName, Integer currentQuantity,
                                                    Integer idealAmount, MimeMessage mime) throws MessagingException {
    MimeMessageHelper helper = new MimeMessageHelper(mime, true);

    String subject = "Aviso: Necessário reposição de item";
    String text = String.format("""
          <p>Olá,</p>
          <p>A quantidade do item <strong>%s</strong> está abaixo da quantidade ideal estabelecida.</p>
          <ul>
              <li><strong>Quantidade Atual: </strong>%d</li>
              <li><strong>Quantidade Ideal: </strong>%d</li>
          </ul>
          """, itemName, currentQuantity, idealAmount);


    helper.setTo(sendTo);
    helper.setSubject(subject);
    helper.setText(text, true);

    return helper;
  }
}
