package fitnessstudio.staff;

import javax.validation.constraints.NotBlank;

public class RosterEntryForm {

	private final @NotBlank String name;
	private final @NotBlank String duration;

	public RosterEntryForm(String name, String duration){
		this.name = name;
		this.duration = duration;
	}

}
