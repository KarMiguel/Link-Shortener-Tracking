package io.github.karMiguel.capzip.controllers;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import io.github.karMiguel.capzip.dtos.clickDto.ClickDTO;
import io.github.karMiguel.capzip.dtos.clickDto.ClicksByCityDTO;
import io.github.karMiguel.capzip.dtos.clickDto.ClicksByPeriodDTO;
import io.github.karMiguel.capzip.dtos.shortLinkDto.TotalDto;
import io.github.karMiguel.capzip.exceptions.InvalidJwtAuthenticationException;
import io.github.karMiguel.capzip.exceptions.UrlRedirectException;
import io.github.karMiguel.capzip.model.click.Click;
import io.github.karMiguel.capzip.model.linkShort.LinkShort;
import io.github.karMiguel.capzip.model.users.Users;
import io.github.karMiguel.capzip.security.JwtUserDetails;
import io.github.karMiguel.capzip.services.clickServices.ClickServices;
import io.github.karMiguel.capzip.services.linkShortServices.LinkShortServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Tag(name = "Clicks", description = "Endpoints for Managing clicks links")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ClickController {

    private final LinkShortServices linkShortService;


    private final ClickServices clickServices;

    @CrossOrigin(origins = "*")
    @GetMapping("/{shortLink}/")
    public void redirectToOriginalLink(@PathVariable String shortLink, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String decodedShortLink = URLDecoder.decode(shortLink, StandardCharsets.UTF_8.toString());

            Users user = linkShortService.findUserByShortLink(decodedShortLink);
            LinkShort redirect = linkShortService.findByLinkLong(decodedShortLink);

            if (redirect == null) {
                throw new UrlRedirectException("URL não encontrada para redirecionamento.");
            }
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            if (user.getId() != 1L) {
                Click click = new Click();
                //click.setIp(request.getHeader("X-FORWARDED-FOR"));
                //click.setIp(clickServices.getIp());
                click.setIp(request.getRemoteAddr());
                click.setLocalization(clickServices.getLocationFromIp(request.getRemoteAddr()));
                //click.setLocalization(clickServices.getClientLocation(request.getRemoteAddr()));
                click.setLinkShort(redirect);
                click.setUserAgent(request.getHeader("User-Agent"));

                log.info("Criando objeto ClickDTO: {}", click);

                try {
                    clickServices.saveClick(click);
                    log.info("Clique salvo com sucesso.");
                } catch (Exception e) {
                    log.error("Erro ao salvar clique: ", e);
                }
            }
            response.sendRedirect(redirect.getLinkLong());
        } catch (UnsupportedEncodingException e) {
            log.error("Erro ao decodificar shortLink", e);
            throw new UrlRedirectException("Erro interno do servidor ao decodificar shortLink");
        } catch (GeoIp2Exception e) {
            throw new RuntimeException(e);
        }
    }
    //clicks by short link
    @Operation(summary = "Get all clicks for a short link")
    @GetMapping("/api/v1/clicks/all")
    public ResponseEntity<PagedModel<EntityModel<ClickDTO>>> clicksByShortLink(
            @RequestParam String shortlink,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @AuthenticationPrincipal JwtUserDetails userDetails) {

        if (userDetails == null){
            throw new InvalidJwtAuthenticationException("Não Autorizado!");
        }

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "linkShort.shortLink"));
        Page<ClickDTO> clicksPage = clickServices.getClicksByShortLink(shortlink, pageable);

        PagedModel<EntityModel<ClickDTO>> pagedModel = PagedModel.of(
                clicksPage.map(EntityModel::of).getContent(),
                new PagedModel.PageMetadata(clicksPage.getSize(), clicksPage.getNumber(), clicksPage.getTotalElements())
        );

        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Get clicks by city")
    @GetMapping("/api/v1/clicks/by-city")
    public ResponseEntity<Page<ClicksByCityDTO>> getClicksByCity(
            @RequestParam String shortLink,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "localization") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @AuthenticationPrincipal JwtUserDetails userDetails) {

        if (userDetails == null){
            throw new InvalidJwtAuthenticationException("Não Autorizado!");
        }

        String extractedCode = shortLink.substring(shortLink.lastIndexOf('/') + 1);

        Users user = linkShortService.findUserByShortLink(extractedCode);

            if (user.getId() != userDetails.getId()) {
                throw new InvalidJwtAuthenticationException("Esse link não pertence a você.");
            }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort));
        Page<ClicksByCityDTO> clicksByCity = clickServices.getClicksByCity(user, shortLink, pageable);
        return ResponseEntity.ok(clicksByCity);
    }
    @Operation(summary = "Get clicks by period day")
    @GetMapping("/api/v1/clicks/by-period")
    public ResponseEntity<ClicksByPeriodDTO> getClicksByPeriod(
            @RequestParam String shortLink,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        if (userDetails == null){
            throw new InvalidJwtAuthenticationException("Não Autorizado!");
        }

        String extractedCode = shortLink.substring(shortLink.lastIndexOf('/') + 1);
        Users user = linkShortService.findUserByShortLink(extractedCode);
        if (user.getId() != userDetails.getId()) {
            throw new InvalidJwtAuthenticationException("Esse link não pertence a você.");
        }

        ClicksByPeriodDTO clicksByPeriod = clickServices.getClicksByPeriod(shortLink);
        return ResponseEntity.ok(clicksByPeriod);
    }
    @Operation(summary = "Count total clicks")
    @GetMapping("/api/v1/total/clicks")
    public ResponseEntity<TotalDto> countCLicks() {
        int totalClicks = Math.toIntExact(clickServices.countClicks());
        return ResponseEntity.ok(new TotalDto(totalClicks));
    }

}
