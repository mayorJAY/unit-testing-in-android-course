package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

public class FetchUserProfileUseCaseSyncTest {

    private FetchUserProfileUseCaseSync sut;
    private UserProfileHttpEndpointSyncTd userProfileHttpEndpointSyncTd;
    private UsersCacheTd usersCacheTd;
    private static final String USER_ID = "user_id";

    @Before
    public void setUp() {
        userProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        usersCacheTd = new UsersCacheTd();
        sut = new FetchUserProfileUseCaseSync(userProfileHttpEndpointSyncTd, usersCacheTd);
    }

    // pass valid user id, return success
    @Test
    public void fetchUserProfileSync_success_successReturned() {
        FetchUserProfileUseCaseSync.UseCaseResult result = sut.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.SUCCESS));
    }

    // pass invalid user id, return failure
    @Test
    public void fetchUserProfileSync_invalidId_failureReturned() {
        FetchUserProfileUseCaseSync.UseCaseResult result = sut.fetchUserProfileSync("");
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    // pass valid user id, id should be the same passed to UserProfileHttpEndpointSync
    @Test
    public void fetchUserProfileSync_success_userIdPassedIsSentToEndpoint() {
        sut.fetchUserProfileSync(USER_ID);
        Assert.assertThat(userProfileHttpEndpointSyncTd.mUserId, is(USER_ID));
    }

    // pass valid user id, id should be the same passed to UsersCache
    @Test
    public void fetchUserProfileSync_success_userIdPassedIsSentToUsersCache() {
        sut.fetchUserProfileSync(USER_ID);
        usersCacheTd.getUser(USER_ID);
        Assert.assertThat(usersCacheTd.mUserId, is(USER_ID));
    }

    // pass valid user id, valid User returned
    @Test
    public void fetchUserProfileSync_success_userReturnedFromCache() {
        sut.fetchUserProfileSync(USER_ID);
        User user = usersCacheTd.getUser(USER_ID);
        Assert.assertThat(user, is(notNullValue()));
    }

    // pass invalid user id, null returned
    @Test
    public void fetchUserProfileSync_success_nullReturnedFromCache() {
        sut.fetchUserProfileSync("id");
        User user = usersCacheTd.getUser("id");
        Assert.assertThat(user, is(nullValue()));
    }

    // network issue happens, return network error
    @Test
    public void fetchUserProfileSync_networkError_failureReturned() {
        userProfileHttpEndpointSyncTd.isNetworkError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = sut.fetchUserProfileSync("id");
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }

    // auth error happens, returns failure
    @Test
    public void fetchUserProfileSync_authError_failureReturned() {
        userProfileHttpEndpointSyncTd.isAuthError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = sut.fetchUserProfileSync("id");
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    // server error happens, returns failure
    @Test
    public void fetchUserProfileSync_serverError_failureReturned() {
        userProfileHttpEndpointSyncTd.isServerError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = sut.fetchUserProfileSync("id");
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    // general error happens, returns failure
    @Test
    public void fetchUserProfileSync_generalError_failureReturned() {
        userProfileHttpEndpointSyncTd.isGeneralError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = sut.fetchUserProfileSync("nil");
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    // general error happens, returns userNotCached
    @Test
    public void fetchUserProfileSync_generalError_userNotCached() {
        userProfileHttpEndpointSyncTd.isGeneralError = true;
        sut.fetchUserProfileSync("nil");
        User user = usersCacheTd.getUser("nil");
        Assert.assertThat(user, is(nullValue()));
    }

    // auth error happens, returns userNotCached
    @Test
    public void fetchUserProfileSync_authError_userNotCached() {
        userProfileHttpEndpointSyncTd.isAuthError = true;
        sut.fetchUserProfileSync("nil");
        User user = usersCacheTd.getUser("nil");
        Assert.assertThat(user, is(nullValue()));
    }

    // server error happens, returns userNotCached
    @Test
    public void fetchUserProfileSync_serverError_userNotCached() {
        userProfileHttpEndpointSyncTd.isServerError = true;
        sut.fetchUserProfileSync("nil");
        User user = usersCacheTd.getUser("nil");
        Assert.assertThat(user, is(nullValue()));
    }

    // network error happens, returns userNotCached
    @Test
    public void fetchUserProfileSync_networkError_userNotCached() {
        userProfileHttpEndpointSyncTd.isServerError = true;
        sut.fetchUserProfileSync("nil");
        User user = usersCacheTd.getUser("nil");
        Assert.assertThat(user, is(nullValue()));
    }

    //========================================= Helper Classes ========================================//
    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {

        String mUserId;
        boolean isGeneralError;
        boolean isAuthError;
        boolean isServerError;
        boolean isNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserId = userId;
            if (userId.equals(USER_ID)) {
                return new EndpointResult(EndpointResultStatus.SUCCESS, userId, "John Doe", "image_url");
            } else if (isGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            } else if (isAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            } else if (isServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            } else if (isNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            }
        }
    }

    private static class UsersCacheTd implements UsersCache {

        private User mUser;
        String mUserId;

        @Override
        public void cacheUser(User user) {
            mUser = user;
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            mUserId = userId;
            if (userId.equals(USER_ID)) {
                return mUser;
            }
            return null;
        }
    }
}