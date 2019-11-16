package fitnessstudio.member;

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
import java.util.Optional;

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

		if (userAccounts.findByUsername(form.getUserName()).isPresent()) {
			result.rejectValue("userName", "register.duplicate.userAccountName");
			return null;
		} else {
			var userAccount = userAccounts.create(form.getUserName(), password, MEMBER_ROLE);
			return members.save(new Member(userAccount, firstName, lastName));
		}
	}

	public Streamable<Member> findAll() {
		return members.findAll();
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

}
