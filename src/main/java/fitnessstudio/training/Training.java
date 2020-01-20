package fitnessstudio.training;

import fitnessstudio.member.Member;
import fitnessstudio.staff.Staff;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Represents a Training in the Gym.
 *
 * @author Bill Kippe
 * @author Markus Wieland
 * @version 1.0
 */
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

	private int week;

	private String startTime;

	private int duration;

	private String description;

	@Enumerated
	private TrainingState state;

	public Training() {
		state = TrainingState.REQUESTED;
	}

	/**
	 * Creates a new {@link Training} instance.
	 *
	 * @param type			type of Training
	 * @param trainer		trainer of Training
	 * @param member		member of Training
	 * @param day			day of Training (1-7)
	 * @param startTime		start time of training
	 * @param duration		duration of training in minutes
	 * @param description	description of training
	 * @param week			week of training
	 */
	public Training(TrainingType type, Staff trainer, Member member, int day, String startTime, int duration,
					String description, int week) {
		this();
		this.type = type;
		this.trainer = trainer;
		this.member = member;
		this.startTime = startTime;
		this.duration = duration;
		this.description = description;
		this.day = day;
		this.week = week;
	}

	public int getWeek() {return week;}

	public long getTrainingId() {
		return trainingId;
	}

	public Staff getTrainer() {
		return trainer;
	}

	public Member getMember() {
		return member;
	}

	public String getStartTime() {
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

	/**
	 * Decline Training when it's requested.
	 *
	 * @return boolean if its able to be declined.
	 */
	public boolean decline() {
		if (getState().equals(TrainingState.REQUESTED)) {
			state = TrainingState.DECLINED;
			if (type.equals(TrainingType.TRIAL)) {
				member.setFreeTrained(false);
			}
			return true;
		}
		return false;
	}

	/**
	 * Accept Training when it's requested.
	 *
	 * @return boolean if its able to te accepted
	 */
	public boolean accept() {
		if (getState().equals(TrainingState.REQUESTED)) {
			state = TrainingState.ACCEPTED;
			return true;
		}
		return false;
	}

	/**
	 * Method to end Training.
	 *
	 * @return boolean if its able to be ended.
	 */
	public boolean end() {
		if (getState().equals(TrainingState.ACCEPTED)) {
			state = TrainingState.ENDED;
			return true;
		}
		return false;
	}

	public String getDayAsString(){
		return DayOfWeek.of(getDay()+1).getDisplayName(TextStyle.FULL, Locale.GERMAN);
	}

}
