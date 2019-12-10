package fitnessstudio.training;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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

	public TrainingForm(String type, String staff, String day, String time, String description, Integer week) {
		this.type = type;
		this.staff = staff;
		this.day = day;
		this.time = time;
		this.week = week;
		this.description = description;
	}

	public Integer getWeek() {return week;}
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
