package hoangdung.vn.projectSpring.service.interfaces;

import java.util.List;
import java.util.Optional;
import hoangdung.vn.projectSpring.dto.response.CategoryResponse;
import hoangdung.vn.projectSpring.entity.Category;


public interface CategoryInterface {
    CategoryResponse createCategory(Category category);
    CategoryResponse updateCategory(Long id);
    void deleteCategory(Long id);
    Optional<CategoryResponse> getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
}
