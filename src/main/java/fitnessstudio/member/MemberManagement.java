package fitnessstudio.member;

import fitnessstudio.staff.Staff;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import javax.transaction.Transactional;
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

	private final MemberRepository members;
	private final UserAccountManager userAccounts;


	/**
	 * Creates a new {@link MemberManagement} with the given {@link MemberRepository} and {@link UserAccountManager}.
	 *
	 * @param members      must not be {@literal null}.
	 * @param userAccounts must not be {@literal null}.
	 */
	MemberManagement(MemberRepository members, UserAccountManager userAccounts) {
		Assert.notNull(members, "MemberRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManager must not be null!");

		this.members = members;
		this.userAccounts = userAccounts;
	}

	public Member createMember(RegistrationForm form, Errors result) {
		Assert.notNull(form, "Registration form must not be null");

		var firstName = form.getFirstName();
		var lastName = form.getLastName();
		var password = Password.UnencryptedPassword.of(form.getPassword());
		var iban = form.getIban();
		var bic = form.getBic();

		if (userAccounts.findByUsername(form.getUserName()).isPresent()) {
			result.rejectValue("userName", "register.duplicate.userAccountName");
			return null;
		}

		if (iban.length() != 22){
			result.rejectValue("iban", "register.iban.wrongSize");
			return null;
		}

		if (bic.length() < 8 || bic.length() > 11){
			result.rejectValue("bic", "register.bic.wrongSize");
			return null;
		}

		var userAccount = userAccounts.create(form.getUserName(), password, MEMBER_ROLE);

		return members.save(new Member(userAccount, firstName, lastName, iban, bic));

	}

	public void deleteMember(Long memberId) {
		Optional<Member> member = findById(memberId);
		member.ifPresent(members::delete);
	}

	public void authorizeMember(Long memberId) {
		Optional<Member> member = findById(memberId);
		member.ifPresent(m -> m.getUserAccount().setEnabled(true));
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

	public List<Member> findAllAuthorized() {
		return userAccounts.findEnabled()
			.stream().map(this::findByUserAccount)
			.flatMap(member -> member.stream()
				.flatMap(Stream::of)).collect(Collectors.toList());
	}


	public Optional<Member> findById(long id) {
		return members.findById(id);
	}

	public Optional<Member> findByUserAccount(UserAccount userAccount) {
		return members.findByUserAccount(userAccount);
	}

	public void memberPayIn(Member member, Money amount) {
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

		return map;
	}
}
