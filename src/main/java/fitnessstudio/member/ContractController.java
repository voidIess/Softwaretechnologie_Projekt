package fitnessstudio.member;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class ContractController {

	private static final String REDIRECT_LOGIN = "redirect:/login";
	private static final String REDIRECT_CONTRACTS = "redirect:/admin/contracts";
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
	public String createNew(@Valid @ModelAttribute("form") ContractForm form, Model model) {
		contractManagement.createContract(form);
		return REDIRECT_CONTRACTS;
	}


	@GetMapping("/admin/contracts")
	@PreAuthorize("hasRole('BOSS')")
	public String contracts(Model model) {
		model.addAttribute("contractList", contractManagement.getAllContracts());
		return "contracts";
	}

	@GetMapping("/admin/contract/delete/{id}")
	@PreAuthorize("hasRole('BOSS')")
	public String delete(@PathVariable long id, Model model){
		contractManagement.deleteContract(id);
		return REDIRECT_CONTRACTS;
	}

	@GetMapping("/admin/contract/detail/{id}")
	@PreAuthorize("hasRole('BOSS')")
	public String detail(@PathVariable long id, Model model){
		Optional<Contract> contract = contractManagement.findById(id);
		if (contract.isPresent()){
			Contract c = contract.get();
			model.addAttribute("contract", c);
			model.addAttribute("form", new ContractForm(c.getName(), c.getDescription(), c.getPrice().getNumber().doubleValue(), c.getDuration()));
			return "contractDetail";
		}
		return REDIRECT_CONTRACTS;
	}

	@PostMapping("/admin/contract/detail/{id}")
	@PreAuthorize("hasRole('BOSS')")
	public String editContract(@PathVariable Long id, @Valid ContractForm form){
		contractManagement.editContract(id, form);
		return REDIRECT_CONTRACTS;
	}

}
