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
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UnitTests fuer Staff Package
 */
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
		staff = new Staff(account, "Markus", "Wieland", Money.of(100, "EUR"));
	}

	/**
	 * U-7-01
	 */
	@Test
	@Order(1)
	void testCreateStaff() {
		management.saveStaff(staff);
		staffId = staff.getStaffId();
		assertThat(repository.findById(staff.getStaffId())).isNotEmpty();
	}

	/**
	 * U-7-02
	 */
	@Test
	void setFirstNameTest() {
		staff.setFirstName("cristiano");
		assertNotNull(staff.getFirstName());
	}

	/**
	 * U-7-03
	 */
	@Test
	void setLastNameTest() {
		staff.setLastName("ronaldo");
		assertNotNull(staff.getLastName());
	}

	/**
	 * U-7-04
	 */
	@Test
	void setSalaryTest() {
		staff.setSalary(Money.of(450000, "EUR"));
		assertNotNull(staff.getLastName());
	}

	/**
	 * U-7-05
	 */
	@Test
	void getUsernameTest() {
		assertNotNull(staff.getUserName());
	}

	/**
	 * U-7-06
	 */
	@Test
	@Order(2)
	void testGetAllStaff() {
		assertThat(management.getAllStaffs().size() == repository.findAll().toList().size()).isTrue();
	}

	/**
	 * U-7-07
	 */
	@Test
	@Order(3)
	void testFindByUserAccount() {
		assertThat(management.findByUserAccount(account)).isNotEmpty();
	}

	/**
	 * U-7-08
	 */
	@Test
	@Order(4)
	void testFindById() {
		assertThat(management.findById(staffId)).isNotEmpty();
	}

	/**
	 * U-7-09
	 */
	@Test
	@Order(6)
	void testRemoveStaff() {
		management.removeStaff(staffId);
		assertThat(userAccounts.findDisabled().toList().contains(account)).isTrue();
	}

}
