package io.github.karMiguel.capzip.controllers;

import io.github.karMiguel.capzip.dtos.LinkShortOutDto;
import io.github.karMiguel.capzip.dtos.ShortLinkDto;
import io.github.karMiguel.capzip.dtos.TotalDto;
import io.github.karMiguel.capzip.exceptions.EntityNotFoundException;
import io.github.karMiguel.capzip.exceptions.InvalidJwtAuthenticationException;
import io.github.karMiguel.capzip.exceptions.LinkShortException;
import io.github.karMiguel.capzip.exceptions.ResponseSuccess;
import io.github.karMiguel.capzip.model.LinkShort;
import io.github.karMiguel.capzip.model.Users;
import io.github.karMiguel.capzip.security.JwtUserDetails;
import io.github.karMiguel.capzip.services.LinkShortServices;
import io.github.karMiguel.capzip.services.UserServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Link Short", description = "Endpoints for Managing Link Short")
@RestController
@RequiredArgsConstructor
public class LinkShortController {

    private final LinkShortServices linkShortServices;
    private final UserServices userServices;

    @Value("${DOMAIN_URL}")
    private String DOMAIN_URL;

    @PostMapping("/api/v1/link/shorten-link")
    public ResponseEntity<?> shortenLink(@RequestParam String link,
                                         @AuthenticationPrincipal JwtUserDetails userDetails) {

        if (userDetails == null){
            throw new InvalidJwtAuthenticationException("Não Autorizado!");
        }

        String urlRegex = "^(https?|ftp):\\/\\/[^\\s/$.?#].[^\\s]*$";
        if (!link.matches(urlRegex)) {
            throw new LinkShortException("Link inválido, não está em formato web.");
        }

        Users user = userServices.findByEmail(userDetails.getEmailAutentic());
        String shortLink = linkShortServices.generateShortLink(link, user);
        if (shortLink != null) {
            return ResponseEntity.ok(new ShortLinkDto(DOMAIN_URL+"/"+ shortLink+"/"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error shortening the link.");
        }
    }

    @PostMapping("/api/v1/link/shorten-link-no-auth")
    public ResponseEntity<?> shortenLinkNoAuth( @RequestParam String link) {

        String urlRegex = "^(https?|ftp):\\/\\/[^\\s/$.?#].[^\\s]*$";
        if (!link.matches(urlRegex)) {
            throw new LinkShortException("Link inválido, não está em formato web.");
        }

        Users user = userServices.findById(Long.valueOf(1));
        String shortLink = linkShortServices.generateShortLink(link, user);
        if (shortLink != null) {
            return ResponseEntity.ok(new ShortLinkDto(DOMAIN_URL+"/"+ shortLink+"/"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error shortening the link.");
        }
    }

    @GetMapping("/api/v1/link/my-link-short")
    public ResponseEntity<Page<LinkShortOutDto>> listAllShortLinks(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "qtdClick") String sortBy) {

        if (userDetails == null){
            throw new InvalidJwtAuthenticationException("Não Autorizado!");
        }

        Users user = userServices.findByEmail(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending()); // Ordenação decrescente por default

        Page<LinkShortOutDto> linkShortOutDtos = linkShortServices.listAllShortLinks(user, pageable);

        if (linkShortOutDtos != null && !linkShortOutDtos.isEmpty()) {
            return ResponseEntity.ok(linkShortOutDtos);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }
    }
    @DeleteMapping("/{shortLink}")
    public ResponseEntity<?> deleteShortLink(
            @PathVariable String shortLink,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        if (userDetails == null){
            throw new InvalidJwtAuthenticationException("Não Autorizado!");
        }

        String extractedCode = shortLink.substring(shortLink.lastIndexOf('/') + 1);

        LinkShort linkShort = linkShortServices.findByShortLink(extractedCode);
        if (linkShort == null) {
            throw new EntityNotFoundException("Link não encontrado.");
        }
        if (!linkShort.getUser().getId().equals(userDetails.getId())) {
            throw new InvalidJwtAuthenticationException("Esse link não pertence a você.");
        }

        boolean deleted = linkShortServices.deleteShortLink(extractedCode, userDetails.getId());

        if (deleted) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new ResponseSuccess("Link deletado com sucesso."));
        } else {
            throw new EntityNotFoundException("Link não encontrado.");
        }
    }
    @GetMapping("/api/v1/total/short-link")
    public ResponseEntity<TotalDto> countShortLinks() {
        int totalLinks = Math.toIntExact(linkShortServices.countShortLinks());

        return ResponseEntity.ok(new TotalDto(totalLinks));
    }
}