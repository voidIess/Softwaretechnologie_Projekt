package fitnessstudio.statistics;

import fitnessstudio.invoice.InvoiceEntry;
import fitnessstudio.invoice.InvoiceManagement;
import fitnessstudio.invoice.InvoiceType;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffManagement;
import org.javamoney.moneta.Money;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.money.NumberValue;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StatisticManagement {

	private final AttendanceRepository attendances;
	private final InvoiceManagement invoiceManagement;
	private final StaffManagement staffManagement;

	public StatisticManagement(AttendanceRepository attendances, InvoiceManagement invoiceManagement, StaffManagement staffManagement) {
		Assert.notNull(attendances, "AttendanceRepository must not be null!");
		Assert.notNull(invoiceManagement, "InvoiceManagement must not be null!");

		this.attendances = attendances;
		this.invoiceManagement = invoiceManagement;
		this.staffManagement = staffManagement;
	}

	public void addAttendance(LocalDate date, long memberId, long duration) {
		if(attendances.findById(date).isEmpty()) {
			attendances.save(new Attendance(date));
		}
		Attendance attendance = attendances.findById(date).get();
		attendance.addMember(memberId);
		attendance.addTime(duration);
	}

	public void addAttendance(long memberId, long duration) {
		addAttendance(LocalDate.now(), memberId, duration);
	}

	public Streamable<Attendance> findAll() {
		return attendances.findAll();
	}

	public Optional<Attendance> findById(LocalDate date) {
		return attendances.findById(date);
	}

	public long getAverageTimeOfToday() {
		Optional<Attendance> attendance = attendances.findById(LocalDate.now());
		if(attendance.isEmpty()) {
			return 0;
		} else {
			return attendance.get().getAverageTime();
		}
	}

	public long[] getAverageTimesOfThisWeek() {
		long[] times = new long[7];
		LocalDate today = LocalDate.now();

		for(int i=0; i<7; i++) {
			LocalDate date = getLastMonday(today).plusDays(i);
			Optional<Attendance> attendance = findById(date);
			if(attendance.isPresent()) {
				times[i] = attendance.get().getAverageTime();
			}
			if(date.equals(today)) {
				break;
			}
		}

		return times;
	}

	public long getMemberAmountOfToday() {
		Optional<Attendance> attendance = attendances.findById(LocalDate.now());
		if(attendance.isEmpty()) {
			return 0;
		} else {
			return attendance.get().getMemberAmount();
		}
	}

	public long[] getMemberAmountsOfThisWeek() {
		long[] amounts = new long[7];
		LocalDate today = LocalDate.now();

		for(int i=0; i<7; i++) {
			LocalDate date = getLastMonday(today).plusDays(i);
			Optional<Attendance> attendance = findById(date);
			if(attendance.isPresent()) {
				amounts[i] = attendance.get().getMemberAmount();
			}
			if(date.equals(today)) {
				break;
			}
		}

		return amounts;
	}

	public NumberValue getSellingEarningsOfDate(LocalDate date) {
		List<InvoiceEntry> invoice = invoiceManagement.getAllInvoicesOfDate(date);
		Money earnings = Money.of(0, "EUR");
		for(InvoiceEntry entry : invoice) {
			if(entry.getType().equals(InvoiceType.WITHDRAW) || entry.getType().equals(InvoiceType.CASHPAYMENT)) {
				earnings = earnings.add(entry.getAmount());
			}
		}
		return earnings.getNumber();
	}

	public NumberValue[] getSellingEarningsOfThisWeek() {
		NumberValue[] earnings = new NumberValue[7];
		LocalDate date = getLastMonday(LocalDate.now());
		for (int i=0; i<7; i++) {
			earnings[i] = getSellingEarningsOfDate(date);
			date = date.plusDays(1);
		}
		return earnings;
	}

	public Money getStaffExpenditureOfThisWeek() {
		Money earnings = Money.of(0, "EUR");
		for(Staff staff : staffManagement.getAllStaffs()) {
			earnings.add(staff.getSalary());
		}
		return earnings.divide(4);
	}

	private LocalDate getLastMonday(LocalDate date) {
		LocalDate lastMonday = date;
		while (!lastMonday.getDayOfWeek().equals(DayOfWeek.MONDAY)){
			lastMonday = lastMonday.minusDays(1);
		}
		return lastMonday;
	}

	public String[] getDaysOfWeek() {
		String[] week = new String[7];
		LocalDate monday = getLastMonday(LocalDate.now());

		for (int i=0; i<week.length; i++) {
			week[i] = monday.plusDays(i).toString();
		}

		return week;
	}

}
