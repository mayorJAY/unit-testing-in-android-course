package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {
    private FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    private UsersCache usersCache;

    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync fetchUserHttpEndpointSync, UsersCache usersCache) {
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.usersCache = usersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        if (usersCache.getUser(userId) != null) {
            return new UseCaseResult(Status.SUCCESS, usersCache.getUser(userId));
        }

        FetchUserHttpEndpointSync.EndpointResult endpointResult;
        try {
            endpointResult = fetchUserHttpEndpointSync.fetchUserSync(userId);
        } catch (NetworkErrorException e) {
            return new UseCaseResult(Status.NETWORK_ERROR, null);
        }

        if (isSuccessful(endpointResult)) {
            usersCache.cacheUser(new User(endpointResult.getUserId(), endpointResult.getUsername()));
            return new UseCaseResult(Status.SUCCESS, new User(endpointResult.getUserId(), endpointResult.getUsername()));
        } else {
            return new UseCaseResult(Status.FAILURE, null);
        }
    }

    private boolean isSuccessful(FetchUserHttpEndpointSync.EndpointResult endpointResult) {
        return endpointResult.getStatus() == FetchUserHttpEndpointSync.EndpointStatus.SUCCESS;
    }
}
