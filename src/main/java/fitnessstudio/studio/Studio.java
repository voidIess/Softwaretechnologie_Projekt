package fitnessstudio.studio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class Studio {

	private @Id
	@GeneratedValue
	long id;


	@Column( columnDefinition="LONGVARCHAR")
	private String openingTimes;
	private String advertisingBonus;
	private String address;
	private String name;

	public Studio() {

	}

	public Studio(String openingTimes, String advertisingBonus, String address, String name) {
		this.openingTimes = openingTimes;
		this.advertisingBonus = advertisingBonus;
		this.address = address;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
