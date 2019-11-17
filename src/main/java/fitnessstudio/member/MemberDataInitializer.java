package fitnessstudio.member;

import org.apache.juli.logging.Log;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Order(20)
public class MemberDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(MemberDataInitializer.class);

	private final UserAccountManager userAccountManager;
	private final MemberManagement memberManagement;


	MemberDataInitializer(UserAccountManager userAccountManager, MemberManagement memberManagement){

		Assert.notNull(userAccountManager, "UserAccountManager must not be null");
		Assert.notNull(memberManagement, "MemberManagement must not be null");

		this.memberManagement = memberManagement;
		this.userAccountManager = userAccountManager;
	}

	@Override
	public void initialize() {
		if (userAccountManager.findByUsername("boss").isPresent()){
			return;
		}

		LOG.info("Creating default BOSS (user: 'boss', pass: '123'");
		userAccountManager.create("boss", Password.UnencryptedPassword.of("123"), Role.of("BOSS"));

		LOG.info("Creating default MEMBER (user: 'member', pass: '123'");
		memberManagement.createMember(new RegistrationForm("FirstName", "LastName", "member", "123"), null).getUserAccount().setEnabled(true);

		LOG.info("Creating unauthorized MEMBER (user: 'member2', pass: '123'");
		memberManagement.createMember(new RegistrationForm("FirstName", "LastName", "member2", "123"), null);

	}
}
