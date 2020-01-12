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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StatisticManagement {

	private final AttendanceManagement attendanceManagement;
	private final InvoiceManagement invoiceManagement;
	private final RevenueManagement revenueManagement;
	private final StaffManagement staffManagement;

	public StatisticManagement(AttendanceManagement attendanceManagement, InvoiceManagement invoiceManagement,
							   RevenueManagement revenueManagement, StaffManagement staffManagement) {

		Assert.notNull(attendanceManagement, "AttendanceManagement must not be null!");
		Assert.notNull(invoiceManagement, "InvoiceManagement must not be null!");
		Assert.notNull(revenueManagement, "RevenueRepository must not be null!");
		Assert.notNull(staffManagement, "StaffManagement must not be null!");

		this.attendanceManagement = attendanceManagement;
		this.invoiceManagement = invoiceManagement;
		this.revenueManagement = revenueManagement;
		this.staffManagement = staffManagement;
	}

	public void addAttendance(LocalDate date, long memberId, long duration) {
		attendanceManagement.addAttendance(date, memberId, duration);
	}

	public void addAttendance(long memberId, long duration) {
		attendanceManagement.addAttendance(memberId, duration);
	}

	public void addRevenue(long memberId, long contractId) {
		revenueManagement.addRevenue(memberId, contractId);
	}

	public Streamable<Attendance> findAllAttendances() {
		return attendanceManagement.findAll();
	}

	public Optional<Attendance> findAttendanceById(LocalDate date) {
		return attendanceManagement.findById(date);
	}

	public Streamable<Revenue> findAllRevenues() {
		return  revenueManagement.findAll();
	}

	public long getAverageTimeOfToday() {
		Optional<Attendance> attendance = attendanceManagement.findById(LocalDate.now());
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
			Optional<Attendance> attendance = attendanceManagement.findById(date);
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
		Optional<Attendance> attendance = attendanceManagement.findById(LocalDate.now());
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
			Optional<Attendance> attendance = attendanceManagement.findById(date);
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

	public double getStaffExpenditurePerMonth() {
		Money earnings = Money.of(0, "EUR");

		for(Staff staff : staffManagement.getAllStaffs()) {
			earnings = earnings.add(staff.getSalary());
		}

		return earnings.getNumberStripped().doubleValue();
	}

	public double getMemberRevenuePerMonth() {
		return revenueManagement.getMonthlyRevenue().getNumberStripped().doubleValue();
	}

	public double getPercentageExpenditure() {
		BigDecimal total =  BigDecimal.valueOf(getStaffExpenditurePerMonth() + getMemberRevenuePerMonth());
		BigDecimal expenditure = BigDecimal.valueOf(getStaffExpenditurePerMonth());
		if (expenditure.compareTo(BigDecimal.ZERO) == 0) {
			return 0;
		}
		return expenditure.divide(total, 4, RoundingMode.UP).doubleValue()*100;
	}

	public double getPercentageRevenue() {
		BigDecimal total = BigDecimal.valueOf(getStaffExpenditurePerMonth() + (getMemberRevenuePerMonth()));
		BigDecimal revenue = BigDecimal.valueOf(getMemberRevenuePerMonth());
		if (revenue.compareTo(BigDecimal.ZERO) == 0) {
			return 0;
		}
		return revenue.divide(total, 4, RoundingMode.DOWN).doubleValue()*100;
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
