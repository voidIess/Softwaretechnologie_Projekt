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

	public Studio() {

	}

	public Studio(String openingTimes, String advertisingBonus) {
		this.openingTimes = openingTimes;

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

	public String getAdvertisingBonus() {
		return advertisingBonus;
	}

	public void setAdvertisingBonus(String advertisingBonus) {
		this.advertisingBonus = advertisingBonus;
	}


}
