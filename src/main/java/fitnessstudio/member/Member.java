package fitnessstudio.member;

import fitnessstudio.contract.Contract;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Contract contract;

	private LocalDate endDate;
	private LocalDate lastPause;
	@CreationTimestamp private LocalDate membershipStartDate;

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

	public Money getCredit() {
		return creditAccount.getCredit();
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public CreditAccount getCreditAccount() {
		return creditAccount;
	}

	void payIn(Money amount) {
		creditAccount.payIn(amount);
	}

	void payOut(Money amount) {creditAccount.payOut(amount);}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public void authorize() {
		getUserAccount().setEnabled(true);
		endDate = LocalDate.now().plusDays(contract.getDuration());
	}

	boolean checkIn() {
		isAttendant = true;
		checkInTime = LocalDateTime.now();
		return true;
	}

	long checkOut() {
		isAttendant = false;
		long duration = Duration.between(checkInTime, LocalDateTime.now()).toMinutes();
		exerciseTime += duration;
		checkInTime = null;
		return duration;
	}

	public LocalDate getEndDate() {
		return endDate;
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

	public void setFreeTrained(boolean isFreeTrained) {
		this.isFreeTrained = isFreeTrained;
	}

	public void setLastPause(LocalDate lastPause) {
		this.lastPause = lastPause;
	}

	public void setPaused(boolean paused) {
		isPaused = paused;
	}

	void trainFree() {
		isFreeTrained = true;
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

	@Override
	public String toString() {
		return firstName + " " + lastName + "(ID: " + memberId + ")";
	}

	public boolean isAttendant() {
		return isAttendant;
	}

	void disable(){
		userAccount.setEnabled(false);
	}

	boolean pause(LocalDate now) {
		if (getLastPause() == null || getLastPause().getYear() < now.getYear()) {
			setPaused(true);
			setLastPause(now);
			setEndDate(getEndDate().plusDays(31));
			payIn(contract.getPrice());
			return true;
		}
		return false;
	}
	void unPause(){
		if (isPaused){
			setPaused(false);
		}
	}

	public boolean wasMemberLastMonth() {
		if(membershipStartDate == null){	//member wasn't saved yet
			return false;
		}
		LocalDate lastMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth());
		return membershipStartDate.isBefore(lastMonth) || membershipStartDate.isEqual(lastMonth);
	}
}