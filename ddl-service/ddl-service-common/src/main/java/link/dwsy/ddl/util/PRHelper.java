package link.dwsy.ddl.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @Author Dwsy
 * @Date 2022/8/29
 */

public class PRHelper {
    public static PageRequest order(String order,String[] properties,int page,int size) {
        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), properties);
        return PageRequest.of(page <= 0 ? 0 : page-1, Math.min(size, 20), sort);
    }

    public static PageRequest order(Sort.Direction direction,String[] properties,int page,int size) {
        Sort sort = Sort.by(direction, properties);
        return PageRequest.of(page <= 0 ? 0 : page-1, Math.min(size, 20), sort);
    }
    public static Sort sort(String order, String[] properties) {
        return Sort.by(Sort.Direction.valueOf(order.toUpperCase()), properties);
    }
}
