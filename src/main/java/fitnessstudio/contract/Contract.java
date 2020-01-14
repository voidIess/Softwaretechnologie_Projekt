package fitnessstudio.contract;

import org.javamoney.moneta.Money;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Represents a contract for the membership.
 *
 * @author Bill Kippe
 * @version 1.0
 */
@Entity
public class Contract {

	@Id
	@GeneratedValue
	private long contractId;

	private String name;
	private String description;
	/**
	 * Represents the contracts price as Money.
	 * @see Money
	 */
	private Money price;
	/**
	 * Represents the contract's duration in days.
	 */
	private int duration;

	public Contract() {
	}

	/**
	 * Creates a new {@link Contract} instance with the given name, description price and duration.
	 *
	 * @param name			name of contract
	 * @param description 	description of contract
	 * @param price 		price of contract
	 * @param duration 		duration of contract in days
	 */
	public Contract(String name, String description, Money price, int duration) {
		this();

		this.name = name;
		this.description = description;
		this.price = price;
		this.duration = duration;
	}

	/**
	 * Updates the contract with the new name, description, price and duration.
	 *
	 * @param name			new name of contract
	 * @param description	new description of contract
	 * @param price			new price of contract
	 * @param duration		new duration of contract in days
	 */
	public void update(String name, String description, Money price, int duration) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.duration = duration;
	}

	public long getContractId() {
		return contractId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Money getPrice() {
		return price;
	}

	public int getDuration() {
		return duration;
	}
}
