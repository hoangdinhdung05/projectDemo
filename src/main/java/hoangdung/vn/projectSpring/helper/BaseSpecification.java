package hoangdung.vn.projectSpring.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class BaseSpecification<T> {
    
    public static <T> Specification<T> keywordSpec(String keyword, String... fields) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (keyword == null || keyword.isEmpty()) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            for (String field : fields) {
                predicates.add(cb.like(
                        cb.lower(root.get(field).as(String.class)),
                        "%" + keyword.toLowerCase() + "%"));
            }

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static <T> Specification<T> whereSpec(Map<String, String> filters) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = filters.entrySet().stream()
                    .map(entry -> cb.equal(root.get(entry.getKey()), entry.getValue()))
                    .collect(Collectors.toList());
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static <T> Specification<T> complexWhereSpec(Map<String, Map<String, String>> filters){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = filters.entrySet().stream()
                .flatMap((entry) -> entry.getValue().entrySet().stream()
                    .map(condition -> {
                        String field = entry.getKey();
                        String operator = condition.getKey();
                        String value = condition.getValue();

                        switch (operator.toLowerCase()) {
                            case "eq" -> {
                                return criteriaBuilder.equal(root.get(field), value);
                            }
                            case "gt" -> {
                                return criteriaBuilder.greaterThan(root.get(field), value);
                            }
                            case "gte" -> {
                                return criteriaBuilder.greaterThanOrEqualTo(root.get(field), value);
                            }
                            case "lt" -> {
                                return criteriaBuilder.lessThan(root.get(field), value);
                            }
                            case "lte" -> {
                                return criteriaBuilder.lessThanOrEqualTo(root.get(field), value);
                            }
                            case "in" -> {
                                List<String> values = List.of(value.split(","));
                                return root.get(field).in(values);
                            }
                            default -> throw new IllegalArgumentException("Toán tử " + operator + " không được hỗ trợ");
                        }
                    }))
                .collect(Collectors.toList());
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
