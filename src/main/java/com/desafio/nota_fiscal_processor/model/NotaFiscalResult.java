package com.desafio.nota_fiscal_processor.model;

import java.math.BigDecimal;

public class NotaFiscalResult {
    private NotaFiscal notaFiscal;
    private boolean valid;
    private String errorMessage;

    public NotaFiscalResult(NotaFiscal notaFiscal, boolean valid, String errorMessage) {
        this.notaFiscal = notaFiscal;
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    public Long getId(){
        return notaFiscal.getId();
    }

    public String getCnpj(){
        return notaFiscal.getCnpj();
    }

    public BigDecimal getValor(){
        return notaFiscal.getValor();
    }

    public boolean isValid(){
        return valid;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}
