package fitnessstudio.studio;

import org.salespointframework.core.DataInitializer;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.stream.IntStream;

@Component
public class StudioInitializer implements DataInitializer {

	private final StudioRepository studioRepository;
	private static final int SUNDAY = 7;
	private static final int WEDNESDAY = 3;
	private static final int MAX_DAY_OF_WEEK = 8;

	public StudioInitializer(StudioRepository studioRepository) {
		this.studioRepository = studioRepository;
	}

	@Override
	public void initialize() {
		if (!studioRepository.findAll().iterator().hasNext()) {

			Collection<String> list = new LinkedList<>();
			IntStream.range(1, MAX_DAY_OF_WEEK).forEach(day -> {

				if (day == WEDNESDAY) {
					list.add(DayOfWeek.of(day).getDisplayName(TextStyle.FULL, Locale.GERMAN) + ":  0:00 - 11:00 Uhr " +
							"\n und 14:00 - 22:00 Uhr");
					return;
				}

				if (day == SUNDAY) {
					list.add(DayOfWeek.of(day).getDisplayName(TextStyle.FULL, Locale.GERMAN) + ":  geschlossen ");
					return;
				}

				list.add(DayOfWeek.of(day).getDisplayName(TextStyle.FULL, Locale.GERMAN) + ":  0:00 bis " +
						"23:59 Uhr");
			});
			String openingTimes = Arrays.toString(list.toArray()).replace("[", "").
					replace("]", "").
					replace(",", "\n");
			Studio studio = new Studio(openingTimes, "20", "Traumstraße 1, Dresden",
					"Fitness Second");
			studioRepository.save(studio);
		}
	}
}
