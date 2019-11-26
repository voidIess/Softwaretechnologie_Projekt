package fitnessstudio.training;


import fitnessstudio.member.Member;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class TrainingController {
	private static final String REDIRECT_LOGIN = "redirect:/login";


	private final TrainingManagement trainingManagement;

	TrainingController(TrainingManagement trainingManagement) {
		Assert.notNull(trainingManagement, "TrainingManagement must not be null");

		this.trainingManagement = trainingManagement;
	}

	@GetMapping("/member/contract/create")
	public String create(@LoggedIn Optional<UserAccount> userAccount, Model model, TrainingForm form, Errors result) {
		return userAccount.map(user -> {
			Optional<Member> member = trainingManagement.findByUserAccount(user);
			if (member.isPresent()) {
				model.addAttribute("member", member.get());
				model.addAttribute("staffs", trainingManagement.getAllStaffs());
				model.addAttribute("types", trainingManagement.getTypes());
				model.addAttribute("form", form);
				model.addAttribute("error", result);
				return "training_create";
			}
			return REDIRECT_LOGIN;
		}).orElse(REDIRECT_LOGIN);
	}

	@PostMapping("/member/contract/create")
	public String createNew(@Valid @ModelAttribute("form") TrainingForm form, Model model, Errors result) {
		if (result.hasErrors()){
			return createNew(form, model, result);
		}
		return "redirect:/member/home";
	}
}
