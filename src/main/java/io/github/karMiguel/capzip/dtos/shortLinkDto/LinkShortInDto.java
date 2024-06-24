package io.github.karMiguel.capzip.dtos.shortLinkDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinkShortInDto {

    @NotBlank(message = "O link não pode estar em branco.")
    @Pattern(regexp = "^(https?|ftp):\\/\\/[^\\s/$.?#].[^\\s]*$", message = "Link inválido, não está em formato web.")
    private String linkLong;

    private String shortLink;
}
