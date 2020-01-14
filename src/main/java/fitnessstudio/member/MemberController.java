package fitnessstudio.member;

import fitnessstudio.contract.ContractManagement;
import fitnessstudio.invoice.InvoiceManagement;
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
	private static final String REDIRECT_MEMBERS = "redirect:/admin/members";
	private static final String REDIRECT_HOME = "redirect:/member/home";
	private static final String REDIRECT_CHECKIN = "redirect:/checkin";

	private final MemberManagement memberManagement;
	private final ContractManagement contractManagement;
	private final InvoiceManagement invoiceManagement;


	MemberController(MemberManagement memberManagement, ContractManagement contractManagement,
					 InvoiceManagement invoiceManagement) {
		Assert.notNull(memberManagement, "MemberManagement must not be null");
		Assert.notNull(contractManagement, "ContractManagement must not be null");
		Assert.notNull(invoiceManagement, "InvoiceManagement must not be null");

		this.memberManagement = memberManagement;
		this.contractManagement = contractManagement;
		this.invoiceManagement = invoiceManagement;
	}

	@GetMapping("/register")
	public String register(Model model, RegistrationForm form, Errors results) {
		model.addAttribute("form", form);
		model.addAttribute("error", results);
		model.addAttribute("contractList", contractManagement.getAllContracts());
		return "register";
	}

	@GetMapping("/register/{id}")
	public String registerFromFriend(@PathVariable String id, Model model, RegistrationForm form, Errors results) {
		form.setBonusCode(id);
		model.addAttribute("form", form);
		model.addAttribute("error", results);
		model.addAttribute("friendCode", id);
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
	public String members(Model model, @ModelAttribute("form") SearchForm form) {
		model.addAttribute("memberList", memberManagement.findAllAuthorized(form.getSearch()));
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
				model.addAttribute("contractText", memberManagement.getContractTextOfMember(member.get()));
				return "member/memberDetail";
			}
			return REDIRECT_LOGIN;
		}).orElse(REDIRECT_LOGIN);
	}

	@GetMapping("/member/edit")
	public String edit(@LoggedIn Optional<UserAccount> userAccount, EditingForm form, Model model, Errors results) {
		if (userAccount.isPresent()) {
			Optional<Member> member = memberManagement.findByUserAccount(userAccount.get());
			if (member.isPresent()) {
				form = memberManagement.preFillMember(member.get(), form);

				model.addAttribute("form", form);
				model.addAttribute("error", results);
				return "/member/editMember";
			}
		}
		return REDIRECT_LOGIN;
	}

	@PostMapping("/member/edit")
	public String editNew(@LoggedIn Optional<UserAccount> userAccount, @Valid @ModelAttribute("form")
		EditingForm form, Model model, Errors result) {
		return userAccount.map(user -> {

			Optional<Member> member = memberManagement.findByUserAccount(user);

			if (member.isPresent()) {
				memberManagement.editMember(member.get().getMemberId(), form, result);
				if (result.hasErrors()) {
					return edit(userAccount, form, model, result);
				}
				return REDIRECT_HOME;
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
				memberManagement.memberPayIn(member.get().getMemberId(), money);
				return REDIRECT_HOME;
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

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/checkin")
	public String showCheckIn(Model model) {
		model.addAttribute("attendants", memberManagement.findAllAttendant());
		return "checkin";
	}

	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/member/checkin")
	public String checkIn(@RequestParam long id, Model model) {
		memberManagement.checkMemberIn(id);
		return REDIRECT_CHECKIN;
	}

	@GetMapping("/member/checkout/{id}")
	@PreAuthorize("hasRole('STAFF')")
	public String checkOut(@PathVariable long id, Model model) {
		memberManagement.checkMemberOut(id);
		return REDIRECT_CHECKIN;
	}

	@PostMapping("/admin/member/payin")
	@PreAuthorize("hasRole('STAFF')")
	public String barPayIn(@RequestParam long id, @RequestParam double amount) {
		Money money = Money.of(amount, "EUR");
		memberManagement.memberPayIn(id, money);
		return REDIRECT_CHECKIN;
	}

	@PostMapping("/printPdfInvoice")
	public String printPdfInvoice(@LoggedIn Optional<UserAccount> userAccount, Model model) {
		return userAccount.map(user -> {

			Optional<Member> member = memberManagement.findByUserAccount(user);

			if (member.isPresent()) {
				if (member.get().wasMemberLastMonth()) {
					model.addAttribute("type", "invoice");
					model.addAllAttributes(memberManagement.createPdfInvoice(userAccount.get()));
					return "pdfView";
				}
				return REDIRECT_HOME;
			}
			return REDIRECT_LOGIN;
		}).orElse(REDIRECT_LOGIN);
	}

	/*
		TEMPORARY!
	 */
	@GetMapping("/member/invoices")
	public String invoices(@LoggedIn Optional<UserAccount> userAccount, Model model) {
		return userAccount.map(user -> {
			Optional<Member> member = memberManagement.findByUserAccount(user);
			if (member.isPresent()) {
				model.addAttribute("invoices",
					invoiceManagement.getAllInvoicesForMember(member.get().getMemberId()));
				return "member/memberInvoices";
			}
			return REDIRECT_LOGIN;
		}).orElse(REDIRECT_LOGIN);
	}

	@GetMapping("/member/pause")
	public String pause(@LoggedIn Optional<UserAccount> userAccount) {
		return userAccount.map(user -> {
			Optional<Member> member = memberManagement.findByUserAccount(user);
			if (member.isPresent()) {
				memberManagement.pauseMembership(member.get());
				return REDIRECT_HOME;
			}
			return REDIRECT_LOGIN;
		}).orElse(REDIRECT_LOGIN);
	}

	@PreAuthorize("hasRole('MEMBER')")
	@GetMapping("/member/memberPause")
	public String showPause() {
		return "member/memberPause";
	}


	@GetMapping("/member/end")
	public String end(@LoggedIn Optional<UserAccount> userAccount) {
		return userAccount.map(user -> {
			Optional<Member> member = memberManagement.findByUserAccount(user);
			member.ifPresent(value -> memberManagement.deleteMember(value.getMemberId()));
			return REDIRECT_LOGIN;
		}).orElse(REDIRECT_LOGIN);
	}

	@PreAuthorize("hasRole('MEMBER')")
	@GetMapping("/member/endMembership")
	public String showEnd() {
		return "member/endMembership";
	}


	/**
	 * Link zum Einladen eines Freundes. Pfad: /member/invite
	 * @param form Formular fuer das Einladung
	 * @param model Model der Seite
	 * @param userAccount UserAccount des Einladenden
	 * @return redirect auf member/home, wenn userAccount oder member nicht gefunden werden. friendInvite wenn vorhanden
	 */
	@PreAuthorize("hasRole('MEMBER')")
	@GetMapping("/member/invite")
	public String inviteFriend (FriendInviteForm form, Model model, @LoggedIn Optional<UserAccount> userAccount) {
		UserAccount userAccountMember = userAccount.orElse(null);
		if (userAccountMember == null) {
			return REDIRECT_HOME;
		}
		Member member = memberManagement.findByUserAccount(userAccountMember).orElse(null);
		if (member == null) {
			return REDIRECT_HOME;
		}
		model.addAttribute("memberId", member.getMemberId());
		model.addAttribute("memberName", member.getFirstName() + " " + member.getLastName());
		model.addAttribute("form", form);
		return "member/inviteFriend";
	}

	/**
	 * Online Auftrag zum senden der Email
	 * @param model Seite des Models
	 * @param form Ausgefülltes Formular für Einladung
	 * @param errors Fehler beim Erstellen der Einladung
	 * @param userAccount UserAccount des angemeldeten Nutzers
	 * @return
	 */
	@PostMapping("/member/inviteAction")
	@PreAuthorize("hasRole('MEMBER')")
	public String inviteFriend(Model model, @Valid @ModelAttribute("form") FriendInviteForm form,
							   Errors errors, @LoggedIn Optional<UserAccount> userAccount) {
		if (errors.hasErrors()) {
			return  inviteFriend(form, model, userAccount);
		}
		memberManagement.inviteFriend(form);
		return REDIRECT_HOME;
	}

}
