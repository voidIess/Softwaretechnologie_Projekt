package fitnessstudio.statistics;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.time.LocalDate;

public interface AttendanceRepository extends CrudRepository<Attendance, LocalDate> {

	@Override
	Streamable<Attendance> findAll();

}
