package fitnessstudio.studio;

import javax.validation.constraints.NotEmpty;

/**
 * Describes the payload to be expected to add general information of the studio.
 */
public interface StudioForm {


	@NotEmpty String getOpeningTimes();

	@NotEmpty String getAdvertisingBonus();

	@NotEmpty String getAddress();

	@NotEmpty String getName();


}
