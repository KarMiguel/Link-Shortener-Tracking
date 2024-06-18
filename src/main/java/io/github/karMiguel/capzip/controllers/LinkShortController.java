package io.github.karMiguel.capzip.controllers;

import io.github.karMiguel.capzip.dtos.LinkShortOutDto;
import io.github.karMiguel.capzip.dtos.ShortLinkDto;
import io.github.karMiguel.capzip.exceptions.ResponseSuccess;
import io.github.karMiguel.capzip.model.Users;
import io.github.karMiguel.capzip.security.JwtUserDetails;
import io.github.karMiguel.capzip.services.LinkShortServices;
import io.github.karMiguel.capzip.services.UserServices;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.security.Principal;
import java.util.List;

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

        Users user = userServices.findByEmail(userDetails.getEmailAutentic());
        String shortLink = linkShortServices.generateShortLink(link, user);
        if (shortLink != null) {
            return ResponseEntity.ok(new ShortLinkDto(DOMAIN_URL+"/"+ shortLink));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error shortening the link.");
        }
    }


    @GetMapping("/api/v1/link/shorten/all")
    public ResponseEntity<Page<LinkShortOutDto>> listAllShortLinks(@AuthenticationPrincipal JwtUserDetails userDetails,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "id") String sortBy) {
        Users user = userServices.findByEmail(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<LinkShortOutDto> linkShortOutDtos = linkShortServices.listAllShortLinks(user, pageable);

        if (linkShortOutDtos != null && !linkShortOutDtos.isEmpty()) {
            return ResponseEntity.ok(linkShortOutDtos);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @DeleteMapping("/{shortLink}")
    public ResponseEntity<?> deleteShortLink(@PathVariable String shortLink) {

        boolean deleted = linkShortServices.deleteShortLink(shortLink);

        if (deleted) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new ResponseSuccess("Link deletado com sucesso."));
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseSuccess("Link não encontrado."));
        }
    }

    @GetMapping("/api/v1/link/shorten/count")
    public ResponseEntity<Integer> countShortLinks() {
        return ResponseEntity.ok(linkShortServices.countShortLinks());
    }
}