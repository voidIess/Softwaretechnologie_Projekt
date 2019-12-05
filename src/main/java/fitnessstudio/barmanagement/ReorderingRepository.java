package fitnessstudio.barmanagement;

import org.springframework.data.repository.CrudRepository;

public interface ReorderingRepository extends CrudRepository<ReorderingArticle, Long> {
}
