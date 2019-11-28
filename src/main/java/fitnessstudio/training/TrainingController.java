package fitnessstudio.training;


import fitnessstudio.member.Member;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
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

	@GetMapping("/member/training/create")
	@PreAuthorize("hasRole('MEMBER')")
	public String create(Model model, TrainingForm form, Errors result) {
		model.addAttribute("staffs", trainingManagement.getAllStaffs());
		model.addAttribute("types", trainingManagement.getTypes());
		model.addAttribute("form", form);
		model.addAttribute("error", result);
		return "training/training_create";

	}

	@PostMapping("/member/training/create")
	public String createNew(@LoggedIn Optional<UserAccount> userAccount, @Valid @ModelAttribute("form") TrainingForm form, Model model, Errors result) {
		return userAccount.map(user -> {
			Optional<Member> member = trainingManagement.findByUserAccount(user);
			if (member.isPresent()) {
				trainingManagement.createTraining(member.get(), form, result);
				if (result.hasErrors()) {
					return create(model,form, result);
				}
				return "redirect:/member/trainings";
			}
			return REDIRECT_LOGIN;
		}).orElse(REDIRECT_LOGIN);
	}

	@GetMapping("/member/trainings")
	public String trainings(@LoggedIn Optional<UserAccount> userAccount, Model model) {
		return userAccount.map(user -> {
			Optional<Member> member = trainingManagement.findByUserAccount(user);
			if (member.isPresent()) {
				model.addAttribute("trainings", trainingManagement.getAllTrainingByMember(member.get()));
				return "training/member_trainings";
			}
			return REDIRECT_LOGIN;
		}).orElse(REDIRECT_LOGIN);
	}

	@GetMapping("admin/trainings")
	@PreAuthorize("hasRole('STAFF')")
	public String manageTrainings(Model model){
		model.addAttribute("trainings", trainingManagement.getAllTrainings());

		return "training/manage_trainings";
	}
}
