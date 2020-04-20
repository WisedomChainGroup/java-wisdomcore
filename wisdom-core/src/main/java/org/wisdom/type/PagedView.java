package org.wisdom.type;

import lombok.Value;

import java.util.List;

@Value
public class PagedView<T> {
    private int total;
    private int page;

    private List<T> records;

    public int getSize() {
        return records == null ? 0 : records.size();
    }
}
