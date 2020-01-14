package fitnessstudio.member;

import fitnessstudio.contract.ContractManagement;
import fitnessstudio.statistics.StatisticManagement;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Initializes default members, when none are already existent.
 * <ul>
 *     <li>user: 'boss', pass: '123'</li>
 *     <li>user: 'member', pass: '123'</li>
 * </ul>
 */
@Component
@Order(100)
public class MemberDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(MemberDataInitializer.class);

	private final UserAccountManager userAccountManager;
	private final MemberManagement memberManagement;
	private final ContractManagement contractManagement;
	private  final StatisticManagement statisticManagement;

	/**
	 * Creates a new {@link MemberDataInitializer} instance with the given parameters.
	 *
	 * @param userAccountManager	Must not be {@literal null}.
	 * @param memberManagement		Must not be {@literal null}.
	 * @param contractManagement	Must not be {@literal null}.
	 * @param statisticManagement	Must not be {@literal null}.
	 */
	MemberDataInitializer(UserAccountManager userAccountManager, MemberManagement memberManagement,
						  ContractManagement contractManagement, StatisticManagement statisticManagement) {

		Assert.notNull(userAccountManager, "UserAccountManager must not be null");
		Assert.notNull(memberManagement, "MemberManagement must not be null");
		Assert.notNull(contractManagement, "ContractManagement must not be null");
		Assert.notNull(statisticManagement, "StatisticManagement must not be null");

		this.memberManagement = memberManagement;
		this.userAccountManager = userAccountManager;
		this.contractManagement = contractManagement;
		this.statisticManagement = statisticManagement;
	}

	/**
	 * Overwrites the {@link DataInitializer#initialize()} Method
	 * and adds {@link org.salespointframework.useraccount.UserAccount}s for default member and boss.
	 */
	@Override
	public void initialize() {
		if (userAccountManager.findByUsername("boss").isPresent()) {
			return;
		}

		LOG.info("Creating default BOSS (user: 'boss', pass: '123'");
		userAccountManager.create("boss", Password.UnencryptedPassword.of("123"), "boss@boss_mail.net",
			Role.of("BOSS"), Role.of("STAFF"));

		if (!contractManagement.getAllContracts().isEmpty()) {
			LOG.info("Creating default MEMBER (user: 'member', pass: '123'");
			memberManagement.createMember(new RegistrationForm("Ulli", "Bulli", "email@email.de", "member",
				"123", "0123456789012345678912", "0123456789",
				contractManagement.getAllContracts().get(0).getContractId(), ""), null).authorize();

			//save contract price of default member(s)
			for(Member member : memberManagement.findAll()) {
				statisticManagement.addRevenue(member.getMemberId(), member.getContract().getContractId());
			}
		}
	}
}
