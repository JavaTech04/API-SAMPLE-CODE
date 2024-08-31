package com.javatech.utils;

public interface AppConst {
    String SORT_BY = "(\\w+?)(:)(.*)";
    String SORT_BY_VALID = "(\\w+?)(:)(asc|desc)";
    String SEARCH_OPERATOR = "(\\w+?)(:|<|>)(.*)";
    String SEARCH_SPEC_OPERATOR = "(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)";
}