package fitnessstudio.training;

import fitnessstudio.member.Member;
import fitnessstudio.staff.Staff;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
public class Training {

	@Id
	@GeneratedValue
	private long trainingId;

	@Enumerated
	private TrainingType type;

	@ManyToOne
	private Staff trainer;

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
}
