package com.desafio.nota_fiscal_processor.model;

import java.math.BigDecimal;

public class NotaFiscal {
    private Long id;
    private String cnpj;
    private BigDecimal valor;
    private String extraFields;

    public NotaFiscal() {}

    public NotaFiscal(Long id, String cnpj, BigDecimal valor) {
        this.id = id;
        this.cnpj = cnpj;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getExtraFields() {
        return extraFields;
    }

    public void setCamposExtras(String extraFields) {
        this.extraFields = extraFields;
    }
}
