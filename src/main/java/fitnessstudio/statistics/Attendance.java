package fitnessstudio.statistics;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Entity
public class Attendance {

	@Id
	private LocalDate date;

	@ElementCollection
	private Set<Long> memberIds;

	@ElementCollection
	private List<Long> attendanceTimes;

	public Attendance() {
		memberIds = new HashSet<>();
		attendanceTimes = new LinkedList<>();
		date = LocalDate.now();
	}

	public Attendance(LocalDate date) {
		this();
		this.date = date;
	}

	public int getMemberAmount() {
		return memberIds.size();
	}

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

	public boolean addMember(Long memberId) {
		return memberIds.add(memberId);
	}

	public void addTime(long time) {
		if(time < 0) {
			throw new IllegalArgumentException("Couldn't add negative time");
		}
		attendanceTimes.add(time);
	}
}
