package de.chrdw.mensa_siemens.parser;

import java.time.LocalDate;

import lombok.Data;

public @Data class MenuKey {
    private final int mensaId;
    private final LocalDate day;
}