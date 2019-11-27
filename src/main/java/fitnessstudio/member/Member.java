package fitnessstudio.member;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Member {

	@Id
	@GeneratedValue
	private long memberId;

	private String firstName;
	private String lastName;

	@OneToOne
	private UserAccount userAccount;

	@Embedded
	private CreditAccount creditAccount;

	@ManyToOne
	private Contract contract;

	private LocalDate startDate;
	private LocalDate lastPause;

	//Exercise time in minutes
	private long exerciseTime;

	private boolean isFreeTrained;

	private boolean isPaused;

	public Member() {
		isPaused = false;
		exerciseTime = 0;
		isFreeTrained = false;
	}

	public Member(UserAccount userAccount, String firstName, String lastName, String iban, String bic) {
		this();

		this.userAccount = userAccount;
		this.creditAccount = new CreditAccount(iban, bic);
		this.firstName = firstName;
		this.lastName = lastName;
		userAccount.setEnabled(false);
	}

	public long getMemberId() {
		return memberId;
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

	public Money getCredit() {
		return creditAccount.getCredit();
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public CreditAccount getCreditAccount() {return creditAccount;}

	public void payIn(Money amount){
		creditAccount.payIn(amount);
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract){
		this.contract = contract;
	}

	public void authorize(){
		getUserAccount().setEnabled(true);
		getContract().subscribe(this);
		startDate = LocalDate.now();
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getLastPause() {
		return lastPause;
	}

	public long getExerciseTime() {
		return exerciseTime;
	}

	public boolean isFreeTrained() {
		return isFreeTrained;
	}

	public boolean isPaused() {
		return isPaused;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Member member = (Member) o;

		return memberId == member.memberId;

	}

	@Override
	public int hashCode() {
		return (int) (memberId ^ (memberId >>> 32));
	}
}
