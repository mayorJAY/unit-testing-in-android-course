package com.techyourchance.unittesting.screens.questiondetails;

import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase;
import com.techyourchance.unittesting.questions.QuestionDetails;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;
import com.techyourchance.unittesting.testdata.QuestionsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QuestionDetailsControllerTest {


    public static final String QUESTION_ID = "questionId";
    public static final QuestionDetails QUESTION_DETAILS = QuestionsTestData.getQuestionDetails();
    private QuestionDetailsController sut;
    private UseCaseTd useCaseTd;
    @Mock
    ScreensNavigator screensNavigator;
    @Mock
    ToastsHelper toastsHelper;
    @Mock QuestionDetailsViewMvc questionDetailsViewMvc;

    @Before
    public void setUp() {
        useCaseTd = new UseCaseTd();
        sut = new QuestionDetailsController(useCaseTd, screensNavigator, toastsHelper);
        sut.bindView(questionDetailsViewMvc);
        sut.bindQuestionId(QUESTION_ID);
    }

    @Test
    public void onStart_listenersRegistered() {
        sut.onStart();
        verify(questionDetailsViewMvc).registerListener(sut);
        useCaseTd.verifyListenerRegistered(sut);
    }

    @Test
    public void onStart_progressIndicationShown() {
        sut.onStart();
        verify(questionDetailsViewMvc).showProgressIndication();
    }

    @Test
    public void onStart_success_questionBound() {
        success();
        sut.onStart();
        verify(questionDetailsViewMvc).bindQuestion(QUESTION_DETAILS);
    }

    @Test
    public void onStart_success_progressIndicationHidden() {
        success();
        sut.onStart();
        verify(questionDetailsViewMvc).hideProgressIndication();
    }

    @Test
    public void onStart_failure_toastDisplayed() {
        failure();
        sut.onStart();
        verify(toastsHelper).showUseCaseError();
    }

    @Test
    public void onStart_failure_progressIndicationHidden() {
        failure();
        sut.onStart();
        verify(questionDetailsViewMvc).hideProgressIndication();
    }

    @Test
    public void onNavigateUpClicked_navigatedUp() {
        sut.onNavigateUpClicked();
        verify(screensNavigator).navigateUp();
    }

    @Test
    public void toQuestionDetails_navigateToQuestionDetails() {
        sut.toQuestionDetails(QUESTION_ID);
        verify(screensNavigator).toQuestionDetails(QUESTION_ID);
    }

    @Test
    public void toQuestionsList_navigateToQuestionList() {
        sut.toQuestionsList();
        verify(screensNavigator).toQuestionsList();
    }

    @Test
    public void onStop_listenersNotRegistered() {
        sut.onStop();
        verify(questionDetailsViewMvc).unregisterListener(sut);
        useCaseTd.verifyListenerNotRegistered(sut);
    }

    private void success() {
        //no-op
    }

    private void failure() {
        useCaseTd.isFailure = true;
    }

    private static class UseCaseTd extends FetchQuestionDetailsUseCase {

        public boolean isFailure;

        public UseCaseTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetailsAndNotify(String questionId) {
            if (!questionId.equals(QUESTION_ID)) {
                throw new RuntimeException("invalid question ID: " + questionId);
            }
            for (Listener listener: getListeners()) {
                if (isFailure) {
                    listener.onQuestionDetailsFetchFailed();
                    return;
                }
                listener.onQuestionDetailsFetched(QUESTION_DETAILS);
            }
        }

        public void verifyListenerRegistered(QuestionDetailsController candidate) {
            for (Listener listener : getListeners()) {
                if (listener == candidate) {
                    return;
                }
            }
            throw new RuntimeException("listener not registered");
        }

        public void verifyListenerNotRegistered(QuestionDetailsController candidate) {
            for (Listener listener : getListeners()) {
                if (listener == candidate) {
                    throw new RuntimeException("listener registered");
                }
            }
        }
    }
}