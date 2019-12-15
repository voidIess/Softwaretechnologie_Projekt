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

	private Money salary;

	@OneToOne
	private UserAccount userAccount;


	@CreationTimestamp
	private LocalDate startDate;

	public Staff() {
	}

	public Staff(UserAccount userAccount, String firstName, String lastName, Money salary) {
		this.userAccount = userAccount;
		this.userAccount.setFirstname(firstName);
		this.userAccount.setLastname(lastName);
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
		return this.userAccount.getFirstname();
	}

	public void setFirstName(String firstName) {
		this.userAccount.setFirstname(firstName);
	}

	public String getLastName() {
		return this.userAccount.getLastname();
	}

	public void setLastName(String lastName) {
		this.userAccount.setLastname(lastName);
	}

	public String getUserName() {
		return userAccount.getUsername();
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	@Override
	public String toString() {
		return this.userAccount.getFirstname() + ", " + this.userAccount.getLastname();
	}

	public boolean workedLastMonth() {
		if (startDate == null) {    //staff wasn't saved yet
			return false;
		}
		LocalDate lastMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth());
		return startDate.isBefore(lastMonth) || startDate.isEqual(lastMonth);
	}
}

