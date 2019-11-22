package fitnessstudio.barmanagement;


import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
		return "add_article";
	}

	@PostMapping("/article")
	public String addArticle(@Valid ArticleForm form, Model model) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

		LocalDate startDate = LocalDate.parse(form.getStartDiscount(), formatter);
		LocalDate endDate = LocalDate.parse(form.getEndDiscount(), formatter);
		LocalDate expirationDate = LocalDate.parse(form.getExpiationDate(), formatter);

		Discount discount = new Discount(startDate, endDate, Integer.parseInt(form.getPercentDiscount()));

		Article article = new Article(form.getName(),
			Money.of(new BigDecimal(form.getPrice()), "EUR"),
			form.getArt(),
			form.getDescription(),
			expirationDate,
			discount);
		discountRepository.save(discount);
		catalog.save(article);
		inventory.save(new UniqueInventoryItem(article, Quantity.of(Integer.parseInt(form.getNumber()))));
		return "redirect:/catalog";
	}

	@PostMapping("/article/delete/{id}")
	public String delete(@PathVariable ProductIdentifier id) {

		inventory.findAll().forEach(uniqueInventoryItem -> {
			Article article = (Article) uniqueInventoryItem.getProduct();
			if (Objects.equals(article.getId(), id)) {
				inventory.delete(uniqueInventoryItem);
				catalog.delete(article);
			}
		});

		return "redirect:/catalog";
	}


	@GetMapping("/article/detail/{id}")
	public String editArticle(@PathVariable ProductIdentifier id, Model model, ArticleForm form) {
		model.addAttribute("id", id);
		model.addAttribute("form", form);
		return "edit_article";
	}

	@PostMapping("/article/detail/{id}")
	public String editArticle(@PathVariable ProductIdentifier id, @Valid ArticleForm form) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

		LocalDate startDate = LocalDate.parse(form.getStartDiscount(), formatter);
		LocalDate endDate = LocalDate.parse(form.getEndDiscount(), formatter);
		LocalDate expirationDate = LocalDate.parse(form.getExpiationDate(), formatter);


		inventory.findAll().forEach(uniqueInventoryItem -> {

			Article article = (Article) uniqueInventoryItem.getProduct();

			if (Objects.equals(article.getId(), id)) {

				article.setName(form.getName());
				article.setPrice(Money.of(new BigDecimal(form.getPrice()), "EUR"));
				article.setArt(form.getArt());
				article.setDescription(form.getDescription());
				article.setExpirationDate(expirationDate);

				discountRepository.findAll().forEach(discount -> {
					if (discount.getId().equals(article.getDiscount().getId())) {

						discount.setStartDate(startDate);
						discount.setEndDate(endDate);
						discount.setPercent(Integer.parseInt(form.getPercentDiscount()));
						discountRepository.save(discount);
					}

				});
				catalog.save(article);
				inventory.delete(uniqueInventoryItem);
				inventory.save(new UniqueInventoryItem(article, Quantity.of(Integer.parseInt(form.getNumber()))));

			}
		});
		return "redirect:/catalog";
	}

}
