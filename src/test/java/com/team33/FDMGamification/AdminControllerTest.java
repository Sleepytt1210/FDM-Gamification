package com.team33.FDMGamification;

import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Choice;
import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Service.ChallengeService;
import com.team33.FDMGamification.Service.ChoiceService;
import com.team33.FDMGamification.Service.QuestionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@SpringBootTest
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration
@ActiveProfiles(value = "test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
public class AdminControllerTest {

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

    private Integer choId = 1;

    private MockMultipartFile file;

    private String chlFormUrl(Integer id) {
        return "/admin/challenges/"+id;
    }

    private String quesFormUrl(Integer id) {
        return "/admin/questions/"+id;
    }

    private String choFormUrl(Integer id) {
        return "/admin/choices/"+id;
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(sharedHttpSession())
                .build();
        file = new MockMultipartFile(
                "pic",
                "shark.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                Base64Utils.decodeFromString(challengeService.findById(chlId).getThumbnail().getBase64String())
        );
    }

    @Test
    public void testGETAdminHomeURI_RedirectsChallengePage() throws Exception{
        this.mockMvc.perform(get("/admin/")).andDo(print()).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/challenges"));
    }

    @ParameterizedTest
    @CsvSource({"challenges,challenges","questions,questions","choices,choices"})
    @Transactional
    public void testGETAdminHomePages(String page, String expected) throws Exception{
        ResultActions result = mockMvc.perform(get("/admin/"+page)).andExpect(status().isOk());
        MvcResult mvcResult = result.andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        assertThat(mav.getModelMap()).isNotNull();
        assertThat(mav.getModelMap().containsAttribute(expected));
    }

    @Test
    @Transactional
    public void testGETAdminChallengeFormPage() throws Exception{
        ResultActions result = mockMvc.perform(get(chlFormUrl(chlId)))
                .andExpect(status().isOk()).andDo(print());
        MvcResult mvcResult = result.andReturn();
    }

    @Test
    @Transactional
    public void testGETAdminQuestionFormPage() throws Exception{
        ResultActions result = mockMvc.perform(get(quesFormUrl(quesId)))
                .andExpect(status().isOk()).andDo(print());
        MvcResult mvcResult = result.andReturn();
    }

    @Test
    @Transactional
    public void testGETAdminChoiceFormPage() throws Exception{
        ResultActions result = mockMvc.perform(get(chlFormUrl(choId)))
                .andExpect(status().isOk()).andDo(print());
        MvcResult mvcResult = result.andReturn();
    }

    // Use multipart
    @Test
    @Transactional
    public void testGET_POSTAdminChallengeFormPage() throws Exception{
        String url = chlFormUrl(chlId);
        mockMvc.perform(get(url))
                .andDo(result ->
                    mockMvc.perform(
                            multipart(url)
                                    .file(file)
                                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                    .param("save", "save")
                                    .characterEncoding("utf-8")
                    ).andDo(print()).andExpect(status().is3xxRedirection())
        );
    }

    @Test
    @Transactional
    public void testGET_POSTAdminQuestionFormPage() throws Exception{
        String url = quesFormUrl(quesId);
        mockMvc.perform(get(url))
                .andDo(result -> mockMvc.perform(
                        post(url)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("save", "save")
                                .param("challenge.id", chlId.toString())
                        ).andDo(print()).andExpect(status().is3xxRedirection())
                );
    }

    @Test
    @Transactional
    public void testGET_POSTAdminChoiceFormPage() throws Exception{
        String url = choFormUrl(choId);
        mockMvc.perform(get(url))
                .andDo(result -> mockMvc.perform(
                        post(url)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("save", "save")
                                .param("question.questionId", quesId.toString())
                        ).andDo(print()).andExpect(status().is3xxRedirection())
                );
    }

    @Test
    @Transactional
    public void testPostInvalidChallengeFormData_thenVerifyErrorResponse() throws Exception{
        String url = chlFormUrl(chlId);
        mockMvc.perform(get(url)).andDo(
                result -> mockMvc.perform(
                        multipart(url)
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("save", "save")
                                .param("challengeTitle", "")
                                .param("description", "")
                )
                        .andDo(print())
                        .andExpect(model().attributeHasFieldErrorCode(
                                "challenge","challengeTitle","NotBlank"))
                        .andExpect(model().attributeHasFieldErrorCode(
                                "challenge", "description", "NotBlank"))
                        .andExpect(view().name("admin/challengeForm"))
                        .andExpect(status().isOk())
        );
    }

    @Test
    @Transactional
    public void testPostInvalidQuestionFormData_thenVerifyErrorResponse() throws Exception{
        String url = quesFormUrl(quesId);
        mockMvc.perform(get(url)).andDo(
                result -> mockMvc.perform(
                        post(url)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("save", "save")
                                .param("questionTitle", "")
                                .param("questionText", "")
                                .param("challenge.id", chlId.toString())
                )
                        .andDo(print())
                        .andExpect(model().attributeHasFieldErrorCode(
                                "question","questionTitle","NotBlank"))
                        .andExpect(model().attributeHasFieldErrorCode(
                                "question", "questionText", "NotBlank"))
                        .andExpect(view().name("admin/questionForm"))
                        .andExpect(status().isOk())
        );
    }

    @Test
    @Transactional
    public void testPostInvalidChoiceFormData_thenVerifyErrorResponse() throws Exception{
        String url = choFormUrl(choId);
        mockMvc.perform(get(url)).andDo(
                result -> mockMvc.perform(
                        post(url)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("save", "save")
                                .param("choiceText", "")
                                .param("choiceWeight", "4")
                                .param("choiceReason", "")
                                .param("question.questionId", quesId.toString())
                )
                        .andDo(print())
                        .andExpect(model().attributeHasFieldErrorCode(
                                "choice","choiceText","NotBlank"))
                        .andExpect(model().attributeHasFieldErrorCode(
                                "choice", "choiceWeight", "Max"))
                        .andExpect(model().attributeHasFieldErrorCode(
                                "choice", "choiceReason", "NotBlank"))
                        .andExpect(view().name("admin/choiceForm"))
                        .andExpect(status().isOk())
        );
    }

    @Test
    @Transactional
    public void testChallengeFormAddAndDeleteChildEntities() throws Exception{
        Challenge challenge = challengeService.findById(chlId);
        List<Question> questions = challenge.getQuestions();
        int oriQuesSize = questions.size();
        int oriChoiceSize = questions.get(0).getChoices().size();
        int quesIdx = 0;

        // Add question and choices first
        String url = chlFormUrl(chlId);
        HttpSession session = mockMvc.perform(get(url))
                .andDo(result ->
                        mockMvc.perform(
                                multipart(url)
                                        .file(file)
                                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                        .param("addQuestion", "")
                                        .characterEncoding("utf-8")
                        ).andExpect(status().is3xxRedirection())
                                .andDo(result1 -> mockMvc.perform(
                                        multipart(url)
                                                .file(file)
                                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                                .param("addChoice", Integer.toString(quesIdx))
                                                .characterEncoding("utf-8")
                                )).andExpect(status().is3xxRedirection())
                ).andReturn().getRequest().getSession();
        Challenge updatedChallenge = (Challenge) session.getAttribute("challenge");

        // Ensure the child items are added.
        assertThat(updatedChallenge.getQuestions().size()).isEqualTo(oriQuesSize+1);
        assertThat(updatedChallenge.getQuestions().get(quesIdx).getChoices().size()).isEqualTo(oriChoiceSize+1);

        // Increment as child entities are added
        oriQuesSize++;
        oriChoiceSize++;

        // Delete the added choice and question
        int finalOriChoiceSize = oriChoiceSize;
        int finalOriQuesSize = oriQuesSize;
        session = mockMvc.perform(get(url))
                .andDo(result ->
                        mockMvc.perform(
                                multipart(url)
                                        .file(file)
                                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                        .param("removeChoice", quesIdx+","+(finalOriChoiceSize - 1))
                                        .characterEncoding("utf-8")
                        ).andExpect(status().is3xxRedirection())
                            .andDo(result1 -> mockMvc.perform(
                                    multipart(url)
                                            .file(file)
                                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                            .param("removeQuestion", ""+(finalOriQuesSize -1))
                                            .characterEncoding("utf-8")
                        ).andExpect(status().is3xxRedirection())
                    )
                ).andReturn().getRequest().getSession();

        updatedChallenge = (Challenge) session.getAttribute("challenge");
        assertThat(updatedChallenge.getQuestions().size()).isEqualTo(oriQuesSize-1);
        assertThat(updatedChallenge.getQuestions().get(quesIdx).getChoices().size()).isEqualTo(oriChoiceSize-1);
    }

    @Test
    @Transactional
    public void testQuestionFormAddAndDeleteChildEntities() throws Exception{
        Question question = questionService.findById(quesId);
        List<Choice> choices = question.getChoices();
        int oriChoiceSize = choices.size();

        // Add choices first
        String url = quesFormUrl(quesId);
        HttpSession session = mockMvc.perform(get(url))
                .andDo(result ->
                        mockMvc.perform(
                                post(url)
                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                        .param("addChoice", "")
                                        .characterEncoding("utf-8")
                        ).andExpect(status().is3xxRedirection())
                ).andReturn().getRequest().getSession();
        Question updatedQuestion = (Question) session.getAttribute("question");

        // Ensure the child items are added.
        assertThat(updatedQuestion.getChoices().size()).isEqualTo(oriChoiceSize+1);

        // Increment as child entities are added
        oriChoiceSize++;

        // Delete the added choice
        session = mockMvc.perform(post(url)
                                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                    .param("removeChoice", ""+(oriChoiceSize - 1))
                                    .characterEncoding("utf-8")
                    )
                .andExpect(status().is3xxRedirection()).andReturn().getRequest().getSession();

        updatedQuestion = (Question) session.getAttribute("question");
        assertThat(updatedQuestion.getChoices().size()).isEqualTo(oriChoiceSize-1);
    }
}
