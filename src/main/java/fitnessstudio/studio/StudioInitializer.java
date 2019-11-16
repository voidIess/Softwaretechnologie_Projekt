package fitnessstudio.studio;

import org.salespointframework.core.DataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudioInitializer implements DataInitializer {

	@Autowired
	private StudioRepository studioRepository;

	@Override
	public void initialize() {
		Studio studio = new Studio("Mon-Sun 8am-10pm", "24", "50", "20");
		studioRepository.save(studio);
	}
}
