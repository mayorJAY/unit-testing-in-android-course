package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

public class FetchReputationUseCaseSync {

    enum UseCaseResultStatus {
        SUCCESS,
        FAILURE,
        NETWORK_ERROR,
    }

    private GetReputationHttpEndpointSync getReputationHttpEndpointSync;

    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync getReputationHttpEndpointSync) {
        this.getReputationHttpEndpointSync = getReputationHttpEndpointSync;
    }

    public UseCaseResult getReputationSync() {
        GetReputationHttpEndpointSync.EndpointResult result = getReputationHttpEndpointSync.getReputationSync();
        if (result.getStatus() == GetReputationHttpEndpointSync.EndpointStatus.SUCCESS) {
            return new UseCaseResult(UseCaseResultStatus.SUCCESS, result.getReputation());
        } else if (result.getStatus() == GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR) {
            return new UseCaseResult(UseCaseResultStatus.FAILURE, result.getReputation());
        }
        return new UseCaseResult(UseCaseResultStatus.NETWORK_ERROR, result.getReputation());
    }

    static class UseCaseResult {

        private UseCaseResultStatus mStatus;
        private int mUseCaseReputation;

        public UseCaseResult(UseCaseResultStatus useCaseResultStatus, int useCaseReputation) {
            mStatus = useCaseResultStatus;
            mUseCaseReputation = useCaseReputation;
        }

        public UseCaseResultStatus getStatus() {
            return mStatus;
        }

        public int getReputation() {
            return mUseCaseReputation;
        }
    }
}
