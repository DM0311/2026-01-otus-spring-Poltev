package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private CommentService commentService;

    @DisplayName("Должен вернуть все комментарии к книге")
    @Test
    void shouldReturnAllAuthors() throws Exception {
        List<CommentDto> comments = List.of(new CommentDto(1L,"Comments_1"));
        given(commentService.findAllByBookId(1L)).willReturn(comments);
        mvc.perform(get("/api/book/1/comment"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(comments)))
                .andExpect(jsonPath("$[0].id").value("1"));
    }
}
