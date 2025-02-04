package com.desafio.nota_fiscal_processor.mapper;

import com.desafio.nota_fiscal_processor.model.NotaFiscal;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.List;

public class NotaFiscalMapper implements FieldSetMapper<NotaFiscal> {

    @Override
    public NotaFiscal mapFieldSet(FieldSet fieldSet) throws BindException {
        NotaFiscal notaFiscal = new NotaFiscal();
        int fieldCounter = fieldSet.getFieldCount();

      /*  if(fieldCounter > 3){
            List<String> fields = new ArrayList<>();

            for (int i = 1; i < fieldCounter; i++) {
                fields.add(fieldSet.readString(i));
            }

            notaFiscal.setId(fieldSet.readLong("id"));
            notaFiscal.setCamposExtras(String.join("|", fields));

            return notaFiscal;
        } */
        notaFiscal.setId(fieldSet.readLong("id"));
        notaFiscal.setCnpj(fieldSet.readString("cnpj"));
        notaFiscal.setValor(fieldSet.readBigDecimal("valor"));

        return notaFiscal;
    }
}
