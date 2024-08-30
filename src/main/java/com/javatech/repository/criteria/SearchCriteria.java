package com.javatech.repository.criteria;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCriteria {
    private String key; // id, firstName, lastName ...
    private String operation; // :, <, > ...
    private Object value;
}
