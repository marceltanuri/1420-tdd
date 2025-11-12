package com.ada.t1420.supermercado.domain;

import java.util.List;


public interface PromocaoRepository {

    
    
    List<ConfiguracaoDesconto> buscarRegrasAtivas();

}