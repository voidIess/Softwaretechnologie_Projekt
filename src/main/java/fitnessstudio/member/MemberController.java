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

/**
 * Spring MVC Controller to interact with the {@link Member}s.
 *
 * @version 1.0
 */
@Controller
public class MemberController {

	private static final String REDIRECT_LOGIN = "redirect:/login";
	private static final String REDIRECT_MEMBERS = "redirect:/admin/members";
	private static final String REDIRECT_HOME = "redirect:/member/home";
	private static final String REDIRECT_CHECKIN = "redirect:/checkin";

	private final MemberManagement memberManagement;
	private final ContractManagement contractManagement;
	private final InvoiceManagement invoiceManagement;


	/**
	 * Creates a new {@link MemberController} instance
	 * with the given {@link MemberManagement}, {@link MemberRepository} and {@link InvoiceManagement}.
	 *
	 * @param memberManagement		Must not be {@literal null}.
	 * @param contractManagement	Must not be {@literal null}.
	 * @param invoiceManagement		Must not be {@literal null}.
	 */
	MemberController(MemberManagement memberManagement, ContractManagement contractManagement,
					 InvoiceManagement invoiceManagement) {
		Assert.notNull(memberManagement, "MemberManagement must not be null");
		Assert.notNull(contractManagement, "ContractManagement must not be null");
		Assert.notNull(invoiceManagement, "InvoiceManagement must not be null");

		this.memberManagement = memberManagement;
		this.contractManagement = contractManagement;
		this.invoiceManagement = invoiceManagement;
	}

	/**
	 * Shows registration view with the given input form.
	 *
	 * @param model		page model
	 * @param form		user input
	 * @param results	errors through input
	 * @return registration view
	 */
	@GetMapping("/register")
	public String register(Model model, RegistrationForm form, Errors results) {
		model.addAttribute("form", form);
		model.addAttribute("error", results);
		model.addAttribute("contractList", contractManagement.getAllContracts());
		return "register";
	}

	/**
	 * Shows registration view with the given input form.
	 * The bonus code field is filled with the PathVariable id.
	 *
	 * @param model		page model
	 * @param form		user input
	 * @param results	errors through input
	 * @return registration view with bonus code
	 */
	@GetMapping("/register/{id}")
	public String registerFromFriend(@PathVariable String id, Model model, RegistrationForm form, Errors results) {
		form.setBonusCode(id);
		model.addAttribute("form", form);
		model.addAttribute("error", results);
		model.addAttribute("friendCode", id);
		model.addAttribute("contractList", contractManagement.getAllContracts());
		return "register";
	}

	/**
	 * Generates a new {@link Member} with parameters of the given input form when all fields were filled correctly.
	 * Then redirects the user to to the index page.
	 * Otherwise stays on the registration view and prints the calculated errors.
	 *
	 * @param model		page model
	 * @param form		user input
	 * @return index page / register view with errors
	 */
	@PostMapping("/register")
	public String registerNew(@Valid @ModelAttribute("form") RegistrationForm form, Model model, Errors result) {

		memberManagement.createMember(form, result);
		if (result.hasErrors()) {
			return register(model, form, result);
		}

		return "redirect:/";
	}

	/**
	 * Shows overview of all activated members.
	 *
	 * @param model	page model
	 * @param form	input form to search in members
	 * @return overview of activated members
	 */
	@GetMapping("/admin/members")
	@PreAuthorize("hasRole('STAFF')")
	public String members(Model model, @ModelAttribute("form") SearchForm form) {
		model.addAttribute("memberList", memberManagement.findAllAuthorized(form.getSearch()));
		model.addAttribute("unauthorizedMember", memberManagement.findAllUnauthorized().size());
		return "member/members";
	}

	/**
	 * Returns page to view all unactivated members.
	 *
	 * @param model page model
	 * @return overview of unactivated
	 */
	@GetMapping("/admin/authorizeMember")
	@PreAuthorize("hasRole('STAFF')")
	public String authorizeMember(Model model) {
		model.addAttribute("unauthorizedMember", memberManagement.findAllUnauthorized());
		return "member/authorizeMember";
	}


	/**
	 * Shows details of a certain member given to the {@link UserAccount}.
	 * If no member is logged in redirects to the login page.
	 *
	 * @param userAccount	user account of member
	 * @param model			page model
	 * @return member info or login page
	 */
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

	/**
	 * Shows an in-/output {@link EditingForm} for user to change personal data if logged in as member.
	 * Otherwise redirects to the login page.
	 *
	 * @param userAccount	user account of member
	 * @param form			edit in-/output form
	 * @param model			page model
	 * @param results		errors occurred through user input
	 * @return member editing or login page
	 */
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

	/**
	 * Saves changes from given input form of {@link Member} data and redirects the user to the member detail page.
	 * If given {@link UserAccount} isn't valid returns the login page.
	 *
	 * @param userAccount	user account of member
	 * @param form			member edit form
	 * @param model			page model
	 * @param result 		input errors
	 * @return member detail or login page
	 */
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

	/**
	 * Loads input amount to the members {@link CreditAccount} and stays on the member detail page.
	 * If given {@link UserAccount} isn't valid returns the login page.
	 *
	 * @param userAccount	user account of member
	 * @param amount		amount to deposit in euro
	 * @return member detail or login page
	 */
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

	/**
	 * Deletes {@link Member} with ID given by the PathVariable if user has role of staff.
	 * Redirects the user to the activated members overview.
	 *
	 * @param id	member id
	 * @param model	page model
	 * @return activated members overview
	 */
	@GetMapping("/member/delete/{id}")
	@PreAuthorize("hasRole('STAFF')")
	public String delete(@PathVariable long id, Model model) {
		memberManagement.deleteMember(id);
		return "redirect:/admin/authorizeMember";
	}

	/**
	 * Activates the {@link UserAccount} given by the member ID in the PathVariable if user has role of staff.
	 * Redirects the user to the activated members overview.
	 *
	 * @param id	ID of member
	 * @param model page model
	 * @return activated members overview
	 */
	@GetMapping("/member/authorize/{id}")
	@PreAuthorize("hasRole('STAFF')")
	public String authorize(@PathVariable long id, Model model) {
		memberManagement.authorizeMember(id);
		return "redirect:/admin/authorizeMember";
	}

	/**
	 * Shows the checkin view if user has role of staff.
	 *
	 * @param model page model
	 * @return checkin page
	 */
	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/checkin")
	public String showCheckIn(Model model) {
		model.addAttribute("attendants", memberManagement.findAllAttendant());
		return "checkin";
	}

	/**
	 * Changes the state of a certain {@link Member} given by the ID to attendant if user has role of staff.
	 * Stays on checkin page.
	 *
	 * @param id	member ID
	 * @return checkin page
	 */
	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/member/checkin")
	public String checkIn(@RequestParam long id) {
		memberManagement.checkMemberIn(id);
		return REDIRECT_CHECKIN;
	}

	/**
	 * Changes the state of a certain {@link Member} given by the ID to not attendant if user has role of staff.
	 * Stays on checkin page.
	 *
	 * @param id	member ID
	 * @return checkin page
	 */
	@GetMapping("/member/checkout/{id}")
	@PreAuthorize("hasRole('STAFF')")
	public String checkOut(@PathVariable long id) {
		memberManagement.checkMemberOut(id);
		return REDIRECT_CHECKIN;
	}

	/**
	 * Loads the given amount to the online {@link CreditAccount} of the {@link Member} given by ID
	 * if user has role of staff. Stays on checkin page.
	 *
	 * @param id		member ID
	 * @param amount	amount in euro
	 * @return checkin page
	 */
	@PostMapping("/admin/member/payin")
	@PreAuthorize("hasRole('STAFF')")
	public String barPayIn(@RequestParam long id, @RequestParam double amount) {
		Money money = Money.of(amount, "EUR");
		memberManagement.memberPayIn(id, money);
		return REDIRECT_CHECKIN;
	}

	/**
	 * If user is logged in with a valid member {@link UserAccount},
	 * this method opens an other window with the invoice PDF document of the last month for a certain {@link Member}.
	 * Otherwise redirects to the login page.
	 *
	 * @param userAccount	user account of member
	 * @param model			page model
	 * @return home or login page
	 */
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

	/**
	 * Pauses the membership of a certain {@link Member} given by the {@link UserAccount} for 31 days.
	 * If user isn't logged in with a valid member user account redirects to login page.
	 *
	 * @param userAccount user account of member
	 * @return home or login page
	 */
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

	/**
	 * Shows a page with the pause membership dialog if user has role of member.
	 *
	 * @return pause membership option
	 */
	@PreAuthorize("hasRole('MEMBER')")
	@GetMapping("/member/memberPause")
	public String showPause() {
		return "member/memberPause";
	}

	/**
	 * Deletes the {@link Member} given by the {@link UserAccount} from the system when account is valid.
	 * Redirects to the login page.
	 *
	 * @param userAccount members user account
	 * @return login page
	 */
	@GetMapping("/member/end")
	public String end(@LoggedIn Optional<UserAccount> userAccount) {
		return userAccount.map(user -> {
			Optional<Member> member = memberManagement.findByUserAccount(user);
			member.ifPresent(value -> memberManagement.deleteMember(value.getMemberId()));
			return REDIRECT_LOGIN;
		}).orElse(REDIRECT_LOGIN);
	}

	/**
	 * Shows a dialog to end the membership if the user has role of member.
	 *
	 * @return end membership page
	 */
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
