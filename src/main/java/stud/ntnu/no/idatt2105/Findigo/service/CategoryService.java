package stud.ntnu.no.idatt2105.Findigo.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.idatt2105.Findigo.dtos.category.CategoryResponse;
import stud.ntnu.no.idatt2105.Findigo.dtos.mappers.CategoryMapper;
import stud.ntnu.no.idatt2105.Findigo.entities.Category;
import stud.ntnu.no.idatt2105.Findigo.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private static final Logger logger = LogManager.getLogger(CategoryService.class);

  public List<CategoryResponse> getAllCategories() {
    List<Category> categories = categoryRepository.getCategories();
    if (categories.isEmpty()) {
      logger.info("No categories in database");
    }
    return categories.stream()
        .map(CategoryMapper::toDto)
        .toList();
  }
}
