package fitnessstudio.studio;

import fitnessstudio.contract.ContractManagement;
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
	private final ContractManagement contractManagement;

	@Autowired
	public StudioController(StudioService studioService, ContractManagement contractManagement) {
		this.studioService = studioService;
		this.contractManagement = contractManagement;
	}

	@GetMapping("/")
	public String index(Model model) {
		Studio studio = studioService.getStudio();
		model.addAttribute("openingTimes", studio.getOpeningTimes());
		model.addAttribute("address", studio.getAddress());
		model.addAttribute("advertisingBonus", studio.getAdvertisingBonus());
		model.addAttribute("contractList", contractManagement.getAllContracts());
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
			public @NotEmpty String getAdvertisingBonus() {
				return studio.getAdvertisingBonus();
			}

			@Override
			public @NotEmpty String getAddress() {
				return studio.getAddress();
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
		if (Double.parseDouble(studioForm.getAdvertisingBonus()) < 0) {
			model.addAttribute(ERROR, "Contract term, monthly fees, advertising bonus should be positive");
			model.addAttribute(STATUS, "400");
			return ERROR;
		}

		Studio studio = studioService.getStudio();
		studio.setOpeningTimes(studioForm.getOpeningTimes());
		studio.setAdvertisingBonus(studioForm.getAdvertisingBonus());
		studio.setAddress(studioForm.getAddress());
		studioService.saveStudio(studio);
		return "redirect:/";
	}

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/checkin")
	public String checkin() {
		return "checkin";
	}
}
