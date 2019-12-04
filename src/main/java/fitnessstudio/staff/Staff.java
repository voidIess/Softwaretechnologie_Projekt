package fitnessstudio.staff;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;

@Entity
public class Staff {

	private Money salary;
	@Id @GeneratedValue
	private long staffId;

	private String firstName;
	private String lastName;

	@OneToOne
	private UserAccount userAccount;

	//@OneToMany()
	//private RosterEntry[][] rosterEntries = new RosterEntry[7][];

	public Staff() {}

	public Staff(UserAccount userAccount, String firstName, String lastName, Money salary){
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

	@Override
	public String toString(){
		return lastName + ", " + firstName + " (ID: " + staffId + ")";
	}
}

