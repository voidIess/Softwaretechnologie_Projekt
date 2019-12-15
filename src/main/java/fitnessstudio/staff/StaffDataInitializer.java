package fitnessstudio.staff;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StaffDataInitializer implements DataInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(StaffDataInitializer.class);
	private final StaffManagement staffs;
	private final UserAccountManager userAccounts;


	StaffDataInitializer(StaffManagement staffs, UserAccountManager userAccounts) {
		this.userAccounts = userAccounts;
		this.staffs = staffs;
	}

	@Override
	public void initialize() {

		if (userAccounts.findByUsername("staff").isPresent()) {
			return;
		}
		Staff staff = new Staff(userAccounts.create("staff",
				Password.UnencryptedPassword.of("123"), "markus@email.de",
				Role.of("STAFF")), "Markus", "Wieland",
				Money.of(100, "EUR"));
		LOG.info("Create Staff (username: staff, password: 123)");

		Staff staff2 = new Staff(userAccounts.create("Obi",
				Password.UnencryptedPassword.of("123"), "obi@mehralsbaumarkt.de",
				Role.of("STAFF")),
				"Obi", "Babobi",
				Money.of(10000, "EUR"));
		LOG.info("Create Staff (username: obi, password: 123)");

		staffs.saveStaff(staff);
		staffs.saveStaff(staff2);

	}
}
