package ru.otus.hw.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.otus.hw.configuration.SecurityConfiguration;
import ru.otus.hw.controllers.AuthorController;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.controllers.GenreController;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        BookController.class,
        AuthorController.class,
        GenreController.class,
        CommentController.class
})
@Import(SecurityConfiguration.class)
public class SecurityControllersTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    @DisplayName("Перенаправление на аутентификацию при обращении к защищенным ресурсам без аутентификации")
    @ParameterizedTest(name = "{0} {1} redirect to /login")
    @MethodSource("allProtectedEndpoints")
    void shouldRedirectAnonymousUserToLogin(HttpMethod method, String url) throws Exception {
        mvc.perform(buildRequest(method, url))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @DisplayName("USER должен иметь доступ на чтение")
    @ParameterizedTest(name = "{0} {1} -> {2}")
    @MethodSource("readOnlyEndpoints")
    @WithMockUser(username = "user", roles = "USER")
    void shouldAllowUserToReadResources(HttpMethod method, String url, int expectedStatus) throws Exception {
        mvc.perform(buildRequest(method, url))
                .andExpect(status().is(expectedStatus));
    }

    @DisplayName("USER не должен иметь доступ к редактированию/созданию/удалению книг")
    @ParameterizedTest(name = "{0} {1}")
    @MethodSource("changeDataEndpoints")
    @WithMockUser(username = "user", roles = "USER")
    void shouldDenyUserToWriteBooks(HttpMethod method, String url) throws Exception {
        mvc.perform(buildRequest(method, url))
                .andExpect(status().isForbidden());

        verify(bookService, never()).insert(anyString(), anyLong(), anySet());
        verify(bookService, never()).update(anyLong(), anyString(), anyLong(), anySet());
        verify(bookService, never()).deleteById(anyLong());
    }

    @DisplayName("ADMIN должен иметь доступ ко всем защищённым ресурсам")
    @ParameterizedTest(name = "{0} {1} -> {2}")
    @MethodSource("allProtectedEndpoints")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldAllowAdminToAccessProtectedResources(HttpMethod method, String url, int expectedStatus) throws Exception {
        mvc.perform(buildRequest(method, url))
                .andExpect(status().is(expectedStatus));
    }

    private static Stream<Arguments> readOnlyEndpoints() {
        return Stream.of(
                Arguments.of(HttpMethod.GET, "/", 200),
                Arguments.of(HttpMethod.GET, "/api/author", 200),
                Arguments.of(HttpMethod.GET, "/api/genre", 200),
                Arguments.of(HttpMethod.GET, "/api/book/1/comment", 200),
                Arguments.of(HttpMethod.GET, "/api/book", 200),
                Arguments.of(HttpMethod.GET, "/api/book/1", 200)
        );
    }

    private static Stream<Arguments> changeDataEndpoints() {
        return Stream.of(
                Arguments.of(HttpMethod.POST, "/api/book", 400),
                Arguments.of(HttpMethod.PUT, "/api/book/1", 400),
                Arguments.of(HttpMethod.DELETE, "/api/book/1", 200)
        );
    }

    private static Stream<Arguments> allProtectedEndpoints() {
        return Stream.concat(readOnlyEndpoints(), changeDataEndpoints());
    }

    private static MockHttpServletRequestBuilder buildRequest(HttpMethod method, String url) {
        return request(method, url);
    }
}
