package fitnessstudio.studio;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;


@Controller
public class StudioController {

	private static final String ERROR = "error";
	private static final String STATUS = "status";

	private final StudioService studioService;

	@Autowired
	public StudioController(StudioService studioService) {
		this.studioService = studioService;
	}

	@GetMapping("/")
	public String index(Model model) {
		Studio studio = studioService.getStudio();
		model.addAttribute("openingTimes", studio.getOpeningTimes());
		model.addAttribute("contractTerm", studio.getContractTerm());
		model.addAttribute("monthlyFees", studio.getMonthlyFees());
		model.addAttribute("advertisingBonus", studio.getAdvertisingBonus());
		return "index";
	}

	// for keeping previous value in input field
	@NotNull
	private StudioForm getStudioForm(Studio studio) {
		return new StudioForm() {
			@Override
			public @NotEmpty String getOpeningTimes() {
				return studio.getOpeningTimes();
			}

			@Override
			public @NotEmpty String getContractTerm() {
				return studio.getContractTerm();
			}

			@Override
			public @NotEmpty String getMonthlyFees() {
				return studio.getMonthlyFees();
			}

			@Override
			public @NotEmpty String getAdvertisingBonus() {
				return studio.getAdvertisingBonus();
			}
		};
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/studio")
	public String editStudio(Model model) {

		Studio studio = studioService.getStudio();
		model.addAttribute("studio", studio);
		model.addAttribute("studioForm", getStudioForm(studio));
		return "studio";
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("/studio")
	public String editStudio(@Valid StudioForm studioForm, Model model, Errors errors) {
		if (Integer.parseInt(studioForm.getContractTerm()) < 1) {
			model.addAttribute(ERROR, "Contract term must at least 1 month");
			model.addAttribute(STATUS, "400");
			return ERROR;
		} else if (Integer.parseInt(studioForm.getMonthlyFees()) < 0) {
			model.addAttribute(ERROR, "Hey Boss we shouldn't give our member money monthly");
			model.addAttribute(STATUS, "400");
			return ERROR;
		} else if (Integer.parseInt(studioForm.getAdvertisingBonus()) < 0) {
			model.addAttribute(ERROR, "Advertising bonus should at least 0 EURO");
			model.addAttribute(STATUS, "400");
			return ERROR;
		}

		Studio studio = studioService.getStudio();
		studio.setOpeningTimes(studioForm.getOpeningTimes());
		studio.setMonthlyFees(studioForm.getMonthlyFees());
		studio.setContractTerm(studioForm.getContractTerm());
		studio.setAdvertisingBonus(studioForm.getAdvertisingBonus());
		studioService.saveStudio(studio);
		return "redirect:/";
	}
}
