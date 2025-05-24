package hoangdung.vn.projectSpring.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import hoangdung.vn.projectSpring.dto.response.CategoryResponse;
import hoangdung.vn.projectSpring.entity.Category;
import hoangdung.vn.projectSpring.repository.CategoryRepository;
import hoangdung.vn.projectSpring.service.interfaces.CategoryInterface;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryInterface {
    
    private final CategoryRepository categoryRepository;


    @Override
    public CategoryResponse createCategory(Category category) {
        Category categorySave = categoryRepository.save(category);
        return new CategoryResponse(
            categorySave.getId(), 
            categorySave.getName(), 
            categorySave.getStatus()
        );
    }


    @Override
    public CategoryResponse updateCategory(Long id) {
        Optional<Category> categoryOptional = this.categoryRepository.findById(id);
        if(categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            category.setStatus(!category.getStatus());
            Category categorySave = this.categoryRepository.save(category);
            return new CategoryResponse(
                categorySave.getId(), 
                categorySave.getName(), 
                categorySave.getStatus()
            );
        } else {
            throw new RuntimeException("Category not found with id: " + id);
        }
    }

    @Override
    public void deleteCategory(Long id) {
        Optional<Category> categoryOptional = this.categoryRepository.findById(id);
        if(categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            this.categoryRepository.delete(category);
        }
    }

    @Override
    public Optional<CategoryResponse> getCategoryById(Long id) {
        Optional<Category> categoryOptional = this.categoryRepository.findById(id);
        if(categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            return Optional.of(new CategoryResponse(
                category.getId(), 
                category.getName(), 
                category.getStatus()
            ));
        } else {
            throw new RuntimeException("Category not found with id: " + id);
        }
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> listCategory = this.categoryRepository.findAll();
        List<CategoryResponse> listCategoryResponses = listCategory.stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getStatus()
                ))
                .toList();
        return listCategoryResponses;
    }
    

}
