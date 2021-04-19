package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest {

    private static final String ID = "id";
    private static final String BODY = "body";
    private static final String TITLE = "title";
    private FetchQuestionDetailsUseCase sut;
    @Mock FetchQuestionDetailsEndpointTd fetchQuestionDetailsEndpointTd;
    @Mock FetchQuestionDetailsUseCase.Listener listener1;
    @Mock FetchQuestionDetailsUseCase.Listener listener2;
    @Captor ArgumentCaptor<QuestionDetails> captor;

    @Before
    public void setUp() {
        fetchQuestionDetailsEndpointTd = new FetchQuestionDetailsEndpointTd();
        sut = new FetchQuestionDetailsUseCase(fetchQuestionDetailsEndpointTd);
        success();
    }

    @Test
    public void fetchQuestionDetailsAndNotify_success_listenerNotifiedWithCorrectData() {
        sut.registerListener(listener1);
        sut.registerListener(listener2);
        sut.fetchQuestionDetailsAndNotify(ID);
        Mockito.verify(listener1).onQuestionDetailsFetched(captor.capture());
        Mockito.verify(listener2).onQuestionDetailsFetched(captor.capture());
        List<QuestionDetails> questionDetails = captor.getAllValues();
        assertThat(questionDetails.get(0), is(new QuestionDetails(ID, TITLE, BODY)));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_failure_listenersNotifiedOfFailure() {
        failure();
        sut.registerListener(listener1);
        sut.registerListener(listener2);
        sut.fetchQuestionDetailsAndNotify(ID);
        Mockito.verify(listener1).onQuestionDetailsFetchFailed();
        Mockito.verify(listener2).onQuestionDetailsFetchFailed();
    }

    private void success() {
        // no-op
    }

    private void failure() {
        fetchQuestionDetailsEndpointTd.isFailure = true;
    }

    private static class FetchQuestionDetailsEndpointTd extends FetchQuestionDetailsEndpoint {

        boolean isFailure;

        FetchQuestionDetailsEndpointTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetails(String questionId, Listener listener) {
            if (isFailure) {
                listener.onQuestionDetailsFetchFailed();
            } else {
                listener.onQuestionDetailsFetched(new QuestionSchema(TITLE, ID, BODY));
            }
        }
    }
}