package fitnessstudio.member;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;

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

	public Member() {
	}

	public Member(UserAccount userAccount, String firstName, String lastName, String iban, String bic) {
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
	}
}
