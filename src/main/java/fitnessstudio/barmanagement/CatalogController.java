package fitnessstudio.barmanagement;


import org.salespointframework.quantity.Quantity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;


@PreAuthorize("hasRole('STAFF')")
@Controller
public class CatalogController {


	private final BarManager barManager;

	public CatalogController(BarManager barManager) {
		this.barManager = barManager;
	}

	/**
	 * an webview about all kinds of articles
	 * @param model
	 * @return
	 */
	@GetMapping("/catalog")
	public String catalog(Model model) {
		model.addAttribute("catalog", barManager.getAllArticles());
		return "bar/catalog";
	}

	/**
	 * the webview of the specific information about an {@link Article}
	 * @param article
	 * @param model
	 * @return
	 */
	@GetMapping("/article/{article}")
	public String detail(@PathVariable @ModelAttribute Article article, Model model) {

		Quantity quantity = barManager.getArticleQuantity(article);
		model.addAttribute("article", article);
		model.addAttribute("quantity", quantity.getAmount().intValue());
		model.addAttribute("orderable", quantity.isGreaterThan(Quantity.of(0)));

		return "bar/detail";

	}
}
