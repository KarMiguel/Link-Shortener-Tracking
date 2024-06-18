package io.github.karMiguel.capzip.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.karMiguel.capzip.dtos.ClickDTO;
import io.github.karMiguel.capzip.dtos.ClicksByCityDTO;
import io.github.karMiguel.capzip.dtos.ClicksByPeriodDTO;
import io.github.karMiguel.capzip.dtos.mapper.ClickMapper;
import io.github.karMiguel.capzip.exceptions.EntityNotFoundException;
import io.github.karMiguel.capzip.model.Click;
import io.github.karMiguel.capzip.model.LinkShort;
import io.github.karMiguel.capzip.model.Users;
import io.github.karMiguel.capzip.repository.ClickRepository;
import io.github.karMiguel.capzip.repository.LinkShortRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClickServices {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ClickRepository clickRepository;
    private final LinkShortRepository linkShortRepository;

    @Transactional
    public void saveClick(Click click) {
        log.info("Salvando click:");
        clickRepository.save(click);
        log.info("Salvooooooooooo bddd click: {}", click);

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

    public Page<ClickDTO> getClicksByShortLink(String shortLink, Pageable pageable) {
        String extractedCode = shortLink.substring(shortLink.lastIndexOf('/') + 1);
        Page<Click> clicks = clickRepository.findByShortLink(extractedCode, pageable);
        return ClickMapper.toPageDto(clicks);
    }

    public int countClicks() {
        return Math.toIntExact(clickRepository.count());
    }

    public List<ClicksByCityDTO> getClicksByCity(Users user, String shortLink) {
        String extractedCode = shortLink.substring(shortLink.lastIndexOf('/') + 1);
        List<Object[]> results = clickRepository.countClicksByCity(extractedCode, user);
        return results.stream()
                .map(result -> new ClicksByCityDTO((String) result[0], (Long) result[1]))
                .collect(Collectors.toList());
    }
    public ClicksByPeriodDTO getClicksByPeriod(String shortLink) {
        String extractedCode = shortLink.substring(shortLink.lastIndexOf('/') + 1);

        List<Object[]> results = clickRepository.countClicksByPeriod(extractedCode);

        ClicksByPeriodDTO clicksByPeriod = new ClicksByPeriodDTO(0, 0, 0);

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
                    break;
            }
        }

        return clicksByPeriod;
    }

}
