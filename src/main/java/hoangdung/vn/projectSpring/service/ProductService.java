package hoangdung.vn.projectSpring.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import hoangdung.vn.projectSpring.dto.request.ProductRequest;
import hoangdung.vn.projectSpring.dto.response.ProductResponse;
import hoangdung.vn.projectSpring.entity.Category;
import hoangdung.vn.projectSpring.entity.Product;
import hoangdung.vn.projectSpring.entity.ProductImage;
import hoangdung.vn.projectSpring.helper.BaseSpecification;
import hoangdung.vn.projectSpring.helper.FilterParameter;
import hoangdung.vn.projectSpring.repository.CategoryRepository;
import hoangdung.vn.projectSpring.repository.ProductImageRepository;
import hoangdung.vn.projectSpring.repository.ProductRepository;
import hoangdung.vn.projectSpring.service.interfaces.ProductInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService extends BaseService implements ProductInterface {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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

    @Override
    public Page<ProductResponse> paginate(Map<String, String[]> parameters) {
        int page = parameters.containsKey("page") ? Integer.parseInt(parameters.get("page")[0]) : 1;
        int perpage = parameters.containsKey("perpage") ? Integer.parseInt(parameters.get("perpage")[0]) : 10;

        // Sort
        String sortParam = parameters.containsKey("sort") ? parameters.get("sort")[0] : null;
        Sort sort = createSort(sortParam);
        // Filters
        String keyword = FilterParameter.filterKeyword(parameters);
        Map<String, String> simpleFilters = FilterParameter.filterSimple(parameters);
        Map<String, Map<String, String>> complexFilters = FilterParameter.filterComplex(parameters);

        logger.info("Keyword: " + keyword);
        logger.info("Simple filters: " + simpleFilters);
        logger.info("Complex filters: " + complexFilters);
        // logger.info("Date range filters: " + dateRangeFilters);

        Specification<Product> specs = Specification.where(BaseSpecification.<Product>keywordSpec(keyword, "name"))
                .and(BaseSpecification.<Product>whereSpec(simpleFilters))
                .and(BaseSpecification.<Product>complexWhereSpec(complexFilters));

        Pageable pageable = PageRequest.of(page - 1, perpage, sort);    
        Page<Product> productPage = this.productRepository.findAll(specs, pageable);

        return productPage.map(this::mapToResponse);
    }

    @Override
    public List<Product> getAll(Map<String, String[]> parameters) {
        String sortParam = parameters.containsKey("sort") ? parameters.get("sort")[0] : null;
        Sort sort = createSort(sortParam);
        String keyword = FilterParameter.filterKeyword(parameters);
        Map<String, String> simpleFilters = FilterParameter.filterSimple(parameters);
        Map<String, Map<String, String>> complexFilters = FilterParameter.filterComplex(parameters);

        logger.info("Keyword: " + keyword);
        logger.info("Simple filters: " + simpleFilters);
        logger.info("Complex filters: " + complexFilters);

        Specification<Product> specs = Specification.where(BaseSpecification.<Product>keywordSpec(keyword, "name"))
                .and(BaseSpecification.<Product>whereSpec(simpleFilters))
                .and(BaseSpecification.<Product>complexWhereSpec(complexFilters));

        return this.productRepository.findAll(specs, sort);
    }

}
