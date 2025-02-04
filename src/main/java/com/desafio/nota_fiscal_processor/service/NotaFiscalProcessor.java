package com.desafio.nota_fiscal_processor.service;

import com.desafio.nota_fiscal_processor.model.NotaFiscal;
import com.desafio.nota_fiscal_processor.model.NotaFiscalResult;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;

public class NotaFiscalProcessor implements ItemProcessor<NotaFiscal, NotaFiscalResult> {

    @Override
    public NotaFiscalResult process(NotaFiscal notaFiscal) {
        System.out.println("vendo os erro");
        String errors = "";
        boolean valid = true;

        if(notaFiscal.getExtraFields() != null && !notaFiscal.getExtraFields().isEmpty()){
            errors += "Campos extras detectados";
            valid = false;
        }

        if(!notaFiscal.getCnpj().matches("\\d{14}")){
            errors += "CNPJ inválido; ";
            valid = false;
        }

        if(notaFiscal.getValor().compareTo(BigDecimal.ZERO) <= 0){
            errors += "Valor invalido; ";
            valid = false;
        }

        System.out.println(valid);

        return new NotaFiscalResult(notaFiscal, valid, errors.trim());
    }
}
