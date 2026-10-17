package com.umc.pediupatrao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlteracaoCampo {

    private String campo;
    private String valorAnterior;
    private String valorNovo;
}
