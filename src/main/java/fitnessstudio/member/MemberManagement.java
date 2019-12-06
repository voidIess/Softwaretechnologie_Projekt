package fitnessstudio.member;

import fitnessstudio.contract.Contract;
import fitnessstudio.contract.ContractManagement;
import fitnessstudio.invoice.InvoiceEvent;
import fitnessstudio.invoice.InvoiceType;
import fitnessstudio.statistics.StatisticManagement;
import fitnessstudio.studio.StudioService;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import javax.transaction.Transactional;
import java.math.BigDecimal;
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

	private final ApplicationEventPublisher applicationEventPublisher;
	private final MemberRepository members;
	private final UserAccountManager userAccounts;
	private final ContractManagement contractManagement;
	private final StudioService studioService;
	private final StatisticManagement statisticManagement;

	MemberManagement(MemberRepository members, UserAccountManager userAccounts, ContractManagement contractManagement,
					 StudioService studioService, StatisticManagement statisticManagement,
					 ApplicationEventPublisher applicationEventPublisher) {
		Assert.notNull(members, "MemberRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManager must not be null!");
		Assert.notNull(contractManagement, "ContractManagement must not be null!");
		Assert.notNull(studioService, "StudioService must not be null!");
		Assert.notNull(statisticManagement, "StatisticManagement must not be null!");
		Assert.notNull(applicationEventPublisher, "ApplicationEventPublisher should not be null!");

		this.members = members;
		this.userAccounts = userAccounts;
		this.contractManagement = contractManagement;
		this.studioService = studioService;
		this.statisticManagement = statisticManagement;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public Member createMember(RegistrationForm form, Errors result) {
		Assert.notNull(form, "Registration form must not be null");

		var firstName = form.getFirstName();
		var lastName = form.getLastName();
		var password = Password.UnencryptedPassword.of(form.getPassword());
		var iban = form.getIban();
		var bic = form.getBic();
		var contract = form.getContract();

		if (userAccounts.findByUsername(form.getUserName()).isPresent()) {
			result.rejectValue("userName", "register.duplicate.userAccountName");
			return null;
		}

		if (iban.length() != 22) {
			result.rejectValue("iban", "register.iban.wrongSize");
			return null;
		}

		if (bic.length() < 8 || bic.length() > 11) {
			result.rejectValue("bic", "register.bic.wrongSize");
			return null;
		}

		if (contract == null) {
			result.rejectValue("contract", "register.contract.missing");
			return null;
		}

		var bonusCode = form.getBonusCode();
		if (!bonusCode.isEmpty()) {
			Optional<Member> receiverOptional = members.findById(Long.parseLong(bonusCode));
			if (receiverOptional.isEmpty()) {
				result.rejectValue("bonusCode", "register.bonusCode.notFound");
				return null;
			} else {
				Member receiver = receiverOptional.get();

				receiver.payIn(Money.of(new BigDecimal(studioService.getStudio().getAdvertisingBonus()), "EUR"));
				members.save(receiver);
			}

		}

		var userAccount = userAccounts.create(form.getUserName(), password, MEMBER_ROLE);
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
		if (search == null) search = "";
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

	public void memberPayIn(Member member, Money amount) {
		applicationEventPublisher.publishEvent(new InvoiceEvent(this, member.getMemberId(), InvoiceType.DEPOSIT, amount,
			"Online Einzahlung auf Account"));
		member.payIn(amount);
		members.save(member);
	}

	Map<String, Object> createPdfInvoice(UserAccount account) {

		Optional<Member> opt = members.findByUserAccount(account);
		Assert.isTrue(opt.isPresent(), "There is no existing member for this account");
		Member member = opt.get();

		Map<String, Object> map = new HashMap<>();
		map.put("id", member.getMemberId());
		map.put("firstName", member.getFirstName());
		map.put("lastName", member.getLastName());
		map.put("contract", member.getContract());

		return map;
	}

	public void checkMemberIn(Long memberId) {
		Optional<Member> member = findById(memberId);
		member.ifPresent(Member::checkIn);
	}

	public void checkMemberOut(Long memberId) {
		Optional<Member> member = findById(memberId);
		member.ifPresent(m -> statisticManagement.addAttendance(memberId, m.checkOut()));
	}

	public void trainFree(Member member) {
		member.trainFree();
		members.save(member);
	}
}
