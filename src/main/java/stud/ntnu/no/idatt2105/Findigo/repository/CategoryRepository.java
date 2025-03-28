package stud.ntnu.no.idatt2105.Findigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
