package fr.climbinggym.adapter.in.web;

import com.jayway.jsonpath.JsonPath;
import fr.climbinggym.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberImportApiIT extends AbstractIntegrationTest {

    @Test
    void imports_valid_lines_and_reports_rejected_ones() throws Exception {
        var run = UUID.randomUUID().toString().substring(0, 8);
        var csv = """
                lastName;firstName;email;birthDate;belayCertified;subscriptionType
                Durand;Léa;lea-%1$s@example.com;1994-03-12;oui;ANNUEL
                Martin;Hugo;hugo-%1$s@example.com;22/07/1988;non;
                Robert;Inès;ines-%1$s@example.com;pas-une-date;non;MENSUEL
                Richard;Nathan;lea-%1$s@example.com;1991-05-05;non;ANNUEL
                Simon;Jules;jules-%1$s;2001-06-23;non;MENSUEL
                Colonnes;Manquantes;oops
                """.formatted(run);
        var file = new MockMultipartFile("file", "members.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/imports/members").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.written").value(2))
                .andExpect(jsonPath("$.data.filtered").value(1))
                .andExpect(jsonPath("$.data.skipped").value(3));

        var json = mockMvc.perform(get("/api/members").param("search", "lea-" + run))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].belayCertified").value(true))
                .andReturn().getResponse().getContentAsString();
        String leaId = JsonPath.read(json, "$.data[0].id");

        mockMvc.perform(get("/api/members/{id}/subscriptions", leaId))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].type").value("ANNUAL"));
    }

    @Test
    void ignores_members_already_in_database() throws Exception {
        var email = uniqueEmail();
        registerMember(email, "1990-05-14", false);
        var csv = "lastName;firstName;email;birthDate;belayCertified;subscriptionType\n"
                + "Sharma;Chris;" + email + ";1990-05-14;non;\n";

        mockMvc.perform(multipart("/api/imports/members")
                        .file(new MockMultipartFile("file", "members.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.written").value(0))
                .andExpect(jsonPath("$.data.filtered").value(1));
    }

    @Test
    void rejects_an_empty_file() throws Exception {
        mockMvc.perform(multipart("/api/imports/members")
                        .file(new MockMultipartFile("file", "members.csv", "text/csv", new byte[0])))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_VALUE"));
    }
}
