package fitnessstudio.member;

import fitnessstudio.contract.Contract;
import fitnessstudio.contract.ContractManagement;
import fitnessstudio.invoice.InvoiceEntry;
import fitnessstudio.invoice.InvoiceEvent;
import fitnessstudio.invoice.InvoiceManagement;
import fitnessstudio.invoice.InvoiceType;
import fitnessstudio.statistics.StatisticManagement;
import fitnessstudio.studio.StudioService;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Streamable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class MemberManagement {

	public static final Role MEMBER_ROLE = Role.of("MEMBER");
	private static final Logger LOG = LoggerFactory.getLogger(MemberManagement.class);

	private final ApplicationEventPublisher applicationEventPublisher;
	private final MemberRepository members;
	private final UserAccountManager userAccounts;
	private final ContractManagement contractManagement;
	private final StudioService studioService;
	private final StatisticManagement statisticManagement;
	private final InvoiceManagement invoiceManagement;

	MemberManagement(MemberRepository members, UserAccountManager userAccounts, ContractManagement contractManagement,
					 StudioService studioService, StatisticManagement statisticManagement,
					 ApplicationEventPublisher applicationEventPublisher, InvoiceManagement invoiceManagement) {

		Assert.notNull(members, "MemberRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManager must not be null!");
		Assert.notNull(contractManagement, "ContractManagement must not be null!");
		Assert.notNull(studioService, "StudioService must not be null!");
		Assert.notNull(statisticManagement, "StatisticManagement must not be null!");
		Assert.notNull(applicationEventPublisher, "ApplicationEventPublisher should not be null!");
		Assert.notNull(invoiceManagement, "InvoiceManagement should not be null!");

		this.members = members;
		this.userAccounts = userAccounts;
		this.contractManagement = contractManagement;
		this.studioService = studioService;
		this.statisticManagement = statisticManagement;
		this.applicationEventPublisher = applicationEventPublisher;
		this.invoiceManagement = invoiceManagement;
	}

	public Member createMember(RegistrationForm form, Errors result) {
		Assert.notNull(form, "Registration form must not be null");

		var firstName = form.getFirstName();
		var lastName = form.getLastName();
		var email = form.getEmail();
		var password = Password.UnencryptedPassword.of(form.getPassword());
		var iban = form.getIban();
		var bic = form.getBic();
		var contract = form.getContract();

		if (userAccounts.findByUsername(form.getUserName()).isPresent()) {
			result.rejectValue("userName", "register.duplicate.userAccountName");
		}

		if (emailExists(email)) {
			result.rejectValue("email", "register.duplicate.userAccountEmail");
		}

		if (iban.length() != 22) {
			result.rejectValue("iban", "register.iban.wrongSize");
		}

		if (bic.length() < 8 || bic.length() > 11) {
			result.rejectValue("bic", "register.bic.wrongSize");
		}

		if (result != null && result.hasErrors()) {
			return null;
		}

		var bonusCode = form.getBonusCode();
		if (!bonusCode.isEmpty()) {
			Optional<Member> receiverOptional = members.findById(Long.parseLong(bonusCode));
			if (receiverOptional.isEmpty()) {
				result.rejectValue("bonusCode", "register.bonusCode.notFound");
				return null;
			}
			else {
				Member receiver = receiverOptional.get();
				Money bonus = Money.of(new BigDecimal(studioService.getStudio().getAdvertisingBonus()),
					"EUR");

				receiver.payIn(bonus);
				members.save(receiver);

				applicationEventPublisher.publishEvent(new InvoiceEvent(this, receiver.getMemberId(),
					InvoiceType.DEPOSIT, bonus, "Anwerbebonus"));
			}

		}

		var userAccount = userAccounts.create(form.getUserName(), password, email, MEMBER_ROLE);
		var member = new Member(userAccount, firstName, lastName, iban, bic);

		Optional<Contract> contractOptional = contractManagement.findById(contract);
		if (contractOptional.isEmpty()) {
			result.rejectValue("contract", "register.contract.notFound");
			return null;
		}

		member.setContract(contractOptional.get());
		return members.save(member);

	}

	public void deleteMember(Long memberId) {
		Optional<Member> member = findById(memberId);
		member.ifPresent(members::delete);
	}

	public void authorizeMember(Long memberId) {
		Optional<Member> member = findById(memberId);
		member.ifPresent(Member::authorize);
	}

	public void editMember(Long memberId, EditingForm form, Errors result) {
		Assert.notNull(form, "EditingForm form must not be null");

		var firstName = form.getFirstName();
		var lastName = form.getLastName();
		var iban = form.getIban();
		var bic = form.getBic();
		var email = form.getEmail();

		Optional<Member> optionalMember = findById(memberId);
		if (optionalMember.isPresent()) {
			Member member = optionalMember.get();

			if (iban.length() != 22) {
				result.rejectValue("iban", "register.iban.wrongSize");
				return;
			}

			if (bic.length() < 8 || bic.length() > 11) {
				result.rejectValue("bic", "register.bic.wrongSize");
				return;
			}

			if (emailExists(email) && !email.equals(member.getUserAccount().getEmail())) {
				result.rejectValue("email", "register.duplicate.userAccountEmail");
				return;
			}

			member.setFirstName(firstName);
			member.setLastName(lastName);
			member.getCreditAccount().update(iban, bic);
			member.getUserAccount().setEmail(email);

			members.save(member);
		}
	}

	EditingForm preFillMember(Member member, EditingForm form) {
		if (form.isEmpty()) {
			return new EditingForm(
				member.getFirstName(),
				member.getLastName(),
				member.getUserAccount().getEmail(),
				member.getCreditAccount().getIban(),
				member.getCreditAccount().getBic());
		}
		return form;
	}

	public Streamable<Member> findAll() {
		return members.findAll();
	}

	public List<Member> findAllUnauthorized() {
		return userAccounts.findDisabled()
			.stream().map(this::findByUserAccount)
			.flatMap(member -> member.stream()
				.flatMap(Stream::of)).collect(Collectors.toList());
	}

	public List<Member> findAllAuthorized(String search) {
		if (search == null) { search = ""; }
		String finalSearch = search;

		return userAccounts.findEnabled()
			.stream().map(this::findByUserAccount)
			.flatMap(Optional::stream)
			.filter(member -> String.valueOf(member.getMemberId()).startsWith(finalSearch))
			.collect(Collectors.toList());
	}


	public Optional<Member> findById(long id) {
		return members.findById(id);
	}

	public Optional<Member> findByUserAccount(UserAccount userAccount) {
		return members.findByUserAccount(userAccount);
	}

	/**
	 * Method to deposit money from member's {@link CreditAccount}.
	 *
	 * @param memberId ID of member
	 * @param amount   amount of money to deposit
	 */
	public void memberPayIn(long memberId, Money amount) {
		Optional<Member> optionalMember = members.findById(memberId);

		if (optionalMember.isPresent()) {
			Member member = optionalMember.get();
			member.payIn(amount);
			members.save(member);

			applicationEventPublisher.publishEvent(new InvoiceEvent(this, memberId, InvoiceType.DEPOSIT, amount,
				"Einzahlung auf Account"));
		}
	}

	/**
	 * Method to withdraw money to member's {@link CreditAccount}.
	 *
	 * @param memberId    ID of member
	 * @param amount      amount of money to withdraw
	 * @param description description of the order
	 */
	public void memberPayOut(long memberId, Money amount, String description) {
		Optional<Member> optionalMember = members.findById(memberId);

		if (optionalMember.isPresent()) {
			Member member = optionalMember.get();
			member.payOut(amount);
			members.save(member);

			applicationEventPublisher.publishEvent(new InvoiceEvent(this, memberId, InvoiceType.WITHDRAW, amount,
				description));
		}
	}

	Map<String, Object> createPdfInvoice(UserAccount account) {

		Optional<Member> opt = members.findByUserAccount(account);
		Assert.isTrue(opt.isPresent(), "There is no existing member for this account");
		Member member = opt.get();

		Map<String, Object> map = new HashMap<>();

		map.put("member", member);

		LocalDate endDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth());
		System.out.println("e; " + endDate);
		map.put("endDate", endDate);
		map.put("endCredit", getMemberCreditOfDate(member, endDate));

		LocalDate startDate = endDate.minusDays(endDate.getDayOfMonth()).plusDays(1);
		map.put("startDate", startDate);
		map.put("startCredit", getMemberCreditOfDate(member, startDate));
		System.out.println("s; " + startDate);

		map.put("invoiceEntries", invoiceManagement.getAllInvoiceForMemberOfLastMonth(member.getMemberId()));

		return map;
	}


	public void checkMemberIn(Long memberId) {
		Optional<Member> member = findById(memberId);
		if (member.isPresent() && !member.get().isPaused() && !member.get().isAttendant()) {
			member.ifPresent(Member::checkIn);
		}
	}

	public void checkMemberOut(Long memberId) {
		Optional<Member> member = findById(memberId);
		if (member.isPresent() && !member.get().isPaused() && member.get().isAttendant()) {
			statisticManagement.addAttendance(memberId, member.get().checkOut());
		}
	}

	public void trainFree(Member member) {
		member.trainFree();
		members.save(member);
	}

	/**
	 * Checks every day if (pauses of) memberships expired.
	 */
	@PostConstruct
	@Scheduled(cron = "0 0 12 * * *")
	public void checkMemberships() {
		LOG.info("Checking contracts..");
		for (Member member : findAllAuthorized(null)) {
			if (member.getEndDate().equals(LocalDate.now())) {
				member.disable();
			}
			if (member.isPaused() && member.getLastPause().plusDays(31).isBefore(LocalDate.now())) {
				member.unPause();
			}
		}
	}

	public String getContractTextOfMember(Member member) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		if (member.isPaused()) {
			return "Mitgliedschaft pausiert bis " + dateFormatter.format(member.getLastPause().plusDays(31));
		}
		else {
			return "Mitglied bis " + dateFormatter.format(member.getEndDate());
		}
	}

	public void pauseMembership(Member member) {
		if (member.pause(LocalDate.now())) {
			applicationEventPublisher.publishEvent(new InvoiceEvent(this, member.getMemberId(),
				InvoiceType.DEPOSIT, member.getContract().getPrice(), "Rückerstattung Pausierung Vertrag"));

			members.save(member);
		}
	}

	boolean emailExists(String email) {
		for (UserAccount userAccount : userAccounts.findAll()) {
			String userAccountEmail = userAccount.getEmail();
			if (userAccountEmail != null && userAccountEmail.equalsIgnoreCase(email)) { return true; }
		}
		return false;
	}

	public Money getMemberCreditOfDate(Member member, LocalDate date) {
		Money credit = Money.of(0, "EUR");
		if (date.isBefore(member.getMembershipStartDate())) {
			return credit;
		}

		List<InvoiceEntry> entries = invoiceManagement.getAllEntriesForMemberBefore(member.getMemberId(), date);
		for (InvoiceEntry entry : entries) {
			if (entry.getType().equals(InvoiceType.WITHDRAW)) {
				credit = credit.subtract(entry.getAmount());
			}
			if (entry.getType().equals(InvoiceType.DEPOSIT)) {
				credit = credit.add(entry.getAmount());
			}
		}
		return credit;
	}

}
