package fitnessstudio.barmanagement;


import org.javamoney.moneta.Money;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class InventoryController {

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final ArticleCatalog catalog;
	private final DiscountRepository discountRepository;

	public InventoryController(UniqueInventory<UniqueInventoryItem> inventory, ArticleCatalog catalog, DiscountRepository discountRepository) {
		this.inventory = inventory;
		this.catalog = catalog;
		this.discountRepository = discountRepository;
	}

	@GetMapping("/article")
	public String addArticle(Model model, ArticleForm form) {
		model.addAttribute("form", form);
		return "article";
	}

	@PostMapping("/article")
	public String addArticle(@Valid ArticleForm form, Model model) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

		LocalDate startDate = LocalDate.parse(form.getStartDiscount(), formatter);
		LocalDate endDate = LocalDate.parse(form.getEndDiscount(), formatter);
		LocalDate expirationDate = LocalDate.parse(form.getExpiationDate(), formatter);

		Discount discount = new Discount(startDate, endDate, Integer.parseInt(form.getPercentDiscount()));


		Article article = new Article(form.getName(), Money.of(new BigDecimal(form.getPrice()), "EUR"),
			form.getArt(), form.getDescription(), expirationDate, discount);
		discountRepository.save(discount);
		catalog.save(article);
		inventory.save(new UniqueInventoryItem(article, Quantity.of(Integer.parseInt(form.getNubmer()))));
		return "redirect:/catalog";
	}

}
