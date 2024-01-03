package com.is4tech.base.util;

import com.is4tech.base.dto.ErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.is4tech.base.util.Utilities.formatoFechaSimple;
import static com.is4tech.base.util.Utilities.getError;

class UtilitiesTest {

    @Test
    void testLogs() {
        var req = Mockito.mock(HttpServletRequest.class);
        Utilities.debugLog("Debug Log");
        Utilities.infoLog(req, HttpStatus.OK, "Test" );
        Utilities.errorLog(req, HttpStatus.BAD_REQUEST, "Error en request", new Exception("Dummy Exception"));
        Assertions.assertTrue(true);
    }

    @Test
    void testFormatoFechaSimple() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        String s = formatoFechaSimple(calendar.getTime());
        Assertions.assertEquals("01/01/2023", s);
    }

    @Test
    void testApiErrors() {
        ErrorDTO ok = getError(404, "Tratando de acceder a recurso que no existe");
        Assertions.assertEquals(404, ok.getError().getErrorCode());
        Assertions.assertEquals("Tratando de acceder a recurso que no existe", ok.getError().getDescription());

        ErrorDTO unk = getError(Integer.MIN_VALUE, "NPE");
        Assertions.assertEquals(500, unk.getError().getErrorCode());
        Assertions.assertEquals("NPE", unk.getError().getDescription());

    }

}
