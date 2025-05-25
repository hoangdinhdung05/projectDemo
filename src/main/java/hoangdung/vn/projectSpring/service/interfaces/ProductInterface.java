package hoangdung.vn.projectSpring.service.interfaces;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import hoangdung.vn.projectSpring.dto.request.ProductRequest;
import hoangdung.vn.projectSpring.dto.response.ProductResponse;

public interface ProductInterface {
    
    ProductResponse createProduct(ProductRequest request, MultipartFile thumbnail, List<MultipartFile> images);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductResponse updateProduct(Long id, ProductRequest request, MultipartFile thumbnail, List<MultipartFile> images);
    void deleteProduct(Long id);
}
