package com.example.gestionpropiedades.service;

import com.example.gestionpropiedades.dto.*;
import com.example.gestionpropiedades.entity.enums.EstadoPropiedad;
import com.example.gestionpropiedades.entity.enums.MetodoPago;
import com.example.gestionpropiedades.entity.enums.Rol;
import com.example.gestionpropiedades.exception.ContratoActivoException;
import com.example.gestionpropiedades.exception.PagoDuplicadoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReglasNegocioIntegrationTest {

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private InquilinoService inquilinoService;

    @Autowired
    private ContratoService contratoService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private DashboardService dashboardService;

    private PropiedadResponse crearPropiedad() {
        PropiedadRequest request = new PropiedadRequest(
                "Av. Siempre Viva 742", "Springfield", new BigDecimal("500.00"), 3, 2, "Casa céntrica");
        return propiedadService.create(request);
    }

    private InquilinoResponse crearInquilino() {
        InquilinoRequest request = new InquilinoRequest("Juan", "Perez", "12345678", "juan@mail.com", "555-1234");
        return inquilinoService.create(request);
    }

    private ContratoRequest contratoRequest(Long propiedadId, Long inquilinoId) {
        return new ContratoRequest(propiedadId, inquilinoId,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                new BigDecimal("500.00"));
    }

    @Test
    void alCrearContratoLaPropiedadQuedaOcupada() {
        PropiedadResponse propiedad = crearPropiedad();
        InquilinoResponse inquilino = crearInquilino();

        contratoService.create(contratoRequest(propiedad.id(), inquilino.id()));

        assertThat(propiedadService.findById(propiedad.id()).estado())
                .isEqualTo(EstadoPropiedad.OCUPADA);
    }

    @Test
    void noSePermitenDosContratosActivosParaLaMismaPropiedad() {
        PropiedadResponse propiedad = crearPropiedad();
        InquilinoResponse inquilino1 = crearInquilino();
        InquilinoRequest req2 = new InquilinoRequest("Ana", "Lopez", "87654321", "ana@mail.com", null);
        InquilinoResponse inquilino2Saved = inquilinoService.create(req2);

        contratoService.create(contratoRequest(propiedad.id(), inquilino1.id()));

        assertThatThrownBy(() -> contratoService.create(contratoRequest(propiedad.id(), inquilino2Saved.id())))
                .isInstanceOf(ContratoActivoException.class);
    }

    @Test
    void alFinalizarElContratoLaPropiedadQuedaDisponible() {
        PropiedadResponse propiedad = crearPropiedad();
        InquilinoResponse inquilino = crearInquilino();
        ContratoResponse contrato = contratoService.create(contratoRequest(propiedad.id(), inquilino.id()));

        contratoService.finalizar(contrato.id());

        assertThat(propiedadService.findById(propiedad.id()).estado())
                .isEqualTo(EstadoPropiedad.DISPONIBLE);
    }

    @Test
    void noSePermitenDosPagosParaElMismoPeriodo() {
        PropiedadResponse propiedad = crearPropiedad();
        InquilinoResponse inquilino = crearInquilino();
        ContratoResponse contrato = contratoService.create(contratoRequest(propiedad.id(), inquilino.id()));

        PagoRequest pago = new PagoRequest(contrato.id(), new BigDecimal("500.00"),
                LocalDate.of(2025, 1, 5), "2025-01", MetodoPago.TRANSFERENCIA);
        pagoService.create(pago);

        assertThatThrownBy(() -> pagoService.create(pago))
                .isInstanceOf(PagoDuplicadoException.class);
    }

    @Test
    void elDashboardReflejaIngresosContratosYPagosPendientes() {
        PropiedadResponse propiedad = crearPropiedad();
        InquilinoResponse inquilino = crearInquilino();
        ContratoResponse contrato = contratoService.create(contratoRequest(propiedad.id(), inquilino.id()));
        pagoService.create(new PagoRequest(contrato.id(), new BigDecimal("500.00"),
                LocalDate.of(2025, 1, 5), "2025-01", MetodoPago.TRANSFERENCIA));
        pagoService.create(new PagoRequest(contrato.id(), new BigDecimal("500.00"),
                LocalDate.of(2025, 2, 5), "2025-02", MetodoPago.EFECTIVO));

        DashboardResponse resumen = dashboardService.obtenerResumen();

        assertThat(resumen.contratosActivos()).isEqualTo(1);
        assertThat(resumen.pagosPendientes()).isEqualTo(2);
        assertThat(resumen.ingresosTotales()).isEqualByComparingTo("0");
        assertThat(resumen.montoPendiente()).isEqualByComparingTo("1000.00");
    }
}
