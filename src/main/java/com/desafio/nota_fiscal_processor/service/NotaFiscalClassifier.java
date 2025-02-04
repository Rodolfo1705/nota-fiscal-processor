package com.desafio.nota_fiscal_processor.service;

import com.desafio.nota_fiscal_processor.model.NotaFiscalResult;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

public class NotaFiscalClassifier implements Classifier<NotaFiscalResult, ItemWriter<? super NotaFiscalResult>> {

    private final ItemWriter<? super NotaFiscalResult> validWriter;
    private final ItemWriter<? super NotaFiscalResult> invalidWriter;

    public NotaFiscalClassifier(ItemWriter<? super NotaFiscalResult> validWriter,
                                ItemWriter<? super NotaFiscalResult> invalidWriter) {
        this.validWriter = validWriter;
        this.invalidWriter = invalidWriter;
    }

    @Override
    public ItemWriter<? super NotaFiscalResult> classify(NotaFiscalResult processadorNotaFiscal) {
        System.out.println("valido ou invalido");
        return processadorNotaFiscal.isValid() ? validWriter : invalidWriter;
    }
}
