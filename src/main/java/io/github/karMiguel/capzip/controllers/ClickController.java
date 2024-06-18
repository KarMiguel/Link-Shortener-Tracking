package io.github.karMiguel.capzip.controllers;

import io.github.karMiguel.capzip.dtos.ClickDTO;
import io.github.karMiguel.capzip.dtos.ClicksByCityDTO;
import io.github.karMiguel.capzip.dtos.ClicksByPeriodDTO;
import io.github.karMiguel.capzip.dtos.mapper.ClickMapper;
import io.github.karMiguel.capzip.model.Click;
import io.github.karMiguel.capzip.model.LinkShort;
import io.github.karMiguel.capzip.model.Users;
import io.github.karMiguel.capzip.security.JwtUserDetails;
import io.github.karMiguel.capzip.services.ClickServices;
import io.github.karMiguel.capzip.services.LinkShortServices;
import io.github.karMiguel.capzip.services.UserServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "Clicks", description = "Endpoints for Managing clicks links")
@RestController
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ClickController {

    private final LinkShortServices linkShortService;

    private final UserServices userServices;

    private final ClickServices clickServices;
    @CrossOrigin(origins = "*")
    @GetMapping("/{shortLink}")
    public void redirectToOriginalLink(@PathVariable String shortLink, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String decodedShortLink = URLDecoder.decode(shortLink, StandardCharsets.UTF_8.toString());

        log.info("Decodificado shortLink: {}", decodedShortLink);

        LinkShort redirect = linkShortService.findByLinkLong(decodedShortLink);
        String ip = clickServices.getIp();
        String localization = clickServices.getLocationFromIp(ip);

        Click click = new Click();
        click.setIp(ip);
        click.setLocalization(localization);
        click.setLinkShort(redirect);
        click.setUserAgent(request.getHeader("User-Agent"));

        log.info("Criando objeto ClickDTO: {}", click);

        try {
            clickServices.saveClick(click);
            log.info("Clique salvo com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao salvar clique: ", e);
        }

        response.sendRedirect(redirect.getLinkLong());
    }

    //clicks by short link
    @GetMapping("api/v1/clicks")
    public ResponseEntity<PagedModel<EntityModel<ClickDTO>>> clicksByShortLink(
            @RequestParam String shortlink,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "linkShort.shortLink"));
        Page<ClickDTO> clicksPage = clickServices.getClicksByShortLink(shortlink, pageable);

        PagedModel<EntityModel<ClickDTO>> pagedModel = PagedModel.of(
                clicksPage.map(EntityModel::of).getContent(),
                new PagedModel.PageMetadata(clicksPage.getSize(), clicksPage.getNumber(), clicksPage.getTotalElements())
        );

        return ResponseEntity.ok(pagedModel);
    }
    @GetMapping("/clicks/city/")
    public ResponseEntity<List<ClicksByCityDTO>> getClicksByCity(@RequestParam String shortLink, @AuthenticationPrincipal JwtUserDetails userDetails) {
        Users user = userServices.findByEmail(userDetails.getUsername());
        List<ClicksByCityDTO> clicksByCity = clickServices.getClicksByCity(user, shortLink);
        return ResponseEntity.ok(clicksByCity);
    }

    @GetMapping("/clicks/period/")
    public ResponseEntity<ClicksByPeriodDTO> getClicksByPeriod(@RequestParam String shortLink) {
        ClicksByPeriodDTO clicksByPeriod = clickServices.getClicksByPeriod(shortLink);
        return ResponseEntity.ok(clicksByPeriod);
    }

    @GetMapping("/clicks/count")
    public ResponseEntity<Integer> countCLicks() {
        return ResponseEntity.ok(clickServices.countClicks());
    }
    @GetMapping("/greet/{name}")
    public ResponseEntity<String> greetUser(@PathVariable String name) {
        return ResponseEntity.ok("Olá, " + name + "!");
    }
}
