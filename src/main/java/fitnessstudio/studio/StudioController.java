package fitnessstudio.studio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class StudioController {

	@Autowired
	private StudioService studioService;

	@GetMapping("/")
	public String index(Model model) {
		Studio studio = studioService.getStudio();
		model.addAttribute("openingTimes", studio.getOpeningTimes());
		model.addAttribute("contractTerm", studio.getContractTerm());
		model.addAttribute("monthlyFees", studio.getMonthlyFees());
		model.addAttribute("advertisingBonus", studio.getAdvertisingBonus());
		return "index";
	}


	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/studio")
	public String editStudio(Model model, StudioForm studioForm) {
		model.addAttribute("studio", studioService.getStudio());
		model.addAttribute("studioForm", studioForm);
		return "studio";
	}


	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("/studio")
	public String editStudio(StudioForm studioForm, Model model, Errors errors) {
		Studio studio = studioService.getStudio();
		studio.setOpeningTimes(studioForm.getOpeningTimes());
		studio.setMonthlyFees(studioForm.getMonthlyFees());
		studio.setContractTerm(studioForm.getContractTerm());
		studio.setAdvertisingBonus(studioForm.getAdvertisingBonus());
		studioService.saveStudio(studio);
		return "redirect:/";
	}
}
