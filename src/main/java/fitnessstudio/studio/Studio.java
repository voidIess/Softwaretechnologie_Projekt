package fitnessstudio.studio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Studio {

	private @Id
	@GeneratedValue
	long id;

	private String openingTimes;
	private String contractTerm;
	private String monthlyFees;
	private String advertisingBonus;

	public Studio() {

	}

	public Studio(String openingTimes, String contractTerm, String monthlyFees, String advertisingBonus) {
		this.openingTimes = openingTimes;
		this.contractTerm = contractTerm;
		this.monthlyFees = monthlyFees;
		this.advertisingBonus = advertisingBonus;
	}

	public long getId() {
		return id;
	}

	public String getOpeningTimes() {
		return openingTimes;
	}

	public void setOpeningTimes(String openningTimes) {
		this.openingTimes = openningTimes;
	}

	public String getContractTerm() {
		return contractTerm;
	}

	public void setContractTerm(String contractTerm) {
		this.contractTerm = contractTerm;
	}

	public String getMonthlyFees() {
		return monthlyFees;
	}

	public void setMonthlyFees(String monthlyFees) {
		this.monthlyFees = monthlyFees;
	}

	public String getAdvertisingBonus() {
		return advertisingBonus;
	}

	public void setAdvertisingBonus(String advertisingBonus) {
		this.advertisingBonus = advertisingBonus;
	}
}
