package com.leocine.service;

import com.leocine.dto.NotificationRequestDTO;
import com.leocine.dto.NotificationResponseDTO;
import com.leocine.entity.NotificationMessage;
import com.leocine.exception.NotificationProcessingException;
import com.leocine.repository.NotificationJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationJpaRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private NotificationRequestDTO criarRequest() {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setUserID("user-1");
        request.setUserEmail("teste@email.com");
        request.setMsgString("Pagamento aprovado!");
        request.setDateTime(OffsetDateTime.now());
        return request;
    }

    @Test
    void deveCriarNotificacao() {
        NotificationRequestDTO request = criarRequest();
        when(notificationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponseDTO response = notificationService.createNotification(request);

        assertNotNull(response.getId());
        assertEquals("user-1", response.getUserID());
        assertEquals("teste@email.com", response.getUserEmail());
        verify(notificationRepository).save(any());
    }

    @Test
    void deveBuscarNotificacaoPorId() {
        NotificationMessage entity = new NotificationMessage();
        entity.setId("abc-123");
        entity.setUserID("user-1");
        entity.setUserEmail("teste@email.com");
        entity.setMsgString("Mensagem teste");
        entity.setDateTime(OffsetDateTime.now());

        when(notificationRepository.findById("abc-123")).thenReturn(Optional.of(entity));

        NotificationResponseDTO response = notificationService.getNotificationById("abc-123");

        assertEquals("abc-123", response.getId());
        assertEquals("user-1", response.getUserID());
    }

    @Test
    void deveLancarExcecaoQuandoNotificacaoNaoEncontrada() {
        when(notificationRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(NotificationProcessingException.class,
                () -> notificationService.getNotificationById("inexistente"));
    }

    @Test
    void deveLancarExcecaoQuandoIdNuloNoBuscar() {
        assertThrows(NotificationProcessingException.class,
                () -> notificationService.getNotificationById(null));
    }

    @Test
    void deveEnviarEmailComSucesso() {
        NotificationMessage entity = new NotificationMessage();
        entity.setId("abc-123");
        entity.setUserID("user-1");
        entity.setUserEmail("teste@email.com");
        entity.setMsgString("Mensagem");
        entity.setDateTime(OffsetDateTime.now());
        entity.setSentAt(null);

        when(notificationRepository.findById("abc-123")).thenReturn(Optional.of(entity));
        when(notificationRepository.save(any())).thenReturn(entity);

        notificationService.sendEmailById("abc-123");

        verify(notificationRepository).save(entity);
        assertNotNull(entity.getSentAt());
    }

    @Test
    void deveLancarExcecaoAoEnviarEmailComIdNulo() {
        assertThrows(NotificationProcessingException.class,
                () -> notificationService.sendEmailById(null));
    }
}
