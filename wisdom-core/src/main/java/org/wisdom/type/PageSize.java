package org.wisdom.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PageSize {
    private int page;

    @Getter(AccessLevel.NONE)
    private int size;

    public int getSize(){
        return size == 0 ? Byte.MAX_VALUE : size;
    }
}
