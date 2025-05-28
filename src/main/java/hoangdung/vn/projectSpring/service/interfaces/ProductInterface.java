package hoangdung.vn.projectSpring.service.interfaces;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import hoangdung.vn.projectSpring.dto.request.ProductRequest;
import hoangdung.vn.projectSpring.dto.response.ProductResponse;
import hoangdung.vn.projectSpring.entity.Product;


public interface ProductInterface {
    
    ProductResponse createProduct(ProductRequest request, MultipartFile thumbnail, List<MultipartFile> images);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductResponse updateProduct(Long id, ProductRequest request, MultipartFile thumbnail, List<MultipartFile> images);
    void deleteProduct(Long id);
    Page<ProductResponse> paginate(Map<String, String[]> parameters);
    List<Product> getAll(Map<String, String[]> parameters);
}
