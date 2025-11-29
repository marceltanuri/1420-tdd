package io.github.marceltanuri.estacionamento.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.marceltanuri.estacionamento.domain.ticket.Ticket;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.TicketRepository;
import io.github.marceltanuri.estacionamento.domain.ticket.service.EmissaoService;
import io.github.marceltanuri.estacionamento.domain.veiculo.Veiculo;
import io.github.marceltanuri.estacionamento.domain.veiculo.Veiculo.TipoVeiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean; // Importação necessária
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content; // Importação adicionada
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmissaoService emissaoService;

    @Autowired
    private TicketRepository ticketRepository;

    // Substitui o bean Clock de produção por um mock controlável
    @MockBean 
    private Clock clock;

    private final String PLACA_CARRO = "ABC1B23";
    private final String PLACA_MOTO = "XYZ9A87";
    
    // Tempo padrão para a maioria dos testes (dentro do horário de funcionamento: 10:00 AM)
    private final LocalDateTime DEFAULT_TIME = LocalDateTime.of(2025, 1, 1, 10, 0, 0);

    /**
     * Helper para configurar o Mock do Clock para um horário específico.
     */
    private void setupMockClock(LocalDateTime dateTime) {
        Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    @BeforeEach
    void setup() {
       // Configura o Clock padrão para o início de cada teste
       setupMockClock(DEFAULT_TIME);
       //ticketRepository.deleteAll();
    }

    // --- Configurações de teste originais (usando o DEFAULT_TIME) ---

    @Test
    @DisplayName("Deve emitir um ticket com sucesso e persistir a informação (Carro)")
    void shouldEmitTicketSuccessfully() throws Exception {
        Veiculo veiculoInput = new Veiculo(PLACA_CARRO, TipoVeiculo.CARRO);

        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.veiculo.placa").value(PLACA_CARRO))
                .andExpect(jsonPath("$.entrada").exists())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Deve retornar o ticket existente se tentar reemitir para o mesmo veiculo (Regra de Negócio)")
    void shouldReturnExistingTicketIfReemissionAttempted() throws Exception {
        Veiculo veiculoInput = new Veiculo("DEF4C56", TipoVeiculo.CARRO);
        emissaoService.emitir(veiculoInput);

        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.veiculo.placa").value("DEF4C56"))
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.entrada").exists());
    }

    @Test
    @DisplayName("Deve emitir ticket com sucesso para um veículo tipo MOTO")
    void shouldEmitTicketSuccessfullyForMotorcycle() throws Exception {
        Veiculo veiculoInput = new Veiculo(PLACA_MOTO, TipoVeiculo.MOTO);

        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.veiculo.placa").value(PLACA_MOTO))
                .andExpect(jsonPath("$.veiculo.tipo").value("MOTO"))
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    @DisplayName("Deve buscar um ticket pelo ID")
    void shouldGetTicketById() throws Exception {
        Veiculo veiculo = new Veiculo("GHI7D89", TipoVeiculo.CARRO);
        Ticket ticket = emissaoService.emitir(veiculo);

        mockMvc.perform(get("/tickets/" + ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticket.getId()))
                .andExpect(jsonPath("$.veiculo.placa").value(veiculo.getPlaca()));
    }

    @Test
    @DisplayName("Deve pagar um ticket")
    void shouldPayTicket() throws Exception {
        Veiculo veiculo = new Veiculo("JKL0E12", TipoVeiculo.CARRO);
        Ticket ticket = emissaoService.emitir(veiculo);

        mockMvc.perform(put("/tickets/" + ticket.getId() + "/pay"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve processar a saída de um veículo com ticket pago")
    void shouldProcessExitWithPaidTicket() throws Exception {
        Veiculo veiculo = new Veiculo("MNO3F45", TipoVeiculo.CARRO);
        Ticket ticket = emissaoService.emitir(veiculo);

        // Simulate payment
        mockMvc.perform(put("/tickets/" + ticket.getId() + "/pay"));

        // A hora de saída usa o DEFAULT_TIME (10:00 AM), que está dentro do expediente
        mockMvc.perform(put("/tickets/" + ticket.getId() + "/exit"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve isentar um ticket por comprovante de compra")
    void shouldExemptTicketByReceipt() throws Exception {
        Veiculo veiculo = new Veiculo("PQR6G78", TipoVeiculo.CARRO);
        Ticket ticket = emissaoService.emitir(veiculo);

        mockMvc.perform(put("/tickets/" + ticket.getId() + "/exempt-by-receipt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("COMPROVANTE_VALIDO"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve isentar um ticket de funcionário")
    void shouldExemptEmployeeTicket() throws Exception {
        Veiculo veiculo = new Veiculo("GJK8D74", TipoVeiculo.CARRO);
        Ticket ticket = emissaoService.emitir(veiculo);

        mockMvc.perform(put("/tickets/" + ticket.getId() + "/exempt-employee"))
                .andExpect(status().isOk());
    }

    // --- Novos Cenários de Teste para Horário de Funcionamento ---
    
    @Test
    @DisplayName("Deve falhar ao processar saída fora do horário de funcionamento (depois de 22:00)")
    void shouldFailExitOutsideOperatingHours() throws Exception {
        // GIVEN: Ticket criado e pago
        Veiculo veiculo = new Veiculo("NNO4G56", TipoVeiculo.CARRO);
        Ticket ticket = emissaoService.emitir(veiculo);

        // O pagamento usa a hora padrão (10:00)
        mockMvc.perform(put("/tickets/" + ticket.getId() + "/pay"));

        // WHEN: Configura o Clock para fora do horário (ex: 00:30 do dia seguinte)
        LocalDateTime outsideTime = LocalDateTime.of(2025, 1, 2, 0, 30, 0);
        setupMockClock(outsideTime);

        // A mensagem esperada
        String expectedMessage = "Ticket não pode ser finalizado fora do horário de funcionamento.";

        // THEN: Espera-se erro 400 Bad Request devido à IllegalStateException.
        // CORREÇÃO: Usamos content().string() para esperar texto puro (e não um objeto JSON).
        mockMvc.perform(put("/tickets/" + ticket.getId() + "/exit"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMessage)); 
    }
    
    @Test
    @DisplayName("Deve permitir saída de ticket ISENTO_FUNCIONARIO fora do horário (ex: 00:30)")
    void shouldAllowEmployeeExitOutsideOperatingHours() throws Exception {
        // GIVEN: Ticket criado para funcionário e isento (placa GJK8D74 é usada no teste shouldExemptEmployeeTicket)
        Veiculo veiculo = new Veiculo("GJK8D74", TipoVeiculo.CARRO); 
        Ticket ticket = emissaoService.emitir(veiculo);
        
        // Isenta o ticket (status = ISENTO_FUNCIONARIO)
        mockMvc.perform(put("/tickets/" + ticket.getId() + "/exempt-employee"))
                .andExpect(status().isOk());

        // WHEN: Configura o Clock para fora do horário (ex: 00:30 do dia seguinte)
        LocalDateTime outsideTime = LocalDateTime.of(2025, 1, 2, 0, 30, 0);
        setupMockClock(outsideTime);

        // THEN: Espera-se sucesso (o SaidaService permite ISENTO_FUNCIONARIO a qualquer hora)
        mockMvc.perform(put("/tickets/" + ticket.getId() + "/exit"))
                .andExpect(status().isOk());
    }

}