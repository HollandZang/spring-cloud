package com.holland.gateway;

import org.springframework.data.repository.CrudRepository;

public interface RouteWhitelistRepo extends CrudRepository<Object, String> {
    @Override
    Iterable<Object> findAll();
}
