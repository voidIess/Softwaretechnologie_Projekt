package fitnessstudio.studio;

import javax.validation.constraints.NotEmpty;

public interface StudioForm {


	@NotEmpty String getOpeningTimes();

	@NotEmpty String getAdvertisingBonus();

	@NotEmpty String getAddress();

	@NotEmpty String getName();


}
