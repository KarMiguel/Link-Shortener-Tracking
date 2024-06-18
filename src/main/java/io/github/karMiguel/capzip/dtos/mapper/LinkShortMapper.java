package io.github.karMiguel.capzip.dtos.mapper;

import io.github.karMiguel.capzip.dtos.LinkShortInDto;
import io.github.karMiguel.capzip.dtos.LinkShortOutDto;
import io.github.karMiguel.capzip.model.LinkShort;
import io.github.karMiguel.capzip.repository.ClickRepository;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class LinkShortMapper {

    public static LinkShort toLink(LinkShortInDto dto) {
        return new ModelMapper().map(dto, LinkShort.class);
    }

    public static LinkShortOutDto toResponse(LinkShort linkShort, Long qtdClick, String DOMAIN_URL) {
        ModelMapper modelMapper = new ModelMapper();
        LinkShortOutDto dto = modelMapper.map(linkShort, LinkShortOutDto.class);
        dto.setShortLink(DOMAIN_URL + "/" + linkShort.getShortLink());
        dto.setQtdClick(qtdClick);
        return dto;
    }
    public static List<LinkShortOutDto> toListDto(List<LinkShort> linkShorts, ClickRepository clickRepository,String DOMAIN_URL) {
        return linkShorts.stream().map(link -> {
            Long clickCount = clickRepository.countClicksByShortLink(link.getShortLink());
            return toResponse(link, clickCount,DOMAIN_URL);
        }).collect(Collectors.toList());
    }

}
