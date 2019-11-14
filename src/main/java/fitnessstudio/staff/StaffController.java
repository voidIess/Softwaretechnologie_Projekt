package fitnessstudio.staff;

import fitnessstudio.member.MemberManagement;

import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class StaffController {

	//@PreAuthorize("hasRole('STAFF')")
	@RequestMapping(path = "/roster", method = RequestMethod.GET)
	String roster(@RequestParam Optional<String> name, Model model) {
		model.addAttribute("name", name.orElse("World"));
		return "roster";
	}
}
