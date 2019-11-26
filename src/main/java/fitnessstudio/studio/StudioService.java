package fitnessstudio.studio;

import org.springframework.stereotype.Service;

@Service
public class StudioService {

	private final StudioRepository studioRepository;

	public StudioService(StudioRepository studioRepository) {
		this.studioRepository = studioRepository;
	}

	public Studio getStudio() {
		return studioRepository.findAll().iterator().next();
	}

	public void saveStudio(Studio studio) {
		studioRepository.save(studio);
	}

}
