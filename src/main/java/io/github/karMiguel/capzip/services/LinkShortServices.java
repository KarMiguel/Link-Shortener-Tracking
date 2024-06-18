package io.github.karMiguel.capzip.services;

import io.github.karMiguel.capzip.dtos.LinkShortOutDto;
import io.github.karMiguel.capzip.dtos.mapper.LinkShortMapper;
import io.github.karMiguel.capzip.model.LinkShort;
import io.github.karMiguel.capzip.model.Users;
import io.github.karMiguel.capzip.repository.ClickRepository;
import io.github.karMiguel.capzip.repository.LinkShortRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class LinkShortServices {
    @Autowired
    private LinkShortRepository linkShortRepository;
    @Autowired
    private  ClickRepository clickRepository;

    @Value("${DOMAIN_URL}")
    private String DOMAIN_URL;
    private final String caracteres = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String generateShortLink(String linkLong, Users user) {
        LinkShort existingLink = linkShortRepository.findByLinkLongAndUser(linkLong, user);
        if (existingLink != null) {
            return existingLink.getShortLink();
        } else {
            String newShortLink = generateNewShortLink();
            LinkShort linkShort = new LinkShort();
            linkShort.setLinkLong(linkLong);
            linkShort.setUser(user);
            linkShort.setShortLink(newShortLink);
            linkShortRepository.save(linkShort);
            return linkShort.getShortLink();
        }
    }

    private String generateNewShortLink() {
        String newShortLink;
        do {
            newShortLink = generateRandomString(5);
        } while (linkShortRepository.findByShortLink(newShortLink) != null); // Verifica se o short link gerado já existe no banco de dados
        return newShortLink;
    }
    public LinkShort saveLink(LinkShort linkShort , Users user) {
        linkShort.setUser(user);
        return linkShortRepository.save(linkShort);
    }
    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }


    public String getShortLinkWithoutAuth(String linkLong) {
        LinkShort linkShort = linkShortRepository.findByLinkLong(linkLong);
        return linkShort != null ? linkShort.getShortLink() : null;
    }
    public List<LinkShortOutDto> listAllShortLinks(Users user) {
        List<LinkShort> linkShorts = linkShortRepository.findByUser(user);
        return LinkShortMapper.toListDto(linkShorts, clickRepository,DOMAIN_URL);
    }

    public LinkShort findByLinkLong(String shortLink) {
        log.info("code extraido = {}",shortLink);

        return linkShortRepository.findByShortLink(shortLink);
    }

    // public List<LinkShort> listAllShortLinksOrderedByClicks(User user) {
     //   return linkShortRepository.findAllByUserOrderByClicksDesc(user);
    //}

    public boolean deleteShortLink(String shortLink) {
        log.info("short link extraido = {}",shortLink);

       // String extractedCode = shortLink.substring(shortLink.lastIndexOf('/') + 1);
       // log.info("code extraido = {}",extractedCode);

        LinkShort linkShort = linkShortRepository.findByShortLink(shortLink);
        log.info("linkShort = {}",linkShort);

        if (linkShort != null) {
            linkShortRepository.delete(linkShort);
            return true;
        }
        return false;
    }
    public List<LinkShort> getShortLinksByIdAndLink(Users user, String shortLink) {
        return linkShortRepository.findAllByUserAndShortLinkNot(user, shortLink);
    }
    public Page<LinkShortOutDto> listAllShortLinks(Users user, Pageable pageable) {
        Page<LinkShort> linkShortPage = linkShortRepository.findAllByUser(user, pageable);
        return linkShortPage.map(linkShort -> {
            Long clickCount = clickRepository.countClicksByShortLink(linkShort.getShortLink());
            return LinkShortMapper.toResponse(linkShort, clickCount, DOMAIN_URL);
        });
    }
    public int countShortLinks() {
        return (int) linkShortRepository.count();
    }

}
