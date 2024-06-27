package net.accelbyte.service;
import org.lognet.springboot.grpc.GRpcService;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import net.accelbyte.profanityfilter.registered.v1.ExtendProfanityValidationRequest;
import net.accelbyte.profanityfilter.registered.v1.ExtendProfanityValidationResponse;
import net.accelbyte.profanityfilter.registered.v1.ProfanityFilterServiceGrpc;

@Slf4j
@GRpcService
public class ProfanityFilterService extends ProfanityFilterServiceGrpc.ProfanityFilterServiceImplBase {
    private static final String PROFANITY_REGEX = ".*\\b(bad|awful|fuck)\\b.*";   // Matches "bad" or "awful" or "fuck"

    @Override
    public void validate(ExtendProfanityValidationRequest request, StreamObserver<ExtendProfanityValidationResponse> responseObserver) {
        log.info("Received validate request");
        final ExtendProfanityValidationResponse.Builder resultBuilder = ExtendProfanityValidationResponse.newBuilder();
        boolean isProfane = isProfane(request.getValue());
        resultBuilder.setIsProfane(isProfane);
        resultBuilder.setMessage(isProfane ? "this contains banned words" : "");
        responseObserver.onNext(resultBuilder.build());
        responseObserver.onCompleted();
    }

    private static boolean isProfane(final String message) {
        return message.matches(PROFANITY_REGEX);
    }
}
