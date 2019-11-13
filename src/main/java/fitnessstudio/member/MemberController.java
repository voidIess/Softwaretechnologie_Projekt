package fitnessstudio.member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class MemberController {

	private final MemberManagement memberManagement;

	MemberController(MemberManagement memberManagement){
		Assert.notNull(memberManagement, "MemberManagement must not be null");

		this.memberManagement = memberManagement;
	}

	@GetMapping("/register")
	public String register(Model model, RegistrationForm form, Errors results){
		model.addAttribute("form", form);
		model.addAttribute("error", results);
		return "register";
	}

	@PostMapping("/register")
	public String registerNew(@Valid @ModelAttribute("form") RegistrationForm form, Model model, Errors result){

		memberManagement.createMember(form, result);
		if (result.hasErrors()){
			return register(model, form, result);
		}

		return "redirect:/";
	}

	@GetMapping("/members")
	public String members(Model model) {
		model.addAttribute("memberList", memberManagement.findAll());
		return "members";
	}



}
