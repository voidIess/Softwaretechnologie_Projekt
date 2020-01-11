package fitnessstudio.contract;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Represents the {@link Contract} as a form for user in-/output.
 *
 * @author Bill Kippe
 * @version 1.0
 */
class ContractForm {

	@NotEmpty(message = "{ContractForm.name.NotEmpty")
	private final String name;

	@NotEmpty(message = "{ContractForm.description.NotEmpty")
	private final String description;

	@NotNull(message = "{ContractForm.price.NotEmpty")
	@Digits(fraction = 2, integer = 3)
	private final Double price;

	@NotNull(message = "{ContractForm.duration.NotEmpty}")
	@Digits(fraction = 0, integer = 4)
	private final Integer duration;

	/**
	 * Creates a new {@link ContractForm} instance with given name, description, price and duration.
	 *
	 * @param name			name of contract
	 * @param description	description of contract
	 * @param price			price of contract
	 * @param duration		duration of contract in days
	 */
	public ContractForm(String name, String description, Double price, Integer duration) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Double getPrice() {
		return price;
	}

	public Integer getDuration() {
		return duration;
	}
}
