package fitnessstudio.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class EmailService {

	private final JavaMailSender mailSender;

	public EmailService(JavaMailSender mailSender) {
		Assert.notNull(mailSender, "mailSender must not be null");

		this.mailSender = mailSender;
	}

	private void sendSimpleMessage(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}

	@Async
	public void sendTrainingStateUpdated(String emailTo, String name, Long trainingId) {
		sendSimpleMessage(emailTo, "Eine deiner Traininganfragen wurde beabeitet",
			"Hallo " + name + ", \n\n"
				+ "Deine Trainingsanfrage(ID: " + trainingId + ") wurde beantwortet. "
				+ "Auf deinem Benutzerkonto erfährst du mehr. \n\n"
				+ "Dein Fitnessstudio-Team!");
	}

	@Async
	public void sendAccountAcceptation(String emailTo, String name) {
		sendSimpleMessage(emailTo, "Dein Mitgliedschaftsantrag wurde bearbeitet",
			"Hallo " + name + ", \n\n"
				+ "Wir haben soeben deinen Antrag zur Mitgliedschaft beabeitet. Absofort hast du ungegrenzten Zutritt "
				+ "zu all unseren Diensten und forallem dem Studio. \n"
				+ "Fang doch gleich morgen an zu trainieren :)! \n"
				+ "(PS: Vergiss nicht dein Konto aufzuladen um damit an unserer Bar unbegrenzt Shoppen zu gehen!) \n\n"
				+ "Dein Fitnessstudio-Team");
	}
}
