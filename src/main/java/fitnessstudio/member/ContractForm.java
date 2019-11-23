package fitnessstudio.member;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
