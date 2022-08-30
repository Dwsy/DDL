package link.dwsy.ddl.util;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@Data
@NoArgsConstructor
public class PageDataConverter<FROM, TO> {
    private boolean first;
    private boolean last;
    private int totalPages;
    private Long totalElements;
    private int pageSize;
    private int pageNumber;
    private boolean empty;
    private List<TO> content;

    public PageDataConverter(Page<FROM> page, converter<FROM, TO> c) {
        this.pageNumber = page.getNumber()+1;
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.pageSize = page.getSize();
        this.content = new ArrayList<>();
        page.getContent().forEach(from -> {
            TO to = c.convert(from);
            if (to != null)
                this.content.add(to);
        });
    }
    public interface converter<FROM, TO> {
        TO convert(FROM f);
    }
}
