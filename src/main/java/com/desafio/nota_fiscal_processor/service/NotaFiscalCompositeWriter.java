package com.desafio.nota_fiscal_processor.service;

import com.desafio.nota_fiscal_processor.model.NotaFiscalResult;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;

import java.util.ArrayList;
import java.util.List;

public class NotaFiscalCompositeWriter implements ItemStreamWriter<NotaFiscalResult> {

    private final FlatFileItemWriter<String> validWriter;
    private final FlatFileItemWriter<String> invalidWriter;

    public NotaFiscalCompositeWriter(FlatFileItemWriter<String> validWriter,
                                     FlatFileItemWriter<String> invalidWriter) {
        this.validWriter = validWriter;
        this.invalidWriter = invalidWriter;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (validWriter != null) {
            ((ItemStream) validWriter).open(executionContext);
        }

        if (invalidWriter != null) {
            ((ItemStream) invalidWriter).open(executionContext);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        if (validWriter != null) {
            ((ItemStream) validWriter).update(executionContext);
        }

        if (invalidWriter != null) {
            ((ItemStream) invalidWriter).update(executionContext);
        }
    }

    @Override
    public void close() throws ItemStreamException {
        if (validWriter != null) {
            ((ItemStream) validWriter).close();
        }

        if (invalidWriter != null) {
            ((ItemStream) invalidWriter).close();
        }
    }

    @Override
    public void write(Chunk<? extends NotaFiscalResult> chunk) throws Exception {
        List<String> validLines = new ArrayList<>();
        List<String> invalidLines = new ArrayList<>();

        for (NotaFiscalResult result : chunk.getItems()) {
            if (result.isValid()) {
                String line = String.format("%s,%s,%s",
                        result.getNotaFiscal().getId(),
                        result.getNotaFiscal().getCnpj(),
                        result.getNotaFiscal().getValor()
                );

                validLines.add(line);
            }
            else {
                String line = result.getNotaFiscal().getExtraFields();

                if (line == null || line.trim().isEmpty()) {
                    line = String.format("%s,%s,%s,%s",
                            result.getNotaFiscal().getId(),
                            result.getNotaFiscal().getCnpj(),
                            result.getNotaFiscal().getValor(),
                            result.getErrorMessage()
                    );
                }

                invalidLines.add(line);
            }
        }

        if (!validLines.isEmpty()) {
            validWriter.write(new Chunk<>(validLines));
        }

        if (!invalidLines.isEmpty()) {
            invalidWriter.write(new Chunk<>(invalidLines));
        }
    }
}