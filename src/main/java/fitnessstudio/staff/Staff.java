package fitnessstudio.staff;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Staff {

	private Money salary;
	@Id @GeneratedValue
	private long staffId;

	private String firstName;
	private String lastName;

	@OneToOne
	private UserAccount userAccount;

	Staff() {}

	Staff(UserAccount userAccount, String firstName, String lastName, Money salary){
		this.userAccount = userAccount;
		this.firstName = firstName;
		this.lastName = lastName;
		this.salary = salary;
	}

	public void setSalary (Money money) {
		this.salary = money;
	}

	public Money getSalary () {
		return this.salary;
	}

	public long getStaffId() {
		return staffId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getUserName() {
		return userAccount.getUsername();
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}
}

