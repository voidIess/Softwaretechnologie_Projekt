package fitnessstudio.studio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudioService {

	@Autowired
	StudioRepository studioRepository;

	public Studio getStudio() {
		return studioRepository.findAll().iterator().next();
	}

	public void saveStudio(Studio studio) {
		studioRepository.save(studio);
	}

}
