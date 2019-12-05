package fitnessstudio.roster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableRowTest {

	@Test
	void constructorTest () {
		TableRow tableRow = new TableRow(LocalDateTime.now(),1);
		assertThat(tableRow.getSlots().size() == 7).isTrue();

		try {
			tableRow = new TableRow(null, 1);
			fail("Die Startzeit das nicht null sein");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			tableRow = new TableRow(LocalDateTime.now(), Roster.AMOUNT_ROWS);
			fail("Die Schichtnummer existiert nicht.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			tableRow = new TableRow(LocalDateTime.now(),-1);
			fail("Die Schichtnummer darf nicht negativ sein!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	//TODO: toString testen

}
