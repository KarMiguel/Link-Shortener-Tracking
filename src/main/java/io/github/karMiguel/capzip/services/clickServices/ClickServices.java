package io.github.karMiguel.capzip.services.clickServices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import io.github.karMiguel.capzip.dtos.clickDto.ClickDTO;
import io.github.karMiguel.capzip.dtos.clickDto.ClicksByCityDTO;
import io.github.karMiguel.capzip.dtos.clickDto.ClicksByPeriodDTO;
import io.github.karMiguel.capzip.dtos.mapper.ClickMapper;
import io.github.karMiguel.capzip.exceptions.EntityNotFoundException;
import io.github.karMiguel.capzip.model.click.Click;
import io.github.karMiguel.capzip.model.linkShort.LinkShort;
import io.github.karMiguel.capzip.model.users.Users;
import io.github.karMiguel.capzip.repository.ClickRepository;
import io.github.karMiguel.capzip.repository.LinkShortRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClickServices {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ClickRepository clickRepository;
    private final LinkShortRepository linkShortRepository;
    private final ResourceLoader resourceLoader;
    private DatabaseReader dbReader;

    @PostConstruct
    public void initialize() {
        try {
            Resource resource = resourceLoader.getResource("classpath:GeoIP2-City.mmdb");
            File database = resource.getFile();
            this.dbReader = new DatabaseReader.Builder(database).build();
        } catch (IOException e) {
            throw new RuntimeException("Error initializing GeoIP Database Reader", e);
        }
    }

    @Transactional
    public void saveClick(Click click) {
        clickRepository.save(click);

    }

    public long getTotalClicksByShortLink(String shortLink) {
        LinkShort linkShort = linkShortRepository.findByShortLink(shortLink);
        if (linkShort == null) {
            throw new EntityNotFoundException("Link encurtado não encontrado.");
        }
        return clickRepository.countByLinkShort(linkShort);
    }

    public String getLocationFromIp(String ip) {
        String url = String.format("https://ipinfo.io/%s/geo", ip);
        String response = restTemplate.getForObject(url, String.class);

        if (response != null) {
            return parseLocation(response);
        } else {
            return "Localização não encontrada.";
        }
    }

    public String getIp() {
        return restTemplate.getForObject("https://api.ipify.org/", String.class);
    }

    private String parseLocation(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            String city = jsonNode.get("city").asText();
            String region = jsonNode.get("region").asText();
            String country = jsonNode.get("country").asText();

            if (!city.isEmpty()) {
                return String.format("%s / %s - %s", city, region, country);
            } else {
                return "Localização não encontrada.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao fazer parsing da localização.";
        }
    }
    public String getClientLocation(String request) throws IOException, GeoIp2Exception {

        InetAddress ipAddress = InetAddress.getByName(request);
        CityResponse response = dbReader.city(ipAddress);

        String cityName = response.getCity().getName();
        String countryName = response.getCountry().getName();
        String postalCode = response.getPostal().getCode();

        return "Client Location: " + cityName + ", " + countryName + " (" + postalCode + ")";
    }
    public Page<ClickDTO> getClicksByShortLink(String shortLink, Pageable pageable) {
        if (shortLink.endsWith("/")) {
            shortLink = shortLink.substring(0, shortLink.length() - 1);
        }
        String extractedCode = shortLink.substring(shortLink.lastIndexOf('/') + 1);
        Page<Click> clicks = clickRepository.findByShortLink(extractedCode, pageable);
        return ClickMapper.toPageDto(clicks);
    }

    public int countClicks() {
        return Math.toIntExact(clickRepository.count());
    }

    public Page<ClicksByCityDTO> getClicksByCity(Users user, String shortLink, Pageable pageable) {
        if (shortLink.endsWith("/")) {
            shortLink = shortLink.substring(0, shortLink.length() - 1);
        }
        String extractedCode = shortLink.substring(shortLink.lastIndexOf('/') + 1);
        Page<Object[]> results = clickRepository.countClicksByCity(extractedCode, user, pageable);
        return results.map(result -> new ClicksByCityDTO((String) result[0], (Long) result[1]));
    }
    public ClicksByPeriodDTO getClicksByPeriod(String shortLink) {
        if (shortLink.endsWith("/")) {
            shortLink = shortLink.substring(0, shortLink.length() - 1);
        }
        String extractedCode = shortLink.substring(shortLink.lastIndexOf('/') + 1);

        List<Object[]> results = clickRepository.countClicksByPeriod(extractedCode);

        ClicksByPeriodDTO clicksByPeriod = new ClicksByPeriodDTO(0, 0, 0,0);

        for (Object[] result : results) {
            String period = (String) result[0];
            long count = (Long) result[1];

            switch (period) {
                case "Manhã":
                    clicksByPeriod.setCountMorning(count);
                    break;
                case "Tarde":
                    clicksByPeriod.setCountAfternoon(count);
                    break;
                case "Noite":
                    clicksByPeriod.setCountNight(count);
                    break;
                default:
                    clicksByPeriod.setCountDawn(count);
                    break;
            }
        }

        return clicksByPeriod;
    }

}
