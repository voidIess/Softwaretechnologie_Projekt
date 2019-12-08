package fitnessstudio.training;

import fitnessstudio.member.Member;
import fitnessstudio.staff.Staff;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

@Entity
public class Training {

	@Id
	@GeneratedValue
	private long trainingId;

	@Enumerated
	private TrainingType type;

	@ManyToOne
	private Staff trainer;

	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@ManyToOne
	private Member member;

	private int day;

	private LocalTime startTime;

	private int duration;

	private String description;

	@Enumerated
	private TrainingState state;

	public Training() {
		state = TrainingState.REQUESTED;
	}

	public Training(TrainingType type, Staff trainer, Member member, int day, LocalTime startTime, int duration, String description) {
		this();
		this.type = type;
		this.trainer = trainer;
		this.member = member;
		this.startTime = startTime;
		this.duration = duration;
		this.description = description;
		this.day = day;
	}

	public long getTrainingId() {
		return trainingId;
	}

	public Staff getTrainer() {
		return trainer;
	}

	public Member getMember() {
		return member;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public int getDuration() {
		return duration;
	}

	public String getDescription() {
		return description;
	}

	public TrainingType getType() {
		return type;
	}

	public TrainingState getState() {
		return state;
	}

	public int getDay() {
		return day;
	}

	public void decline() {
		if (getState().equals(TrainingState.REQUESTED)) {
			state = TrainingState.DECLINED;
			if (type.equals(TrainingType.TRIAL)) {
				member.setFreeTrained(false);
			}
		}
	}

	public void accept() {
		if (getState().equals(TrainingState.REQUESTED)) {
			state = TrainingState.ACCEPTED;
		}
	}

	public void end() {
		if (getState().equals(TrainingState.ACCEPTED)) {
			state = TrainingState.ENDED;
		}
	}

	public String getDayAsString(){
		DayOfWeek dow = DayOfWeek.of(getDay()+1);
		return dow.getDisplayName(TextStyle.FULL, Locale.GERMAN);
	}
}
