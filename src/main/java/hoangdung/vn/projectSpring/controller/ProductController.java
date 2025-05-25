package hoangdung.vn.projectSpring.controller;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import hoangdung.vn.projectSpring.dto.request.ProductRequest;
import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.ProductResponse;
import hoangdung.vn.projectSpring.service.ProductService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam Long categoryId,
            @RequestParam String description,
            @RequestParam String shortDescription,
            @RequestParam String sku,
            @RequestParam Integer stockQuantity
    ) {

        ProductRequest request = ProductRequest.builder()
            .name(name)
            .price(price)
            .categoryId(categoryId)
            .description(description)
            .shortDescription(shortDescription)
            .sku(sku)
            .stockQuantity(stockQuantity)
            .build();

        ProductResponse response = productService.createProduct(request, thumbnail, images);
        ApiResponse<ProductResponse> apiResponse = ApiResponse.ok(response, "Success");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        ApiResponse<List<ProductResponse>> apiResponse = ApiResponse.ok(products, "Success");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        ApiResponse<ProductResponse> apiResponse = ApiResponse.ok(response, "Success");
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        ProductResponse response = productService.updateProduct(id, request, thumbnail, images);
        ApiResponse<ProductResponse> apiResponse = ApiResponse.ok(response, "Success");
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        ApiResponse<Void> apiResponse = ApiResponse.ok(null, "Success");
        return ResponseEntity.ok(apiResponse);
    }
}
