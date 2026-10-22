package fr.climbinggym.adapter.in.web;

import fr.climbinggym.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberApiIT extends AbstractIntegrationTest {

    @Test
    void registers_then_reads_a_member() throws Exception {
        var email = uniqueEmail();
        var id = registerMember(email, "1990-05-14", true);

        mockMvc.perform(get("/api/members/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.belayCertified").value(true));
    }

    @Test
    void rejects_an_email_already_used() throws Exception {
        var email = uniqueEmail();
        registerMember(email, "1990-05-14", false);

        postJson("/api/members", """
                {"firstName":"Adam","lastName":"Ondra","email":"%s","birthDate":"1993-02-05"}
                """.formatted(email.toUpperCase()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("EMAIL_ALREADY_USED"));
    }

    @Test
    void returns_field_errors_on_invalid_body() throws Exception {
        postJson("/api/members", """
                {"firstName":"","lastName":"Ondra","email":"nope","birthDate":"1993-02-05"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.fields.firstName").exists())
                .andExpect(jsonPath("$.error.fields.email").exists());
    }

    @Test
    void patches_only_given_fields() throws Exception {
        var id = registerAdult(false);

        mockMvc.perform(patch("/api/members/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"belayCertified\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.belayCertified").value(true))
                .andExpect(jsonPath("$.data.firstName").value("Chris"));
    }

    @Test
    void searches_members_by_email() throws Exception {
        var email = uniqueEmail();
        registerMember(email, "1990-05-14", false);

        mockMvc.perform(get("/api/members").param("search", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void returns_404_for_unknown_member() throws Exception {
        mockMvc.perform(get("/api/members/{id}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("MEMBER_NOT_FOUND"));
    }
}
