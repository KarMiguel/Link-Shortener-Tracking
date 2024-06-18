package io.github.karMiguel.capzip.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.karMiguel.capzip.model.LinkShort;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClickDTO {

    private Long linkShortId;
    private String userAgent;
    private String ip;
    private String localization;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreated;
}
