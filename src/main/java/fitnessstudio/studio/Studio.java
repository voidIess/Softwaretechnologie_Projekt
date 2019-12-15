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
	private String advertisingBonus;
	private String address;

	public Studio() {

	}

	public Studio(String openingTimes, String advertisingBonus, String address) {
		this.openingTimes = openingTimes;
		this.advertisingBonus = advertisingBonus;
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getId() {
		return id;
	}

	public String getOpeningTimes() {
		return openingTimes;
	}

	public void setOpeningTimes(String openingTimes) {
		this.openingTimes = openingTimes;
	}

	public String getAdvertisingBonus() {
		return advertisingBonus;
	}

	public void setAdvertisingBonus(String advertisingBonus) {
		this.advertisingBonus = advertisingBonus;
	}


}
