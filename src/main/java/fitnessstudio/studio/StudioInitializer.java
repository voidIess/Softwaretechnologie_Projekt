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

	public StudioInitializer(StudioRepository studioRepository) {
		this.studioRepository = studioRepository;
	}

	@Override
	public void initialize() {
		if (!studioRepository.findAll().iterator().hasNext()) {

			Collection<String> list = new LinkedList<>();
			IntStream.range(1, 8).forEach(i ->
					list.add(DayOfWeek.of(i).getDisplayName(TextStyle.FULL, Locale.GERMAN) + ":  0:00 bis " +
							"23:59 Uhr")
			);
			String openingTimes = Arrays.toString(list.toArray()).replace("[", "").
					replace("]", "").
					replace(",", "\n");
			Studio studio = new Studio(openingTimes, "20", "Traumstraße 1, Dresden",
					"Fitness Second");
			studioRepository.save(studio);
		}
	}
}
