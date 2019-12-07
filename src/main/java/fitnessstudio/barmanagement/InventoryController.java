package fitnessstudio.barmanagement;


import org.javamoney.moneta.Money;
import org.jetbrains.annotations.NotNull;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;

@Controller
public class InventoryController {

	private static final String REDIRECT_CATALOG = "redirect:/catalog";
	private static final String ERROR = "error";
	private static final String STATUS = "status";
	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final ArticleCatalog catalog;
	private final DiscountRepository discountRepository;
	private final BarManager barService;

	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	public InventoryController(UniqueInventory<UniqueInventoryItem> inventory, ArticleCatalog catalog,
							   DiscountRepository discountRepository, BarManager barService) {
		this.inventory = inventory;
		this.catalog = catalog;
		this.discountRepository = discountRepository;
		this.barService = barService;
	}

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/reorders")
	public String reorders(Model model) {
		model.addAttribute("reorders", barService.getReorderingArticles());
		model.addAttribute("available", barService.getReorderingArticles().iterator().hasNext());
		return "bar/reorders";
	}

//----------------------------------------Add article-------------------------------------------------------------------

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/article")
	public String addArticle(Model model, ArticleForm form) {
		model.addAttribute("form", form);
		return "bar/add_article";
	}

	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/article")
	public String addArticle(@Valid ArticleForm form, Model model) throws DateTimeParseException {
		if (getError(form, model)) return ERROR;
		Date date = new Date(form).invoke();
		LocalDate startDate = date.getStartDate();
		LocalDate endDate = date.getEndDate();
		LocalDate expirationDate = date.getExpirationDate();

		Discount discount = new Discount(startDate, endDate, Integer.parseInt(form.getPercentDiscount()));

		Article article = new Article(form.getName(),
				Money.of(new BigDecimal(form.getPrice()), "EUR"),
				form.getType(),
				form.getDescription(),
				expirationDate,
				discount);
		discountRepository.save(discount);
		catalog.save(article);
		inventory.save(new UniqueInventoryItem(article, Quantity.of(Integer.parseInt(form.getNumber()))));
		applicationEventPublisher.publishEvent(this);
		return REDIRECT_CATALOG;
	}

//----------------------------------------delete article----------------------------------------------------------------

	@PreAuthorize("hasRole('STAFF')")
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

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/article/detail/{id}")
	public String editArticle(@PathVariable ProductIdentifier id, Model model) throws DateTimeParseException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.GERMANY);

		inventory.findAll().forEach(uniqueInventoryItem -> {

			Article article = (Article) uniqueInventoryItem.getProduct();
			if (Objects.equals(article.getId(), id)) {
				model.addAttribute("id", id);
				model.addAttribute("form", getArticleForm(formatter, uniqueInventoryItem, article));
			}

		});

		return "bar/edit_article";
	}

	// for keeping previous value in input field
	@NotNull
	private ArticleForm getArticleForm(DateTimeFormatter formatter, UniqueInventoryItem uniqueInventoryItem, Article article) {

		return new ArticleForm() {
			@Override
			public @NotEmpty String getName() {
				return article.getName();
			}

			@Override
			public @NotEmpty String getType() {
				return article.getType();
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
			public @NotEmpty @Size(max = 100, message = "percent of discount from 0-100")
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
				return String.valueOf(uniqueInventoryItem.getQuantity().getAmount().intValue());
			}
		};
	}

	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/article/detail/{id}")
	public String editArticle(@PathVariable ProductIdentifier id, @Valid ArticleForm form, Model model) throws DateTimeParseException {
		if (getError(form, model)) return ERROR;

		Date date = new Date(form).invoke();
		LocalDate startDate = date.getStartDate();
		LocalDate endDate = date.getEndDate();
		LocalDate expirationDate = date.getExpirationDate();


		inventory.findAll().forEach(uniqueInventoryItem -> {

			Article article = (Article) uniqueInventoryItem.getProduct();

			if (Objects.equals(article.getId(), id)) {

				article.setName(form.getName());
				article.setPrice(Money.of(new BigDecimal(form.getPrice()), "EUR"));
				article.setType(form.getType());
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
				applicationEventPublisher.publishEvent(this);

			}
		});
		return REDIRECT_CATALOG;
	}

	private boolean getError(@Valid ArticleForm form, Model model) {
		if (Integer.parseInt(form.getNumber()) < 0) {
			model.addAttribute(ERROR, "Article should more than 0");
			model.addAttribute(STATUS, "400");
			return true;
		} else if (Double.parseDouble(form.getPrice()) < 0) {
			model.addAttribute(ERROR, "Price should more than 0 EUR");
			model.addAttribute(STATUS, "400");
			return true;
		} else if (Integer.parseInt(form.getPercentDiscount()) < 0 || Integer.parseInt(form.getPercentDiscount()) > 100) {
			model.addAttribute(ERROR, "Discount should in 0-100 percent");
			model.addAttribute(STATUS, "400");
			return true;
		}
		return false;
	}

	@GetMapping("/stock")
	@PreAuthorize("hasRole('STAFF')")
	public String stock(Model model) {

		model.addAttribute("stock", inventory.findAll());

		return "bar/stock";
	}




	// convert String input to Date
	private static class Date {
		private @Valid ArticleForm form;
		private LocalDate startDate;
		private LocalDate endDate;
		private LocalDate expirationDate;

		public Date(@Valid ArticleForm form) {
			this.form = form;
		}

		public LocalDate getStartDate() {
			return startDate;
		}

		public LocalDate getEndDate() {
			return endDate;
		}

		public LocalDate getExpirationDate() {
			return expirationDate;
		}

		public Date invoke() {

			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
				startDate = LocalDate.parse(form.getStartDiscount(), formatter);
				endDate = LocalDate.parse(form.getEndDiscount(), formatter);
				expirationDate = LocalDate.parse(form.getExpirationDate(), formatter);
				return this;

			} catch (DateTimeParseException e) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				startDate = LocalDate.parse(form.getStartDiscount(), formatter);
				endDate = LocalDate.parse(form.getEndDiscount(), formatter);
				expirationDate = LocalDate.parse(form.getExpirationDate(), formatter);
				return this;
			}

		}
	}
}
