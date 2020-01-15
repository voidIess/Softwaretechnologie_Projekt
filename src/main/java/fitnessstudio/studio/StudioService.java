package fitnessstudio.studio;

import org.springframework.stereotype.Service;

/**
 * Implementation of business logic related to {@link Studio}.
 */
@Service
public class StudioService {

	private final StudioRepository studioRepository;


	public StudioService(StudioRepository studioRepository) {
		this.studioRepository = studioRepository;
	}

	/**
	 * @return the studio from the studio repository
	 */
	public Studio getStudio() {
		return studioRepository.findAll().iterator().next();
	}

	/**
	 * @param studio {@link Studio}
	 */
	public void saveStudio(Studio studio) {
		studioRepository.save(studio);
	}

}
