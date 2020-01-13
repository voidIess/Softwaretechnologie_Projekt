package fitnessstudio.member;

import fitnessstudio.contract.Contract;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Class which represents a member of the studio
 */
@Entity
public class Member {

	@Id
	@GeneratedValue
	private long memberId;

	private String firstName;
	private String lastName;

	@OneToOne
	private UserAccount userAccount;

	/**
	 * Represents the online credit account of the member.
	 */
	@Embedded
	private CreditAccount creditAccount;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Contract contract;

	private LocalDate endDate;
	private LocalDate lastPause;

	private LocalDate membershipStartDate;

	/**
	 * Revers to the last time the member entered the studio.
	 */
	private LocalDateTime checkInTime;

	/**
	 * Revers to the total time the member spend in the studio in minutes.
	 */
	private long exerciseTime;

	/**
	 * Records whether the member had a trial training.
	 */
	private boolean isFreeTrained;

	private boolean isPaused;

	private boolean isAttendant;

	public Member() {
		membershipStartDate = LocalDate.now();
		isPaused = false;
		exerciseTime = 0;
		isFreeTrained = false;
		isAttendant = false;
	}

	/**
	 * Creates a new {@link Member} instance with the given parameters.
	 *
	 * @param userAccount	user account with login data of member
	 * @param firstName		first name of member
	 * @param lastName		last name of member
	 * @param iban			iban of member
	 * @param bic			bic of member
	 */
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

	/**
	 * Loads the given amount to the online {@link CreditAccount} of the member.
	 * @param amount amount in euro
	 */
	void payIn(Money amount) {
		creditAccount.payIn(amount);
	}

	/**
	 * Withdraws the given amount from the online {@link CreditAccount} of the member.
	 * @param amount amount in euro
	 */
	void payOut(Money amount) {
		creditAccount.payOut(amount);
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	/**
	 * Enables the user account of the member.
	 */
	public void authorize() {
		getUserAccount().setEnabled(true);
		endDate = LocalDate.now().plusDays(contract.getDuration());
	}

	/**
	 * Sets member to attendant.
	 * @return boolean whether checkin was successful
	 */
	boolean checkIn() {
		if (isPaused) {
			return false;
		}

		isAttendant = true;
		checkInTime = LocalDateTime.now();
		return isAttendant;
	}

	/**
	 * Sets member to absent.
	 * @return duration of the visit in minutes
	 */
	long checkOut() {
		if (!isAttendant) {
			return 0L;
		}
		isAttendant = false;
		long duration = Duration.between(checkInTime, LocalDateTime.now()).toMinutes();
		exerciseTime += duration;
		checkInTime = null;

		return duration;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public LocalDate getLastPause() {
		return lastPause;
	}

	public void setLastPause(LocalDate lastPause) {
		this.lastPause = lastPause;
	}

	public long getExerciseTime() {
		return exerciseTime;
	}

	public LocalDateTime getCheckInTime() {
		return checkInTime;
	}

	public LocalDate getMembershipStartDate() {
		return membershipStartDate;
	}

	public boolean isFreeTrained() {
		return isFreeTrained;
	}

	public void setFreeTrained(boolean isFreeTrained) {
		this.isFreeTrained = isFreeTrained;
	}

	/**
	 * Sets {@link Member#isFreeTrained} to true.
	 */
	void trainFree() {
		isFreeTrained = true;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(boolean paused) {
		isPaused = paused;
	}

	/**
	 * Returns whether the members contract is expired.
	 * @return boolean whether members contract is expired
	 */
	public boolean isEnded() {
		return endDate.isBefore(LocalDate.now());
	}

	/**
	 * Compares two members by ID.
	 * @param o	object to compare with
	 * @return	boolean whether o equals this member
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Member member = (Member) o;

		return memberId == member.memberId;
	}

	/**
	 * Returns specific hash code of member calculated with memberID.
	 * @return hash code of member
	 */
	@Override
	public int hashCode() {
		return (int) (memberId ^ (memberId >>> 32));
	}

	/**
	 * Defines text to return for member.
	 * @return string "&lt;firstName&gt; &lt;lastName&gt; (ID: &lt;memberID&gt;)"
	 */
	@Override
	public String toString() {
		return firstName + " " + lastName + " (ID: " + memberId + ")";
	}

	public boolean isAttendant() {
		return isAttendant;
	}

	/**
	 * Calls function {@link UserAccount#setEnabled(boolean)} with parameter false.
	 */
	void disable() {
		userAccount.setEnabled(false);
	}

	/**
	 * Pauses membership for 31 days.
	 * @param now date
	 * @return boolean whether pause was successful
	 */
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

	/**
	 * Ends pause of membership.
	 */
	void unPause() {
		if (isPaused) {
			setPaused(false);
		}
	}

	/**
	 * Checks if membership started before current month.
	 * @return boolean whether member was member last month
	 */
	public boolean wasMemberLastMonth() {
		if (membershipStartDate == null) {    //member wasn't saved yet
			return false;
		}
		LocalDate lastMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth());
		return membershipStartDate.isBefore(lastMonth) || membershipStartDate.isEqual(lastMonth);
	}
}