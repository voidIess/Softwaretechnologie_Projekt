package fitnessstudio.barmanagement;


import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Controller
public class InventoryController {

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final ArticleCatalog catalog;
	private final DiscountRepository discountRepository;

	private static final String REDIRECT_CATALOG = "redirect:/catalog";

	public InventoryController(UniqueInventory<UniqueInventoryItem> inventory, ArticleCatalog catalog,
							   DiscountRepository discountRepository) {
		this.inventory = inventory;
		this.catalog = catalog;
		this.discountRepository = discountRepository;
	}

//----------------------------------------Add article-------------------------------------------------------------------

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
		LocalDate expirationDate = LocalDate.parse(form.getExpirationDate(), formatter);

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
		return REDIRECT_CATALOG;
	}

//----------------------------------------delete article----------------------------------------------------------------

	@PostMapping("/article/delete/{id}")
	public String delete(@PathVariable ProductIdentifier id) {

		inventory.findAll().forEach(uniqueInventoryItem -> {
			Article article = (Article) uniqueInventoryItem.getProduct();
			if (Objects.equals(article.getId(), id)) {
				inventory.delete(uniqueInventoryItem);
				catalog.delete(article);
			}
		});

		return REDIRECT_CATALOG;
	}

//----------------------------------------edit article-------------------------------------------------------------------
	@GetMapping("/article/detail/{id}")
	public String editArticle(@PathVariable ProductIdentifier id, Model model) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

		inventory.findAll().forEach(uniqueInventoryItem -> {

			Article article = (Article) uniqueInventoryItem.getProduct();
			if (Objects.equals(article.getId(), id)) {
				model.addAttribute("id", id);

				// for keeping previous value in input field

				model.addAttribute("form", new ArticleForm() {
					@Override
					public @NotEmpty String getName() {
						return article.getName();
					}

					@Override
					public @NotEmpty String getArt() {
						return article.getArt();
					}

					@Override
					public @NotEmpty String getDescription() {
						return article.getDescription();
					}

					@Override
					public @NotEmpty @Digits(fraction = 2, integer = 5) String getPrice() {
						return String.valueOf(article.getPrice().getNumber());
					}

					@Override
					public @NotEmpty String getExpirationDate() {

						return article.getExpirationDate().format(formatter);
					}

					@Override
					public @NotEmpty @Size(min = 1, max = 99, message = "percent of discount from 0-99")
					String getPercentDiscount() {
						return String.valueOf(article.getDiscount().getPercent());
					}

					@Override
					public @NotEmpty String getStartDiscount() {
						return article.getDiscount().getStartDate().format(formatter);
					}

					@Override
					public @NotEmpty String getEndDiscount() {
						return article.getDiscount().getEndDate().format(formatter);
					}

					@Override
					public @NotEmpty @Digits(fraction = 0, integer = 5) String getNumber() {
						return String.valueOf(uniqueInventoryItem.getQuantity());
					}
				});
			}

		});

		return "edit_article";
	}

	@PostMapping("/article/detail/{id}")
	public String editArticle(@PathVariable ProductIdentifier id, @Valid ArticleForm form) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

		LocalDate startDate = LocalDate.parse(form.getStartDiscount(), formatter);
		LocalDate endDate = LocalDate.parse(form.getEndDiscount(), formatter);
		LocalDate expirationDate = LocalDate.parse(form.getExpirationDate(), formatter);


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
		return REDIRECT_CATALOG;
	}

	@GetMapping("/stock")
	@PreAuthorize("hasRole('STAFF')")
	public String stock(Model model) {

		model.addAttribute("stock", inventory.findAll());

		return "stock";
	}

}
