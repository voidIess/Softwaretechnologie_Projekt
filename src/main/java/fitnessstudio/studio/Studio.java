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


	@Column(columnDefinition = "LONGVARCHAR")
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

	/**
	 * @return name of the studio
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name sets the name of the studio
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return address of the studio
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address sets the address of the studio
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return id of the studio
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return opening times of the studio
	 */
	public String getOpeningTimes() {
		return openingTimes;
	}

	/**
	 * @param openingTimes sets the opening time of the studio
	 */
	public void setOpeningTimes(String openingTimes) {
		this.openingTimes = openingTimes;
	}

	/**
	 * @return advertising bonus of the studio
	 */
	public String getAdvertisingBonus() {
		return advertisingBonus;
	}

	/**
	 * @param advertisingBonus set the advertising bonus of the studio
	 */
	public void setAdvertisingBonus(String advertisingBonus) {
		this.advertisingBonus = advertisingBonus;
	}


}
