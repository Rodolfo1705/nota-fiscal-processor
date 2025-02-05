package com.desafio.nota_fiscal_processor.service;

import com.desafio.nota_fiscal_processor.model.NotaFiscal;
import com.desafio.nota_fiscal_processor.model.NotaFiscalResult;
import com.desafio.nota_fiscal_processor.utils.ErrorConstants;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class NotaFiscalProcessorTest {
    @InjectMocks
    private NotaFiscalProcessor notaFiscalProcessor;

    private String validItem;
    private String invalidIdItem;
    private String invalidValorItem;
    private String invalidCnpjItem;
    private String invalidExtraFieldsItem;

    private NotaFiscalResult validNotaFiscalResult;

    @BeforeEach
    public void setUp() {
        validItem = "1,12345678000195,1000";
        invalidIdItem = "-1, 12345678000195,1000";
        invalidCnpjItem = "2,invalidCNPJ,1000";
        invalidValorItem = "3,12345678000195,-500";
        invalidExtraFieldsItem = "4,invalidCNPJ,3000 5, 12345678000195,0";

        validNotaFiscalResult = montarValidNotaFiscalResult();
    }

    @Test
    @Order(1)
    @DisplayName("Validar se a nota fiscal é processada com sucesso.")
    public void processSuccess() throws Exception {
        var result = notaFiscalProcessor.process(validItem);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getNotaFiscal().getId(), validNotaFiscalResult.getNotaFiscal().getId());
        Assertions.assertEquals(result.getNotaFiscal().getCnpj(), validNotaFiscalResult.getNotaFiscal().getCnpj());
        Assertions.assertEquals(result.getNotaFiscal().getValor(), validNotaFiscalResult.getNotaFiscal().getValor());
        Assertions.assertEquals(result.isValid(), validNotaFiscalResult.isValid());
        Assertions.assertEquals(result.getErrorMessage(), validNotaFiscalResult.getErrorMessage());
    }

    @Test
    @Order(2)
    @DisplayName("Verificar erro de Id.")
    public void processIdError() throws Exception {
        var retorno = notaFiscalProcessor.process(invalidIdItem);

        NotaFiscal notaFiscal = montarNotaFiscal(-1L, "12345678000195", BigDecimal.valueOf(1000));
        new NotaFiscalResult(notaFiscal, false, ErrorConstants.INVALID_ID);

        Assertions.assertNotNull(retorno);
        Assertions.assertFalse(retorno.isValid());
        Assertions.assertEquals(retorno.getErrorMessage(), ErrorConstants.INVALID_ID);
    }

    @Test
    @Order(3)
    @DisplayName("Verificar erro de CNPJ.")
    public void processCnpjError() throws Exception {
        var retorno = notaFiscalProcessor.process(invalidCnpjItem);

        NotaFiscal notaFiscal = montarNotaFiscal(2L, "invalidCNPJ", BigDecimal.valueOf(1000));
        new NotaFiscalResult(notaFiscal, false, ErrorConstants.INVALID_CNPJ);

        Assertions.assertNotNull(retorno);
        Assertions.assertFalse(retorno.isValid());
        Assertions.assertEquals(retorno.getErrorMessage(), ErrorConstants.INVALID_CNPJ);
    }

    @Test
    @Order(4)
    @DisplayName("Verificar erro de Valor.")
    public void processValorError() throws Exception {
        var retorno = notaFiscalProcessor.process(invalidValorItem);

        NotaFiscal notaFiscal = montarNotaFiscal(3L, "12345678000195", BigDecimal.valueOf(-500));
        new NotaFiscalResult(notaFiscal, false, ErrorConstants.SHOULD_BE_POSITIVE_VALOR);

        Assertions.assertNotNull(retorno);
        Assertions.assertFalse(retorno.isValid());
        Assertions.assertEquals(retorno.getErrorMessage(), ErrorConstants.SHOULD_BE_POSITIVE_VALOR);
    }

    @Test
    @Order(5)
    @DisplayName("Verificar erro de Campos extra.")
    public void processExtraFieldsError() throws Exception {
        var retorno = notaFiscalProcessor.process(invalidExtraFieldsItem);

        NotaFiscal notaFiscal = montarNotaFiscal(4L, "12345678000195", BigDecimal.valueOf(1000));
        new NotaFiscalResult(notaFiscal, false, ErrorConstants.INVALID_FIELDS_QUANTITY);

        Assertions.assertNotNull(retorno);
        Assertions.assertFalse(retorno.isValid());
        Assertions.assertEquals(retorno.getErrorMessage(), ErrorConstants.INVALID_FIELDS_QUANTITY);
    }

    private NotaFiscal montarNotaFiscal(Long id, String cnpj, BigDecimal valor){
        return new NotaFiscal(id, cnpj, valor);
    }

    private NotaFiscalResult montarValidNotaFiscalResult() {
        return new NotaFiscalResult(
                new NotaFiscal(1L, "12345678000195", BigDecimal.valueOf(1000)),
                true,
                ""
        );
    }
}
