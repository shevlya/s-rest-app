package ru.ssau.s_rest_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@dosug.ru}")
    private String fromEmail;

    @Value("${app.name:Досуг}")
    private String appName;

    // Отправка с обработкой ошибок — не ломаем бизнес-логику если почта не настроена
    private void send(String to, String subject, String text) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
        } catch (Exception e) {
            log.warn("Не удалось отправить письмо на {}: {}", to, e.getMessage());
        }
    }

    public void sendEventApproved(String organizerEmail, String organizerName,
                                  String eventName, String comment) {
        String subject = appName + ": ваше мероприятие одобрено";
        String text = String.format(
                "Здравствуйте, %s!\n\n" +
                        "Ваше мероприятие «%s» прошло модерацию и опубликовано на платформе.\n" +
                        (comment != null && !comment.isBlank() ? "Комментарий администратора: %s\n\n" : "\n") +
                        "С уважением,\nКоманда " + appName,
                organizerName, eventName, comment
        );
        send(organizerEmail, subject, text);
    }

    public void sendEventRejected(String organizerEmail, String organizerName,
                                  String eventName, String reason) {
        String subject = appName + ": мероприятие отклонено";
        String text = String.format(
                "Здравствуйте, %s!\n\n" +
                        "К сожалению, ваше мероприятие «%s» не прошло модерацию.\n" +
                        (reason != null && !reason.isBlank()
                                ? "Причина: " + reason + "\n\n" : "\n") +
                        "Вы можете внести изменения и подать мероприятие повторно.\n\n" +
                        "С уважением,\nКоманда " + appName,
                organizerName, eventName
        );
        send(organizerEmail, subject, text);
    }

    public void sendEventEdited(String organizerEmail, String organizerName,
                                String eventName) {
        String subject = appName + ": в ваше мероприятие внесены изменения";
        String text = String.format(
                "Здравствуйте, %s!\n\n" +
                        "Администратор платформы внёс изменения в ваше мероприятие «%s».\n" +
                        "Мероприятие опубликовано и доступно участникам.\n\n" +
                        "С уважением,\nКоманда " + appName,
                organizerName, eventName
        );
        send(organizerEmail, subject, text);
    }

    public void sendOrganizerRequestApproved(String userEmail, String userName) {
        String subject = appName + ": ваша заявка одобрена";
        String text = String.format(
                "Здравствуйте, %s!\n\n" +
                        "Ваша заявка на роль организатора одобрена.\n" +
                        "Теперь вы можете создавать мероприятия на платформе.\n\n" +
                        "С уважением,\nКоманда " + appName,
                userName
        );
        send(userEmail, subject, text);
    }

    public void sendOrganizerRequestRejected(String userEmail, String userName,
                                             String reason) {
        String subject = appName + ": ваша заявка отклонена";
        String text = String.format(
                "Здравствуйте, %s!\n\n" +
                        "К сожалению, ваша заявка на роль организатора отклонена.\n" +
                        (reason != null && !reason.isBlank()
                                ? "Причина: " + reason + "\n\n" : "\n") +
                        "С уважением,\nКоманда " + appName,
                userName
        );
        send(userEmail, subject, text);
    }
}
