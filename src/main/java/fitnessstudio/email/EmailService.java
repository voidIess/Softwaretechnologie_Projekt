package fitnessstudio.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
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
	public void sendFriendInvite (String emailTo, String friendsName, long memberId) {
		sendSimpleMessage(emailTo, "Du hast eine Einladung für Fitness Second bekommen!",
			"Hallo!" + "\n\n" + friendsName + ", ein Mitglied unseres Fitnessstudios (und vermutlich ein Freund von dir), " +
				"hat dich eingladen unser Fitnessstudio zu testen! \n\n" +
				"Über diesen Link gelangst du zur Registrierung:\n\n" +
				"http://localhost:8080/register/" + memberId);
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
				+ "Wir haben soeben deinen Antrag zur Mitgliedschaft beabeitet. Absofort hast du unbegrenzten Zutritt "
				+ "zu all unseren Diensten und vorallem dem Studio. \n"
				+ "Fang doch gleich morgen an zu trainieren :)! \n\n"
				+ "Dein Fitnessstudio-Team");
	}
}
