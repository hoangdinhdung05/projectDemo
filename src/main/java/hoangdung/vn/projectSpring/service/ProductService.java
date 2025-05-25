package hoangdung.vn.projectSpring.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import hoangdung.vn.projectSpring.dto.request.ProductRequest;
import hoangdung.vn.projectSpring.dto.response.ProductResponse;
import hoangdung.vn.projectSpring.entity.Category;
import hoangdung.vn.projectSpring.entity.Product;
import hoangdung.vn.projectSpring.entity.ProductImage;
import hoangdung.vn.projectSpring.repository.CategoryRepository;
import hoangdung.vn.projectSpring.repository.ProductImageRepository;
import hoangdung.vn.projectSpring.repository.ProductRepository;
import hoangdung.vn.projectSpring.service.interfaces.ProductInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductInterface {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    private String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + filename);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    @Override
    public ProductResponse createProduct(ProductRequest request, MultipartFile thumbnail, List<MultipartFile> images) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setCategory(category);

        // Lưu thumbnail nếu có
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailPath = saveFile(thumbnail);
            product.setThumbnail("/uploads/" + thumbnailPath);
        }

        Product saved = productRepository.save(product);

        // Lưu images nếu có
        if (images != null && !images.isEmpty()) {
            List<ProductImage> imageEntities = images.stream()
                    .map(file -> {
                        String fileName = saveFile(file);
                        return new ProductImage(null, "/uploads/" + fileName, saved);
                    })
                    .collect(Collectors.toList());
            productImageRepository.saveAll(imageEntities);
            saved.setImages(imageEntities);
        }

        return mapToResponse(saved);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToResponse(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, MultipartFile thumbnail, List<MultipartFile> images) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        BeanUtils.copyProperties(request, product);
        product.setCategory(category);

        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailPath = saveFile(thumbnail);
            product.setThumbnail("/uploads/" + thumbnailPath);
        }

        if (images != null && !images.isEmpty()) {
            // Xóa ảnh cũ trước khi thêm ảnh mới
            productImageRepository.deleteByProduct(product);
            List<ProductImage> newImages = images.stream()
                    .map(file -> {
                        String fileName = saveFile(file);
                        return new ProductImage(null, "/uploads/" + fileName, product);
                    })
                    .collect(Collectors.toList());
            productImageRepository.saveAll(newImages);
            product.setImages(newImages);
        }

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    // Hàm tiện ích map entity -> DTO response
    private ProductResponse mapToResponse(Product product) {
        List<String> imageUrls = product.getImages() == null ? List.of() :
                product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getThumbnail(),
                imageUrls
        );
    }
}
