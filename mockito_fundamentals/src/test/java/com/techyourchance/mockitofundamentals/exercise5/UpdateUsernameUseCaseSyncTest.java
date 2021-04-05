package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

public class UpdateUsernameUseCaseSyncTest {

    private UpdateUsernameUseCaseSync sut;
    private UpdateUsernameHttpEndpointSync updateUsernameHttpEndpointSyncMock;
    private UsersCache usersCacheMock;
    private EventBusPoster eventBusPosterMock;
    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";

    @Before
    public void setUp() throws Exception {
        updateUsernameHttpEndpointSyncMock = Mockito.mock(UpdateUsernameHttpEndpointSync.class);
        usersCacheMock = Mockito.mock(UsersCache.class);
        eventBusPosterMock = Mockito.mock(EventBusPoster.class);
        sut = new UpdateUsernameUseCaseSync(updateUsernameHttpEndpointSyncMock, usersCacheMock, eventBusPosterMock);
        success();
    }

    @Test
    public void updateUsernameSync_success_userIdAndUsernamePassedToEndpoint() throws Exception {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        sut.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verify(updateUsernameHttpEndpointSyncMock, Mockito.times(1)).updateUsername(argumentCaptor.capture(), argumentCaptor.capture());
        List<String> captures = argumentCaptor.getAllValues();
        Assert.assertThat(captures.get(0), is(USER_ID));
        Assert.assertThat(captures.get(1), is(USERNAME));
    }

    @Test
    public void updateUsernameSync_success_successReturned() {
        UpdateUsernameUseCaseSync.UseCaseResult result = sut.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS));
    }

    @Test
    public void updateUsernameSync_success_userCached() {
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        sut.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verify(usersCacheMock).cacheUser(argumentCaptor.capture());
        Assert.assertThat(argumentCaptor.getValue().getUserId(), is(USER_ID));
        Assert.assertThat(argumentCaptor.getValue().getUsername(), is(USERNAME));
    }

    @Test
    public void updateUsernameSync_success_cachedUserReturned() {
        sut.updateUsernameSync(USER_ID, USERNAME);
        getUser();
        User user = usersCacheMock.getUser(USER_ID);
        Assert.assertThat(user.getUserId(), is(USER_ID));
        Assert.assertThat(user.getUsername(), is(USERNAME));
    }

    @Test
    public void updateUsernameSync_success_eventPosted() {
        ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);
        sut.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verify(eventBusPosterMock).postEvent(argumentCaptor.capture());
        Assert.assertThat(argumentCaptor.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    @Test
    public void updateUsernameSync_generalError_failureReturned() throws Exception {
        generalError();
        UpdateUsernameUseCaseSync.UseCaseResult result = sut.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameSync_authError_failureReturned() throws Exception {
        authError();
        UpdateUsernameUseCaseSync.UseCaseResult result = sut.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameSync_serverError_failureReturned() throws Exception {
        serverError();
        UpdateUsernameUseCaseSync.UseCaseResult result = sut.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameSync_networkError_networkErrorReturned() throws Exception {
        networkError();
        UpdateUsernameUseCaseSync.UseCaseResult result = sut.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }

    @Test
    public void updateUsernameSync_generalError_noUserCached() throws Exception {
        generalError();
        sut.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUsernameSync_serverError_noUserCached() throws Exception {
        serverError();
        sut.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUsernameSync_authError_noUserCached() throws Exception {
        authError();
        sut.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUsernameSync_networkError_noUserCached() throws Exception {
        networkError();
        sut.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUsernameSync_generalError_noEventPosted() throws Exception {
        generalError();
        sut.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUsernameSync_authError_noEventPosted() throws Exception {
        authError();
        sut.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUsernameSync_serverError_noEventPosted() throws Exception {
        serverError();
        sut.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUsernameSync_networkError_noEventPosted() throws Exception {
        networkError();
        sut.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyNoMoreInteractions(eventBusPosterMock);
    }

    private void success() throws Exception {
        Mockito.when(updateUsernameHttpEndpointSyncMock.updateUsername(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void getUser() {
        Mockito.when(usersCacheMock.getUser(Mockito.any(String.class)))
                .thenReturn(new User(USER_ID, USERNAME));
    }

    private void serverError() throws Exception {
        Mockito.when(updateUsernameHttpEndpointSyncMock.updateUsername(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void authError() throws Exception {
        Mockito.when(updateUsernameHttpEndpointSyncMock.updateUsername(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void generalError() throws Exception {
        Mockito.when(updateUsernameHttpEndpointSyncMock.updateUsername(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void networkError() throws Exception {
        Mockito.doThrow(new NetworkErrorException())
                .when(updateUsernameHttpEndpointSyncMock).updateUsername(Mockito.any(String.class), Mockito.any(String.class));
    }
}