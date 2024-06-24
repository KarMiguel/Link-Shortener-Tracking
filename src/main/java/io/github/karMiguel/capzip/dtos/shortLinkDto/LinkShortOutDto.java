package io.github.karMiguel.capzip.dtos.shortLinkDto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinkShortOutDto {

    private String linkLong;

    private String shortLink;

    private Long qtdClick;

}
