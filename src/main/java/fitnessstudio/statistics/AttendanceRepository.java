package fitnessstudio.statistics;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface AttendanceRepository extends CrudRepository<Attendance, LocalDate> {
}
