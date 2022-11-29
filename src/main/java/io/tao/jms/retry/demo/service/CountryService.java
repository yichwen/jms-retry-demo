package io.tao.jms.retry.demo.service;

import io.tao.jms.retry.demo.model.Country;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CountryService {

    private Map<String, Country> countries = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        countries.put("SG", new Country("Singapore", "SG", "Republic of Singapore"));
        countries.put("MY", new Country("Malaysia", "MY", "Malaysia"));
        countries.put("ID", new Country("Indonesia", "ID", "Republic of Indonesia"));
        countries.put("VN", new Country("Vietnam", "VN", "Socialist Republic of Vietnam"));
        countries.put("BN", new Country("Brunei", "BN", "Nation of Brunei, the Abode of Peace"));
    }

    public List<Country> getCountries() {
        return new ArrayList<>(countries.values());
    }

    public Country getCountryByCode(String code) {
        return countries.get(code);
    }

}
