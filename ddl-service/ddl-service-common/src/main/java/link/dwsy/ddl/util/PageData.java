package link.dwsy.ddl.util;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@Data
@NoArgsConstructor
public class PageData<P> {
    private boolean first;
    private boolean last;
    private int totalPages;
    private Long totalElements;
    private int pageSize;
    private int pageNumber;
    private boolean empty;
    private List<P> content;

    public PageData(boolean first, boolean last, int totalPages, Long totalElements, int pageSize, int pageNumber, boolean empty, List<P> content) {
        this.first = first;
        this.last = last;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.empty = empty;
        this.content = content;
    }

    public PageData(Page<P> page) {
        this.pageNumber = page.getNumber()+1;
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.pageSize = page.getSize();
//        this.content = new ArrayList<>();
        this.content=page.getContent();
    }

    @SuppressWarnings("all")
    public PageData(Page page, List<P> content) {
        this.pageNumber = page.getNumber()+1;
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.pageSize = page.getSize();
//        this.content = new ArrayList<>();
        this.content=content;
    }

}
