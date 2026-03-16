package com.btg.funds.adapter.in.web;

import com.btg.funds.application.port.out.ClientRepository;
import com.btg.funds.domain.Client;
import com.btg.funds.domain.exception.ClientNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClientController.class)
@Import(com.btg.funds.adapter.in.web.advice.GlobalExceptionHandler.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientRepository clientRepository;

    @Test
    void getClient_ShouldReturnOk() throws Exception {
        Client client = new Client("client-1", "Juan Perez", new BigDecimal("1000"), "juan@test.com");
        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));

        mockMvc.perform(get("/api/v1/clients/{clientId}", "client-1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("client-1"))
                .andExpect(jsonPath("$.name").value("Juan Perez"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    void getClient_ShouldReturnNotFound() throws Exception {
        when(clientRepository.findById("missing-client")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/clients/{clientId}", "missing-client"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("CLIENT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(new ClientNotFoundException("missing-client").getMessage()));
    }
}
