package fitnessstudio.member;

import fitnessstudio.contract.Contract;
import fitnessstudio.contract.ContractManagement;
import fitnessstudio.email.EmailService;
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

/**
 * Implementation of business logic related to {@link Member}s.
 *
 * @author Bill Kippe
 * @author Lea Häusler
 */
@Service
@Transactional
public class MemberManagement {

	public static final Role MEMBER_ROLE = Role.of("MEMBER");
	private static final Logger LOG = LoggerFactory.getLogger(MemberManagement.class);

	private final MemberRepository members;
	private final UserAccountManager userAccounts;
	private final ContractManagement contractManagement;
	private final StudioService studioService;
	private final StatisticManagement statisticManagement;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final InvoiceManagement invoiceManagement;
	private final EmailService emailService;

	/**
	 * Creates a new {@link MemberManagement} instance with the given parameters.
	 *
	 * @param members 					Must not be {@literal null}.
	 * @param userAccounts				Must not be {@literal null}.
	 * @param contractManagement		Must not be {@literal null}.
	 * @param studioService				Must not be {@literal null}.
	 * @param statisticManagement		Must not be {@literal null}.
	 * @param applicationEventPublisher	Must not be {@literal null}.
	 * @param invoiceManagement			Must not be {@literal null}.
	 * @param emailService				Must not be {@literal null}.
	 */
	MemberManagement(MemberRepository members, UserAccountManager userAccounts, ContractManagement contractManagement,
					 StudioService studioService, StatisticManagement statisticManagement,
					 ApplicationEventPublisher applicationEventPublisher, InvoiceManagement invoiceManagement,
					 EmailService emailService) {

		Assert.notNull(members, "MemberRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManager must not be null!");
		Assert.notNull(contractManagement, "ContractManagement must not be null!");
		Assert.notNull(studioService, "StudioService must not be null!");
		Assert.notNull(statisticManagement, "StatisticManagement must not be null!");
		Assert.notNull(applicationEventPublisher, "ApplicationEventPublisher must not be null!");
		Assert.notNull(invoiceManagement, "InvoiceManagement must not be null!");
		Assert.notNull(emailService, "EmailService must not be null!");

		this.members = members;
		this.userAccounts = userAccounts;
		this.contractManagement = contractManagement;
		this.studioService = studioService;
		this.statisticManagement = statisticManagement;
		this.applicationEventPublisher = applicationEventPublisher;
		this.invoiceManagement = invoiceManagement;
		this.emailService = emailService;
	}

	/**
	 * Method which saves a new {@link Member} instance with parameters of the given form to the {@link MemberRepository}
	 * or adds errors to the given result.
	 *
	 * @param form		form of the registration input
	 * @param result	errors
	 * @return created {@link Member} instance
	 */
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

		var bonusCode = form.getBonusCode();
		if (!bonusCode.isEmpty()) {
			Optional<Member> receiverOptional = members.findById(Long.parseLong(bonusCode));
			if (receiverOptional.isEmpty()) {
				result.rejectValue("bonusCode", "register.bonusCode.notFound");
			} else {
				Member receiver = receiverOptional.get();
				Money bonus = Money.of(new BigDecimal(studioService.getStudio().getAdvertisingBonus()),
					"EUR");

				receiver.payIn(bonus);
				members.save(receiver);

				applicationEventPublisher.publishEvent(new InvoiceEvent(this, receiver.getMemberId(),
					InvoiceType.DEPOSIT, bonus, "Anwerbebonus"));
			}

		}


		Optional<Contract> contractOptional = contractManagement.findById(contract);
		if (contractOptional.isEmpty()) {
			result.rejectValue("contract", "register.contract.notFound");
		}

		if (result != null && result.hasErrors()) {
			return null;
		}

		var userAccount = userAccounts.create(form.getUserName(), password, email, MEMBER_ROLE);
		var member = new Member(userAccount, firstName, lastName, iban, bic);
		member.setContract(contractOptional.get());
		return members.save(member);

	}

	/**
	 * Senden der Email an den Freund mit der E-Mail
	 * @param form Formular mit den Angaben zur Einladung
	 */
	public void inviteFriend(FriendInviteForm form){
		emailService.sendFriendInvite(form.getEmail(), form.getFriendsName(), form.getFriendsId());
	}

	/**
	 * Deletes the given member from the {@link MemberRepository}.
	 * @param memberId ID of member
	 */
	public void deleteMember(Long memberId) {
		Optional<Member> member = findById(memberId);
		member.ifPresent(members::delete);
	}

	/**
	 * Enables member and sends acceptation email.
	 * @param memberId ID of member
	 */
	public void authorizeMember(Long memberId) {
		Optional<Member> optionalMember = findById(memberId);
		optionalMember.ifPresent(member -> {
			member.authorize();
			emailService.sendAccountAcceptation(member.getUserAccount().getEmail(), member.getFirstName());
			statisticManagement.addRevenue(memberId, member.getContract().getContractId());
		});
	}

	/**
	 * Changes attributes of member to inputs from the given form
	 * or adds errors to the given result.
	 *
	 * @param memberId	ID of member
	 * @param form		form of input to edit member
	 * @param result	errors
	 */
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

	/**
	 * Returns an editing form with the current member attributes.
	 *
	 * @param member	member
	 * @param form		input form to edit member
	 * @return form with current member attributes
	 */
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

	/**
	 * Returns all {@link Member}s saved in the {@link MemberRepository}.
	 * @return all {@link Member}s
	 */
	public Streamable<Member> findAll() {
		return members.findAll();
	}

	/**
	 * Returns all {@link Member}s saved in the {@link MemberRepository} who aren't enabled.
	 * @return all not enabled {@link Member}s
	 */
	public List<Member> findAllUnauthorized() {
		return userAccounts.findDisabled()
			.stream().map(this::findByUserAccount)
			.flatMap(member -> member.stream()
				.flatMap(Stream::of)).collect(Collectors.toList());
	}

	/**
	 * Returns all enabled {@link Member}s saved in the {@link MemberRepository}.
	 * @return all enabled {@link Member}s
	 */
	public List<Member> findAllAuthorized(String search) {
		if (search == null) {
			search = "";
		}
		String finalSearch = search;

		return userAccounts.findEnabled()
			.stream().map(this::findByUserAccount)
			.flatMap(Optional::stream)
			.filter(member -> String.valueOf(member.getMemberId()).startsWith(finalSearch))
			.collect(Collectors.toList());
	}

	/**
	 * Returns all attendant {@link Member}s saved in the {@link MemberRepository}.
	 * @return all attendant {@link Member}s
	 */
	public List<Member> findAllAttendant() {
		return findAll().filter(Member::isAttendant).toList();
	}

	/**
	 * Returns {@link Member} with the given memberId if saved in the {@link MemberRepository}.
	 * @return {@link Member} with given memberId
	 */
	public Optional<Member> findById(long id) {
		return members.findById(id);
	}

	/**
	 * Returns {@link Member} referred to the given user account if saved in the {@link MemberRepository}.
	 * @return {@link Member} with given user account
	 */
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

	/**
	 * Creates and returns the values to be printed on the invoice PDF
	 * of the member referred to the given user account.
	 * @see fitnessstudio.pdf.InvoicePdfGenerator
	 *
	 * @param account logged in account
	 * @return values to be printed on invoice PDF
	 */
	Map<String, Object> createPdfInvoice(UserAccount account) {

		Optional<Member> opt = members.findByUserAccount(account);
		Assert.isTrue(opt.isPresent(), "There is no existing member for this account");
		Member member = opt.get();

		Map<String, Object> map = new HashMap<>();

		map.put("member", member);

		LocalDate endDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth());
		map.put("endDate", endDate);
		map.put("endCredit", getMemberCreditOfDate(member, endDate));

		LocalDate startDate = endDate.minusDays(endDate.getDayOfMonth()).plusDays(1);
		map.put("startDate", startDate);
		map.put("startCredit", getMemberCreditOfDate(member, startDate));

		map.put("invoiceEntries", invoiceManagement.getAllInvoiceForMemberOfLastMonth(member.getMemberId()));

		return map;
	}

	/**
	 * Sets member with the given ID to attendant.
	 * @param memberId ID of member
	 */
	public void checkMemberIn(Long memberId) {
		Optional<Member> member = findById(memberId);
		if (member.isPresent() && !member.get().isPaused() && !member.get().isAttendant()) {
			member.ifPresent(Member::checkIn);
		}
	}

	/**
	 * Sets member with the given ID to not attendant.
	 * @param memberId ID of member
	 */
	public void checkMemberOut(Long memberId) {
		Optional<Member> member = findById(memberId);
		if (member.isPresent() && !member.get().isPaused() && member.get().isAttendant()) {
			statisticManagement.addAttendance(memberId, member.get().checkOut());
		}
	}

	/**
	 * Calls {@link Member#trainFree()} of given member and saves in {@link MemberRepository}.
	 * @param member member to be trail trained
	 */
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
				statisticManagement.deleteRevenue(member.getMemberId());
			}
			if (member.isPaused() && member.getLastPause().plusDays(31).isBefore(LocalDate.now())) {
				member.unPause();
				statisticManagement.addRevenue(member.getMemberId(), member.getContract().getContractId());
			}
		}
	}

	/**
	 * Returns text with current state of {@link Contract} of the given member.
	 *
	 * @param member member
	 * @return "Mitgliedschaft pausiert bis &lt;date&gt;" or "Mitglied bis &lt;date&gt;"
	 */
	public String getContractTextOfMember(Member member) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		if (member.isPaused()) {
			return "Mitgliedschaft pausiert bis " + dateFormatter.format(member.getLastPause().plusDays(31));
		} else {
			return "Mitglied bis " + dateFormatter.format(member.getEndDate());
		}
	}

	/**
	 * Pauses the membership of the given member for 31 days and saves to {@link MemberRepository}.
	 * @param member member to pause
	 */
	public void pauseMembership(Member member) {
		if (member.pause(LocalDate.now())) {
			applicationEventPublisher.publishEvent(new InvoiceEvent(this, member.getMemberId(),
				InvoiceType.DEPOSIT, member.getContract().getPrice(), "Rückerstattung Pausierung Vertrag"));

			members.save(member);
			statisticManagement.deleteRevenue(member.getMemberId());
		}
	}

	/**
	 * Checks if user account with the given email exists in the {@link MemberRepository}.
	 *
	 * @param email email address to be checked
	 * @return boolean whether email exists
	 */
	boolean emailExists(String email) {
		for (UserAccount userAccount : userAccounts.findAll()) {
			String userAccountEmail = userAccount.getEmail();
			if (userAccountEmail != null && userAccountEmail.equalsIgnoreCase(email)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the {@link CreditAccount} balance of the given member on the given date in euro.
	 *
	 * @param member	member to get credit
	 * @param date		date to get credit
	 * @return balance of {@link CreditAccount}
	 */
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
