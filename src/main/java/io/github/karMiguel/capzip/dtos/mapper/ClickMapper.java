package io.github.karMiguel.capzip.dtos.mapper;

import io.github.karMiguel.capzip.dtos.clickDto.ClickDTO;
import io.github.karMiguel.capzip.model.click.Click;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class ClickMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
    }

    public static Click toClick(ClickDTO dto) {
        return modelMapper.map(dto, Click.class);
    }

    public static ClickDTO toResponse(Click click) {
        return modelMapper.map(click, ClickDTO.class);
    }

    public static List<ClickDTO> toListDto(List<Click> clicks) {
        return clicks.stream().map(ClickMapper::toResponse).collect(Collectors.toList());
    }

    public static Page<ClickDTO> toPageDto(Page<Click> clicks) {
        return clicks.map(ClickMapper::toResponse);
    }
}
