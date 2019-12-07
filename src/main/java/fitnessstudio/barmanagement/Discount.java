package fitnessstudio.barmanagement;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class Discount {

	@Id
	@GeneratedValue
	private Long id;
	private LocalDate startDate;
	private LocalDate endDate;
	private int percent;
	public Discount() {
	}

	public Discount(LocalDate startDate, LocalDate endDate, int percent) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.percent = percent;
	}

	public Long getId() {
		return id;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

	@Override
	public String toString() {
		return getPercent()==0? "" :"-"+getPercent()+"%";
	}
}
