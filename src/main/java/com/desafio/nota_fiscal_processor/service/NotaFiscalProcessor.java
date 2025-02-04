package com.desafio.nota_fiscal_processor.service;

import com.desafio.nota_fiscal_processor.model.NotaFiscal;
import com.desafio.nota_fiscal_processor.model.NotaFiscalResult;
import org.springframework.batch.item.ItemProcessor;
import java.math.BigDecimal;
import java.util.List;

public class NotaFiscalProcessor implements ItemProcessor<String, NotaFiscalResult> {

    @Override
    public NotaFiscalResult process(String item) throws Exception {
        List<String> data = List.of(item.split(","));

        if (data.size() != 3) {
            return returnInvalidNotaFiscalResult(item, "Quantidade de campos inválida.");
        }

        data = data.stream().map(String::trim).toList();

        long id;
        String cnpj = data.get(1);
        BigDecimal valor;

        try {
            id = Long.parseLong(data.get(0));
        } catch (NumberFormatException e) {
            return returnInvalidNotaFiscalResult(item, "ID inválido.");
        }

        try {
            valor = new BigDecimal(data.get(2));
        } catch (NumberFormatException e) {
            return returnInvalidNotaFiscalResult(item, "Valor inválido.");
        }

        if (cnpj == null || !cnpj.matches("\\d{14}")) {
            return returnInvalidNotaFiscalResult(item, "CNPJ inválido.");
        }

        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            return returnInvalidNotaFiscalResult(item, "O valor deve ser igual ou maior que zero.");
        }

        NotaFiscal notaFiscal = new NotaFiscal(id, cnpj, valor);
        return new NotaFiscalResult(notaFiscal, true, "");
    }

    private NotaFiscalResult returnInvalidNotaFiscalResult(String extraFields, String errorMessage){
        NotaFiscal notaFiscal = new NotaFiscal();
        notaFiscal.setExtraFields(extraFields);

        return new NotaFiscalResult(notaFiscal, false, errorMessage);
    }
}