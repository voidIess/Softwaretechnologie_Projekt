package fitnessstudio.staff;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StaffDataInitializer implements DataInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(StaffDataInitializer.class);

	private final StaffRepository staffRepo;
	private final UserAccountManager userAccounts;

	StaffDataInitializer(StaffRepository repository, UserAccountManager userAccounts) {
		this.staffRepo = repository;
		this.userAccounts = userAccounts;
	}


	@Override
	public void initialize() {

		LOG.info("Creating default BOSS (user: 'staff', pass: '123'");
		UserAccount account = userAccounts.create("staff", Password.UnencryptedPassword.of("123"), Role.of("STAFF"));
		Staff staff = new Staff(account, "firstNameS", "lastNameS", Money.of(3000, "EUR"));
		staffRepo.save(staff);

	}
}
