package fitnessstudio.member;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class ContractController {

	private static final String REDIRECT_LOGIN = "redirect:/login";
	private final ContractManagement contractManagement;

	ContractController(ContractManagement contractManagement) {
		Assert.notNull(contractManagement, "ContractManagement must not be null");

		this.contractManagement = contractManagement;
	}


	@GetMapping("/admin/contract/create")
	@PreAuthorize("hasRole('BOSS')")
	public String create(Model model, ContractForm form) {
		model.addAttribute("form", form);
		return "contractCreate";
	}

	@PostMapping("/admin/contract/create")
	@PreAuthorize("hasRole('BOSS')")
	public String createNew(@Valid ContractForm form, Model model) {
		contractManagement.createContract(form);

		return "redirect:/admin/contracts";
	}


	@GetMapping("admin/contracts")
	public String contracts(Model model) {
		model.addAttribute("contractList", contractManagement.getAllContracts());
		return "contracts";
	}
}
