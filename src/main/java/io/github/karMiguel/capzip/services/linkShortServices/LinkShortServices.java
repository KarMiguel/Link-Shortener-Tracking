package io.github.karMiguel.capzip.services.linkShortServices;

import io.github.karMiguel.capzip.dtos.shortLinkDto.LinkShortOutDto;
import io.github.karMiguel.capzip.dtos.mapper.LinkShortMapper;
import io.github.karMiguel.capzip.model.linkShort.LinkShort;
import io.github.karMiguel.capzip.model.users.Users;
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
    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }

    public LinkShort findByShortLink(String shortLink) {
        return linkShortRepository.findByShortLink(shortLink);
    }

    public Users findUserByShortLink(String shortLink) {
        return linkShortRepository.findUserByShortLink(shortLink);
    }
    public List<LinkShortOutDto> listAllShortLinks(Users user) {
        List<LinkShort> linkShorts = linkShortRepository.findByUser(user);
        return LinkShortMapper.toListDto(linkShorts, clickRepository,DOMAIN_URL);
    }

    public LinkShort findByLinkLong(String shortLink) {
        log.info("code extraido = {}",shortLink);

        return linkShortRepository.findByShortLink(shortLink);
    }

    public boolean deleteShortLink(String shortLink, Long userId) {
        LinkShort linkShort = linkShortRepository.findByShortLink(shortLink);
        if (linkShort != null && linkShort.getUser().getId().equals(userId)) {
            linkShortRepository.delete(linkShort);
            return true;
        }
        return false;
    }public Page<LinkShortOutDto> listAllShortLinks(Users user, Pageable pageable) {
        Page<Object[]> linkShortPage = linkShortRepository.findAllByUserOrderedByClickCount(user, pageable);
        return linkShortPage.map(obj -> {
            LinkShort linkShort = (LinkShort) obj[0];
            Long clickCount = (Long) obj[1];
            return LinkShortMapper.toResponse(linkShort, clickCount, DOMAIN_URL);
        });
    }
    public int countShortLinks() {
        return (int) linkShortRepository.count();
    }

}
