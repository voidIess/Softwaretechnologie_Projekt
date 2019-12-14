package fitnessstudio.staff;

import org.hibernate.annotations.CreationTimestamp;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
public class Staff {

	@Id
	@GeneratedValue
	private long staffId;

	private String firstName;
	private String lastName;

	private Money salary;

	@OneToOne
	private UserAccount userAccount;


	@CreationTimestamp
	private LocalDate startDate;

	public Staff() {
	}

	public Staff(UserAccount userAccount, String firstName, String lastName, Money salary) {
		this.userAccount = userAccount;
		this.firstName = firstName;
		this.lastName = lastName;
		this.salary = salary;
	}

	public Money getSalary() {
		return this.salary;
	}

	public void setSalary(Money money) {
		this.salary = money;
	}

	public long getStaffId() {
		return staffId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserName() {
		return userAccount.getUsername();
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	@Override
	public String toString() {
		return lastName + ", " + firstName;
	}

	public boolean workedLastMonth() {
		if (startDate == null) {    //staff wasn't saved yet
			return false;
		}
		LocalDate lastMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth());
		return startDate.isBefore(lastMonth) || startDate.isEqual(lastMonth);
	}
}

