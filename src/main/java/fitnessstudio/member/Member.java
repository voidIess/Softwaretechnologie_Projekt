package fitnessstudio.member;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

	private LocalDateTime checkInTime;

	//Exercise time in minutes
	private long exerciseTime;

	private boolean isFreeTrained;

	private boolean isPaused;

	private boolean isAttendant;

	public Member() {
		isPaused = false;
		exerciseTime = 0;
		isFreeTrained = false;
		isAttendant = false;
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

	public boolean checkIn() {
		if(isAttendant) {
			return false;
		} else {
			isAttendant = true;
			checkInTime = LocalDateTime.now();
			return true;
		}
	}

	public long checkOut() {
		if(!isAttendant) {
			return 0;
		} else {
			isAttendant = false;
			long duration = Duration.between(checkInTime, LocalDateTime.now()).toMinutes();
			checkInTime = null;
			return duration;
		}
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

	public LocalDateTime getCheckInTime() {
		return checkInTime;
	}

	public boolean isFreeTrained() {
		return isFreeTrained;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public boolean isAttendant () {
		return isAttendant;
	}
}
