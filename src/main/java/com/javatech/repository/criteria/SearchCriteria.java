package com.javatech.repository.criteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private String key; // id, firstName, lastName ...
    private String operation; // :, <, > ...
    private Object value;
}
