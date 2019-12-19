package fitnessstudio.staff;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.*;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StaffManagementUnitTests {

	@Autowired
	private StaffManagement management;

	@Autowired
	private StaffRepository repository;

	@Autowired
	private UserAccountManager userAccounts;

	private Staff staff;
	private UserAccount account;
	private long staffId;

	@BeforeAll
	void setUp() {
		account = userAccounts.create("managementTestStaff", Password.UnencryptedPassword.of("123"), "managementTestStaff@email.de", Role.of("STAFF"));
		staff = new Staff(account,"Markus", "Wieland", Money.of(100, "EUR"));
	}

	@Test
	@Order(1)
	void testCreateStaff() {
		management.saveStaff(staff);
		staffId = staff.getStaffId();
		assertThat(repository.findById(staff.getStaffId())).isNotEmpty();
	}

	@Test
	@Order(2)
	void testGetAllStaff() {
		assertThat(management.getAllStaffs().size() == repository.findAll().toList().size()).isTrue();
	}

	@Test
	@Order(3)
	void testFindByUserAccount() {
		assertThat(management.findByUserAccount(account)).isNotEmpty();
	}

	@Test
	@Order(4)
	void testFindById() {
		assertThat(management.findById(staffId)).isNotEmpty();
	}

	@Test
	@Order(6)
	void testRemoveStaff() {
		management.removeStaff(staffId);
		assertThat(userAccounts.findDisabled().toList().contains(account)).isTrue();
	}

}