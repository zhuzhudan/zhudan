package com.study.util;

import com.study.bean.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

@Component
@RefreshScope
public class MailUtil {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;

    /**
     * 发送普通邮件
     */
    public Boolean sendMail(Mail mail){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailProperties.getUsername());
        mailMessage.setTo(mail.getToAccount());
        mailMessage.setSubject(mail.getSubject());
        mailMessage.setText(mail.getContent());
        try {
            mailSender.send(mailMessage);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 发送带附件邮件
     */
    public Boolean sendMailAttachment(Mail mail){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(mail.getToAccount());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getContent());

            // 增加附件
            helper.addAttachment(MimeUtility.encodeWord(mail.getAttachmentFileName(), "utf-8", "B"), mail.getAttachmentFile());
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
