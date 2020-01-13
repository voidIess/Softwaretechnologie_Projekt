package fitnessstudio.statistics;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Class which represents the behavior of the members on a certain day.
 *
 * @author Lea Haeusler
 */
@Entity
public class Attendance {

	@Id
	private LocalDate date;

	/**
	 * Represents all members who visited the studio this day.
	 */
	@ElementCollection
	private Set<Long> memberIds;

	/**
	 * Represents the durations of the visits in minutes.
	 */
	@ElementCollection
	private List<Long> attendanceTimes;

	/**
	 * Creates a new {@link Attendance} instance for today.
	 */
	public Attendance() {
		memberIds = new HashSet<>();
		attendanceTimes = new LinkedList<>();
		date = LocalDate.now();
	}

	/**
	 * Creates a new {@link Attendance} instance for the given day.
	 *
	 * @param date		date of the Attendance
	 */
	public Attendance(LocalDate date) {
		this();
		this.date = date;
	}

	/**
	 * Returns the number of members who visited the studio this day.
	 *
	 * @return size of the {@link HashSet} of members
	 */
	public int getMemberAmount() {
		return memberIds.size();
	}

	/**
	 * Returns the average duration one member stayed in the studio in minutes.
	 *
	 * @return average duration
	 */
	public long getAverageTime() {
		long average = 0;
		for (long time : attendanceTimes) {
			average += time;
		}
		return average/attendanceTimes.size();
	}

	public LocalDate getDate() {
		return date;
	}

	/**
	 * Adds a new member to the {@link HashSet} of members who visited the studio this day.
	 *
	 * @param memberId		ID of the member
	 */
	public boolean addMember(Long memberId) {
		return memberIds.add(memberId);
	}

	/**
	 * Adds a new duration to the {@link LinkedList} of visit durations.
	 *
	 * @param time		duration of the visit in minutes
	 */
	public void addTime(long time) {
		if(time < 0) {
			throw new IllegalArgumentException("Couldn't add negative time");
		}
		attendanceTimes.add(time);
	}
}
