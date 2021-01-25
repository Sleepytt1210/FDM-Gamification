package com.team33.FDMGamification;

import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Service.ChallengeService;
import com.team33.FDMGamification.Service.ChoiceService;
import com.team33.FDMGamification.Service.QuestionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration
@ActiveProfiles(value = "test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ChoiceService choiceService;

    @Autowired
    private WebApplicationContext context;

    private Integer chlId = 1;

    private Integer quesId = 1;

    private MockMultipartFile file;

    private String chlUrl(Integer id) {
        return "/scenario/"+id;
    }

    private String quesUrl(Integer id) {
        return chlUrl(chlId)+"/"+id;
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @ParameterizedTest
    @CsvSource({"/,index","/home,index","/explore,explore","/leaderboard,leaderboard"})
    public void testGETUserPages_ShouldSuccess(String url, String view) throws Exception{
        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(view));
    }

    @Test
    public void testGETEmptyScenarioPage_ShouldRedirect() throws Exception{
        this.mockMvc.perform(get("/scenario"))
                .andExpect(status().is3xxRedirection())
                .andDo(print())
                .andExpect(redirectedUrl("/explore"));
    }

    @Test
    public void testGETScenarioPage_ShouldSuccess() throws Exception{
        this.mockMvc.perform(get(chlUrl(chlId)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("scenario"))
                .andExpect(model().attributeExists("questions"));
    }

    @Test
    public void testGETQuestionPage_ShouldSuccess() throws Exception{
        this.mockMvc.perform(get(quesUrl(quesId)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("scenario"))
                .andExpect(model().attributeExists("questions"))
                .andExpect(model().attributeExists("question"))
                .andExpect(model().attributeExists("choices"));
    }

    @Test
    public void testDragAndDropQuestionSubmit_ShouldSuccess() throws Exception{
        Question question = questionService.findById(quesId);
        assertThat(question.getQuestionCompletion()).isEqualTo(0);

        this.mockMvc.perform(
                post(quesUrl(quesId))
                        .param("dragAndDrop", "submit")
                        .param("score1","2")
                        .param("score2", "1")
                        .param("quesInc", "1")
        ).andExpect(status().isOk())
                .andDo(print())
                .andExpect(model().attributeExists("score"))
                .andExpect(model().attribute("score", 2));


        question = questionService.findById(quesId);

        // Expect question completion increment
        assertThat(question.getQuestionCompletion()).isEqualTo(1);
    }

    @Test
    public void testChallengeCompletionIncrement_ShouldSuccess() throws Exception{
        Challenge challenge = challengeService.findById(chlId);
        Question question = questionService.findById(quesId);

        assertThat(challenge.getCompletion()).isEqualTo(0);
        assertThat(question.getQuestionCompletion()).isEqualTo(0);

        this.mockMvc.perform(
                post(quesUrl(quesId))
                        .param("dragAndDrop", "submit")
                        .param("score1","2")
                        .param("score2", "1")
                        .param("quesInc", "1")
                        .param("chalInc", "1")
        ).andExpect(status().isOk())
                .andDo(print())
                .andExpect(model().attributeExists("score"))
                .andExpect(model().attribute("score", 2));

        challenge = challengeService.findById(chlId);
        question = questionService.findById(quesId);

        // Expect question completion increment
        assertThat(challenge.getCompletion()).isEqualTo(1);
        assertThat(question.getQuestionCompletion()).isEqualTo(1);
    }
}
