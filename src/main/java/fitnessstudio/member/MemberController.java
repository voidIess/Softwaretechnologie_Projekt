package fitnessstudio.member;

import fitnessstudio.contract.ContractManagement;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class MemberController {

	private static final String REDIRECT_LOGIN = "redirect:/login";
	private final MemberManagement memberManagement;
	private final ContractManagement contractManagement;


	MemberController(MemberManagement memberManagement, ContractManagement contractManagement) {
		Assert.notNull(memberManagement, "MemberManagement must not be null");
		Assert.notNull(contractManagement, "ContractManagement must not be null");

		this.memberManagement = memberManagement;
		this.contractManagement = contractManagement;
	}

	@GetMapping("/register")
	public String register(Model model, RegistrationForm form, Errors results) {
		model.addAttribute("form", form);
		model.addAttribute("error", results);
		model.addAttribute("contractList", contractManagement.getAllContracts());
		return "register";
	}

	@PostMapping("/register")
	public String registerNew(@Valid @ModelAttribute("form") RegistrationForm form, Model model, Errors result) {

		memberManagement.createMember(form, result);
		if (result.hasErrors()) {
			return register(model, form, result);
		}

		return "redirect:/";
	}

	@GetMapping("/admin/members")
	@PreAuthorize("hasRole('STAFF')")
	public String members(Model model) {
		model.addAttribute("memberList", memberManagement.findAllAuthorized());
		model.addAttribute("unauthorizedMember", memberManagement.findAllUnauthorized().size());
		return "member/members";
	}

	@GetMapping("/admin/authorizeMember")
	@PreAuthorize("hasRole('STAFF')")
	public String authorizeMember(Model model) {
		model.addAttribute("unauthorizedMember", memberManagement.findAllUnauthorized());
		return "member/authorizeMember";
	}


	@GetMapping("/member/home")
	public String detail(@LoggedIn Optional<UserAccount> userAccount, Model model) {
		return userAccount.map(user -> {

			Optional<Member> member = memberManagement.findByUserAccount(user);

			if (member.isPresent()) {
				model.addAttribute("member", member.get());
				return "member/memberDetail";
			}
			return REDIRECT_LOGIN;
		}).orElse(REDIRECT_LOGIN);
	}

	@PostMapping("/member/payin")
	public String payIn(@LoggedIn Optional<UserAccount> userAccount, @RequestParam("amount") double amount) {
		Money money = Money.of(amount, "EUR");
		return userAccount.map(user -> {

			Optional<Member> member = memberManagement.findByUserAccount(user);

			if (member.isPresent()) {
				memberManagement.memberPayIn(member.get(), money);
				return "redirect:/member/home";
			}
			return REDIRECT_LOGIN;
		}).orElse(REDIRECT_LOGIN);
	}

	@GetMapping("/member/delete/{id}")
	@PreAuthorize("hasRole('STAFF')")
	public String delete(@PathVariable long id, Model model) {
		memberManagement.deleteMember(id);
		return "redirect:/admin/authorizeMember";
	}

	@GetMapping("/member/authorize/{id}")
	@PreAuthorize("hasRole('STAFF')")
	public String authorize(@PathVariable long id, Model model) {
		memberManagement.authorizeMember(id);
		return "redirect:/admin/authorizeMember";
	}

	@GetMapping("/member/checkin/{id}")
	@PreAuthorize("hasRole('STAFF')")
	public String checkIn(@PathVariable long id, Model model) {
		memberManagement.checkMemberIn(id);
		return "redirect:/admin/members";
	}

	@GetMapping("/member/checkout/{id}")
	@PreAuthorize("hasRole('STAFF')")
	public String checkOut(@PathVariable long id, Model model) {
		memberManagement.checkMemberOut(id);
		return "redirect:/admin/members";
	}

	@PostMapping("/printPdfInvoice")
	public String printPdfInvoice(@LoggedIn Optional<UserAccount> userAccount, Model model) {
		if (userAccount.isEmpty() || memberManagement.findByUserAccount(userAccount.get()).isEmpty()) {
			return REDIRECT_LOGIN;
		}

		model.addAttribute("type", "invoice");
		model.addAllAttributes(memberManagement.createPdfInvoice(userAccount.get()));

		return "pdfView";
	}
}
