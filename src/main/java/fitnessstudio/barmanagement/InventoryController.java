package fitnessstudio.barmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class InventoryController {

	@Autowired
	private ArticleCatalog catalog;
}
