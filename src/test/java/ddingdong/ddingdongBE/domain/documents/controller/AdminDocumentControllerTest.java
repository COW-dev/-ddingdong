package ddingdong.ddingdongBE.domain.documents.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ddingdong.ddingdongBE.domain.documents.controller.dto.request.GenerateDocumentRequest;
import ddingdong.ddingdongBE.domain.documents.entity.Document;
import ddingdong.ddingdongBE.support.WebAdaptorTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(AdminDocumentController.class)
public class AdminDocumentControllerTest extends WebAdaptorTestSupport {

    @WithMockUser(roles = "ADMIN")
    @DisplayName("document 자료 생성 요청을 수행한다.")
    @Test
    void generate() throws Exception {
        // given
        GenerateDocumentRequest request = GenerateDocumentRequest.builder()
                .title("testTitle")
                .content("testContent").build();
        MockMultipartFile file = new MockMultipartFile("uploadFiles", "test.txt", "text/plain",
                "test content".getBytes());
        when(documentService.create(any()))
                .thenReturn(1L);

        // when // then
        mockMvc.perform(multipart("/server/admin/documents")
                        .file(file)
                        .param("title", request.getTitle())
                        .param("content", request.getContent())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(fileService).uploadDownloadableFile(anyLong(), anyList(), any(), any());
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("documents 조회 요청을 수행한다.")
    @Test
    void getAll() throws Exception {
        //given
        List<Document> foundDocuments = List.of(Document.builder().id(1L).title("A").build(),
                Document.builder().id(2L).title("B").build());
        when(documentService.findAll()).thenReturn(foundDocuments);

        //when //then
        mockMvc.perform(get("/server/admin/documents")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(foundDocuments.size())))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("A"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("B"));
    }
}
