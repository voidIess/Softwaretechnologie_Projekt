package fitnessstudio.member;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public interface ContractForm {

	@NotEmpty(message = "{ContractForm.name.NotEmpty")
	String getName();

	@NotEmpty(message = "{ContractForm.description.NotEmpty")
	String getDescription();

	@NotNull(message = "{ContractForm.price.NotEmpty")
	@Digits(fraction = 2, integer = 3)
	Double getPrice();

	@NotNull(message = "{ContractForm.duration.NotEmpty}")
	@Digits(fraction = 0, integer = 4)
	Integer getDuration();

}
