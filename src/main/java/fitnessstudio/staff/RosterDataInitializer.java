package fitnessstudio.staff;

import fitnessstudio.member.MemberDataInitializer;
import fitnessstudio.member.MemberManagement;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.Month;

@Component
public class RosterDataInitializer implements DataInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(RosterDataInitializer.class);

	private final RosterManagement rosterManagement;
	private final RosterRepository rosters;
	private final UserAccountManager userAccounts;

	RosterDataInitializer(RosterManagement rosterManagement, RosterRepository repository, UserAccountManager userAccounts){

		Assert.notNull(rosterManagement, "RosterManagement must not be null");
		Assert.notNull(repository, "Repository must not be null");

		this.userAccounts = userAccounts;
		this.rosters = repository;
		this.rosterManagement = rosterManagement;
	}

	@Override
	public void initialize() {

		//Roster roster = new Roster(1);
		//UserAccount userAccount, String firstName, String lastName
		Staff staff = new Staff(userAccounts.create("Mar14511", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		LocalDateTime date = LocalDateTime.of(2019, Month.JANUARY,1, 12, 00);

		RosterEntry rosterEntry = new RosterEntry(StaffRole.COUNTER,staff,date,119);
		RosterEntry rosterEntry2 = new RosterEntry(StaffRole.COUNTER,staff,date,60);

		//roster.addEntry(rosterEntry);

		rosters.save(rosterEntry);
		rosters.save(rosterEntry2);
		LOG.info("ID:" + rosterEntry.getRosterId());

		LOG.info("Creating default BOSS (user: 'boss', pass: '123')");



	}
}
