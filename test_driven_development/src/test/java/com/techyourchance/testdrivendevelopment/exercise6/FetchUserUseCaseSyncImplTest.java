package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncImplTest {

    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private FetchUserUseCaseSyncImpl sut;
    @Mock
    FetchUserHttpEndpointSync fetchUserHttpEndpointSyncMock;
    @Mock
    UsersCache usersCacheMock;

    @Before
    public void setUp() throws Exception {
        sut = new FetchUserUseCaseSyncImpl(fetchUserHttpEndpointSyncMock, usersCacheMock);
        success();
    }

    @Test
    public void fetchUserSync_success_validUserIdSentToDataLayer() throws Exception {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        sut.fetchUserSync(USER_ID);
        Mockito.verify(fetchUserHttpEndpointSyncMock, Mockito.times(1)).fetchUserSync(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), is(USER_ID));
    }

    @Test
    public void fetchUserSync_success_successReturned() {
        FetchUserUseCaseSync.UseCaseResult result = sut.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.SUCCESS));
    }

    @Test
    public void fetchUserSync_success_userCached() {
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        sut.fetchUserSync(USER_ID);
        Mockito.verify(usersCacheMock).cacheUser(argumentCaptor.capture());
        Assert.assertThat(argumentCaptor.getValue().getUserId(), is(USER_ID));
        Assert.assertThat(argumentCaptor.getValue().getUsername(), is(USERNAME));
    }

    @Test
    public void fetchUserSync_success_cachedUserReturned() {
        sut.fetchUserSync(USER_ID);
        getUser();
        User user = usersCacheMock.getUser(USER_ID);
        Assert.assertThat(user.getUserId(), is(USER_ID));
        Assert.assertThat(user.getUsername(), is(USERNAME));
    }

    @Test
    public void fetchUserSync_generalError_failureReturned() throws Exception {
        generalError();
        FetchUserUseCaseSync.UseCaseResult result = sut.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.FAILURE));
    }

    @Test
    public void fetchUserSync_authError_failureReturned() throws Exception {
        authError();
        FetchUserUseCaseSync.UseCaseResult result = sut.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.FAILURE));
    }

    @Test
    public void fetchUserSync_serverError_failureReturned() throws Exception {
        serverError();
        FetchUserUseCaseSync.UseCaseResult result = sut.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.FAILURE));
    }

    @Test
    public void fetchUserSync_networkError_networkErrorReturned() throws Exception {
        networkError();
        FetchUserUseCaseSync.UseCaseResult result = sut.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.NETWORK_ERROR));
    }

    @Test
    public void fetchUserSync_success_validUserReturned() {
        getUser();
        FetchUserUseCaseSync.UseCaseResult result = sut.fetchUserSync(USER_ID);
        assertThat(result.getUser(), is(notNullValue()));
        assertThat(result.getUser().getUserId(), is(USER_ID));
        assertThat(result.getUser().getUsername(), is(USERNAME));
    }

    @Test
    public void fetchUserSync_generalError_nullUserReturned() throws Exception {
        generalError();
        FetchUserUseCaseSync.UseCaseResult result = sut.fetchUserSync(USER_ID);
        assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUserSync_authError_nullUserReturned() throws Exception {
        authError();
        FetchUserUseCaseSync.UseCaseResult result = sut.fetchUserSync(USER_ID);
        assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUserSync_serverError_nullUserReturned() throws Exception {
        serverError();
        FetchUserUseCaseSync.UseCaseResult result = sut.fetchUserSync(USER_ID);
        assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUserSync_networkError_nullUserReturned() throws Exception {
        networkError();
        FetchUserUseCaseSync.UseCaseResult result = sut.fetchUserSync(USER_ID);
        assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUserSync_anyError_noUserCached() throws Exception {
        generalError();
        sut.fetchUserSync(USER_ID);
        Mockito.verify(usersCacheMock, Mockito.atMost(1)).getUser(USER_ID);
    }

    //=================================== HELPER METHODS =========================================//

    private void success() throws NetworkErrorException {
        Mockito.when(fetchUserHttpEndpointSyncMock.fetchUserSync(USER_ID))
                .thenReturn(new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void generalError() throws NetworkErrorException {
        Mockito.when(fetchUserHttpEndpointSyncMock.fetchUserSync(USER_ID))
                .thenReturn(new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.GENERAL_ERROR, "", ""));
    }

    private void authError() throws NetworkErrorException {
        Mockito.when(fetchUserHttpEndpointSyncMock.fetchUserSync(USER_ID))
                .thenReturn(new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.AUTH_ERROR, "", ""));
    }

    private void serverError() throws NetworkErrorException {
        Mockito.when(fetchUserHttpEndpointSyncMock.fetchUserSync(USER_ID))
                .thenReturn(new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.SERVER_ERROR, "", ""));
    }

    private void networkError() throws NetworkErrorException {
        Mockito.when(fetchUserHttpEndpointSyncMock.fetchUserSync(USER_ID))
                .thenThrow(new NetworkErrorException());
    }

    private void getUser() {
        Mockito.when(usersCacheMock.getUser(Mockito.any(String.class)))
                .thenReturn(new User(USER_ID, USERNAME));
    }
}