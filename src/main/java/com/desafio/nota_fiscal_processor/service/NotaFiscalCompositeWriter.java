package com.desafio.nota_fiscal_processor.service;

import com.desafio.nota_fiscal_processor.model.NotaFiscalResult;
import org.springframework.batch.item.*;


public class NotaFiscalCompositeWriter implements ItemStreamWriter<NotaFiscalResult> {

    private final ItemWriter<NotaFiscalResult> validWriter;
    private final ItemWriter<NotaFiscalResult> invalidWriter;
    private final NotaFiscalClassifier classifier;

    public NotaFiscalCompositeWriter(ItemWriter<NotaFiscalResult> validWriter,
                                     ItemWriter<NotaFiscalResult> invalidWriter,
                                     NotaFiscalClassifier classifier) {
        this.validWriter = validWriter;
        this.invalidWriter = invalidWriter;
        this.classifier = classifier;
    }

    @Override
    public void open(ExecutionContext executionContext) {
        if(validWriter instanceof ItemStream){
            ((ItemStream) validWriter).open(executionContext);
        }
        if(invalidWriter instanceof ItemStream){
            ((ItemStream) invalidWriter).open(executionContext);
        }
    }

    @Override
    public void write(Chunk<? extends NotaFiscalResult> notaFiscalResults) throws Exception {
        ItemWriter<? super NotaFiscalResult> writer = classifier.classify(notaFiscalResults.getItems().get(0));
        writer.write(notaFiscalResults);
    }

    @Override
    public void update(ExecutionContext executionContext) {
        if(validWriter instanceof ItemStream){
            ((ItemStream) validWriter).update(executionContext);
        }
        if(invalidWriter instanceof ItemStream){
            ((ItemStream) invalidWriter).update(executionContext);
        }
    }

    @Override
    public void close() {
        if(validWriter instanceof ItemStream){
            ((ItemStream) validWriter).close();
        }
        if(invalidWriter instanceof ItemStream){
            ((ItemStream) invalidWriter).close();
        }
    }
}
