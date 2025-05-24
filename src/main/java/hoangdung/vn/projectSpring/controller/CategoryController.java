package hoangdung.vn.projectSpring.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.CategoryResponse;
import hoangdung.vn.projectSpring.entity.Category;
import hoangdung.vn.projectSpring.service.CategoryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        ApiResponse<CategoryResponse> response = ApiResponse.ok(categoryService.createCategory(category), "Success");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id) {
        ApiResponse<CategoryResponse> response = ApiResponse.ok(categoryService.updateCategory(id), "Success");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        ApiResponse<Void> response = ApiResponse.ok(null, "Success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllCategories() {
        ApiResponse<List<CategoryResponse>> response = ApiResponse.ok(categoryService.getAllCategories(), "Success");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        Optional<CategoryResponse> category = categoryService.getCategoryById(id);
        ApiResponse<CategoryResponse> response = ApiResponse.ok(category.get(), "Success");
        return ResponseEntity.ok(response);
    }
}
