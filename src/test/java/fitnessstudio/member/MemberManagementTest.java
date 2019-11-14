package fitnessstudio.member;

import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MemberManagementTests {

	@Test
	void createsUserAccountWhenCreatingAMember() {

		MemberRepository repository = mock(MemberRepository.class);
		when(repository.save(any())).then(i -> i.getArgument(0));

		UserAccountManager userAccountManager = mock(UserAccountManager.class);
		when(userAccountManager.create(any(), any(), any())).thenReturn(new UserAccount());

		MemberManagement memberManagement = new MemberManagement(repository, userAccountManager);

		RegistrationForm form = new RegistrationForm("FirstName", "LastName", "UserName", "Password");
		Member member = memberManagement.createMember(form, null);

		verify(userAccountManager, times(1))
			.create(eq(form.getUserName()),
				eq(Password.UnencryptedPassword.of(form.getPassword())),
				eq(MemberManagement.MEMBER_ROLE));


		assertThat(member.getUserAccount()).isNotNull();
	}

}