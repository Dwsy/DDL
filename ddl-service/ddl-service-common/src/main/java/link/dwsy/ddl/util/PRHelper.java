package link.dwsy.ddl.util;

import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/29
 */

public class PRHelper {
    public static PageRequest order(String order, String[] properties, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), properties);
        return PageRequest.of(page <= 0 ? 0 : page - 1, Math.min(size, 20), sort);
    }

    public static PageRequest order(Sort.Direction direction, String[] properties, int page, int size) {
        Sort sort = Sort.by(direction, properties);
        return PageRequest.of(page <= 0 ? 0 : page - 1, Math.min(size, 20), sort);
    }

    public static PageRequest order(Sort.Direction direction, String property, int page, int size) {
        Sort sort = Sort.by(direction, property);
        return PageRequest.of(page <= 0 ? 0 : page - 1, Math.min(size, 20), sort);
    }

    public static Sort sort(String order, String[] properties) {
        return Sort.by(Sort.Direction.valueOf(order.toUpperCase()), properties);
    }

    public static Sort sort(String[] order, String[] properties) {
        if (order.length != properties.length) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i < order.length; i++) {
            orders.add(new Sort.Order(Sort.Direction.valueOf(order[i].toUpperCase()), properties[i]));
        }
        return Sort.by(orders);

    }
}
