package fitnessstudio.training;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Represents the {@link Training} as form for creation.
 *
 * @author Bill Kippe
 * @version 1.0
 */
class TrainingForm {

	//@NotEmpty(message = "{TrainingForm.type.NotEmpty}")
	private final String type;

	@NotEmpty(message = "{TrainingForm.staff.NotEmpty}")
	private final String staff;

	@NotEmpty(message = "{TrainingForm.day.NotEmpty}")
	private final String day;

	@NotEmpty(message = "{TrainingForm.time.NotEmpty}")
	//@DateTimeFormat(pattern = "HH:mm")
	private final String time;

	@NotEmpty(message = "{TrainingForm.description.NotEmpty}")
	private final String description;

	@NotNull(message = "{TrainingsForm.week.NotEmpty}")
	private final Integer week;

	/**
	 * Creates a new {@link TrainingForm} with given variables.
	 *
	 * @param type			type of training
	 * @param staff			staff (trainer) of training
	 * @param day			day of training (1-7)
	 * @param time			start time of training
	 * @param description	description of training
	 * @param week			week of training
	 */
	public TrainingForm(String type, String staff, String day, String time, String description, Integer week) {
		this.type = type;
		this.staff = staff;
		this.day = day;
		this.time = time;
		this.week = week;
		this.description = description;
	}

	public Integer getWeek() {
		return week;
	}

	public String getType() {
		return type;
	}

	public String getStaff() {
		return staff;
	}

	public String getDay() {
		return day;
	}

	public String getTime() {
		return time;
	}

	public String getDescription() {
		return description;
	}
}
