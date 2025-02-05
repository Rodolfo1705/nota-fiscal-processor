package com.desafio.nota_fiscal_processor.model;


public class NotaFiscalResult {
    private NotaFiscal notaFiscal;
    private boolean valid;
    private String errorMessage;

    public NotaFiscalResult(NotaFiscal notaFiscal, boolean valid, String errorMessage) {
        this.notaFiscal = notaFiscal;
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    public NotaFiscal getNotaFiscal() {
        return notaFiscal;
    }

    public boolean isValid(){
        return valid;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}
