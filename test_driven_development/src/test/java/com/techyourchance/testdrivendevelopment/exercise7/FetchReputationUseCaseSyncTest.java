package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest {

    private FetchReputationUseCaseSync sut;
    @Mock
    GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock;
    private static int VALID_FIGURE = 200;
    private static int ZERO = 0;

    @Before
    public void setUp() {
        sut = new FetchReputationUseCaseSync(getReputationHttpEndpointSyncMock);
        success();
    }

    @Test
    public void getReputationSync_success_successReturned() {
        FetchReputationUseCaseSync.UseCaseResult result = sut.getReputationSync();
        assertThat(result.getStatus(), is(FetchReputationUseCaseSync.UseCaseResultStatus.SUCCESS));
    }

    @Test
    public void getReputationSync_success_reputationReturned() {
        FetchReputationUseCaseSync.UseCaseResult result = sut.getReputationSync();
        assertThat(result.getReputation(), is(VALID_FIGURE));
    }

    @Test
    public void getReputationSync_generalError_failureReturned() {
        generalError();
        FetchReputationUseCaseSync.UseCaseResult result = sut.getReputationSync();
        assertThat(result.getStatus(), is(FetchReputationUseCaseSync.UseCaseResultStatus.FAILURE));
    }

    @Test
    public void getReputationSync_generalError_zeroReputationReturned() {
        generalError();
        FetchReputationUseCaseSync.UseCaseResult result = sut.getReputationSync();
        assertThat(result.getReputation(), is(ZERO));
    }

    @Test
    public void getReputationSync_networkError_networkErrorReturned() {
        networkError();
        FetchReputationUseCaseSync.UseCaseResult result = sut.getReputationSync();
        assertThat(result.getStatus(), is(FetchReputationUseCaseSync.UseCaseResultStatus.NETWORK_ERROR));
    }

    @Test
    public void getReputationSync_networkError_zeroReputationReturned() {
        networkError();
        FetchReputationUseCaseSync.UseCaseResult result = sut.getReputationSync();
        assertThat(result.getReputation(), is(ZERO));
    }

    private void success() {
        Mockito.when(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.SUCCESS, VALID_FIGURE));
    }

    private void generalError() {
        Mockito.when(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR, ZERO));
    }

    private void networkError() {
        Mockito.when(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR, ZERO));
    }
}