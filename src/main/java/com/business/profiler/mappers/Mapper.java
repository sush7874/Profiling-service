package com.business.profiler.mappers;

public interface Mapper <K, V>{
    V map(K model);

    K reverseMap(V entity);
}
