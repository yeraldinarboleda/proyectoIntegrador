package proymodpredictivoia.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import proymodpredictivoia.demo.controller.AuthUserController;
import proymodpredictivoia.demo.model.AuthUser;
import proymodpredictivoia.demo.repository.AuthUserRepository;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthUserController.class)
public class AuthUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthUserRepository authUserRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthUser getSampleUser() {
        AuthUser user = new AuthUser();
        user.setId(1L);
        user.setDocumentId("12345678");
        user.setFullName("Juan Pérez");
        user.setRole("MEDICO");
        user.setHashedPassword("clave123");
        return user;
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        AuthUser user = getSampleUser();

        when(authUserRepository.existsByDocumentId(user.getDocumentId())).thenReturn(false);
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario registrado exitosamente."));
    }

    @Test
    public void testRegisterUserAlreadyExists() throws Exception {
        AuthUser user = getSampleUser();

        when(authUserRepository.existsByDocumentId(user.getDocumentId())).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El usuario ya existe con ese documentId."));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        AuthUser user = getSampleUser();
        Map<String, String> request = Map.of(
                "documentId", "12345678",
                "password", "clave123"
        );

        when(authUserRepository.findByDocumentId("12345678")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login exitoso"));
    }

    @Test
    public void testLoginUserNotFound() throws Exception {
        Map<String, String> request = Map.of(
                "documentId", "99999999",
                "password", "clave123"
        );

        when(authUserRepository.findByDocumentId("99999999")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Usuario no encontrado."));
    }

    @Test
    public void testLoginIncorrectPassword() throws Exception {
        AuthUser user = getSampleUser();
        Map<String, String> request = Map.of(
                "documentId", "12345678",
                "password", "claveIncorrecta"
        );

        when(authUserRepository.findByDocumentId("12345678")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Contraseña incorrecta."));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<AuthUser> users = List.of(getSampleUser());
        when(authUserRepository.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/auth/all"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    public void testGetUserByIdFound() throws Exception {
        AuthUser user = getSampleUser();
        when(authUserRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/auth/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void testGetUserByIdNotFound() throws Exception {
        when(authUserRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/auth/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateUserSuccess() throws Exception {
        AuthUser existing = getSampleUser();
        AuthUser updated = getSampleUser();
        updated.setFullName("Nuevo Nombre");

        when(authUserRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(updated);

        mockMvc.perform(put("/api/auth/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario actualizado exitosamente."));
    }

    @Test
    public void testUpdateUserNotFound() throws Exception {
        AuthUser updated = getSampleUser();

        when(authUserRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/auth/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUserSuccess() throws Exception {
        when(authUserRepository.existsById(1L)).thenReturn(true);
        doNothing().when(authUserRepository).deleteById(1L);

        mockMvc.perform(delete("/api/auth/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario eliminado exitosamente."));
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {
        when(authUserRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/auth/99"))
                .andExpect(status().isNotFound());
    }
}
