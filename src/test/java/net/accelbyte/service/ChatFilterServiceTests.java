package net.accelbyte.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import net.accelbyte.config.MockedAppConfiguration;
//import net.accelbyte.chatv2.filter.ChatMessage;
//import net.accelbyte.chatv2.filter.ChatMessageBulk;
//import net.accelbyte.chatv2.filter.FilterServiceGrpc;
//import net.accelbyte.chatv2.filter.HealthCheckRequest;
//import net.accelbyte.chatv2.filter.HealthCheckResponse;
//import net.accelbyte.chatv2.filter.MessageBatchResult;
//import net.accelbyte.chatv2.filter.FilterServiceGrpc.FilterServiceBlockingStub;
//import net.accelbyte.chatv2.filter.HealthCheckResponse.ServingStatus;
import net.accelbyte.profanityfilter.registered.v1.ExtendProfanityValidationRequest;
import net.accelbyte.profanityfilter.registered.v1.ExtendProfanityValidationResponse;
import net.accelbyte.profanityfilter.registered.v1.ProfanityFilterServiceGrpc;
import net.accelbyte.sdk.core.AccelByteSDK;
import io.grpc.stub.MetadataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lognet.springboot.grpc.context.LocalRunningGrpcPort;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ActiveProfiles("test")
@SpringBootTest(
        classes = MockedAppConfiguration.class,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
class ChatFilterServiceTests {
        private ManagedChannel channel;

        private final Metadata header = new Metadata();

        @LocalRunningGrpcPort
        int port;

        @Autowired
        AccelByteSDK sdk;

        @BeforeEach
        private void init() {
                channel = ManagedChannelBuilder.forAddress("localhost", port)
                        .usePlaintext()
                        .build();

                Metadata.Key<String> key = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);
                header.put(key, "Bearer abc");
        }

        @Test
        void filterBulk() {
                Mockito.reset(sdk);
                Mockito.when(sdk.validateToken(any(), any(), anyInt())).thenReturn(true);

                final ProfanityFilterServiceGrpc.ProfanityFilterServiceBlockingStub stub = ProfanityFilterServiceGrpc.newBlockingStub(channel)
                        .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));

                ExtendProfanityValidationResponse result;

                result = stub.validate(ExtendProfanityValidationRequest.newBuilder().setValue("test").build());
                assertEquals(false, result.getIsProfane());

                result = stub.validate(ExtendProfanityValidationRequest.newBuilder().setValue("you are very bad").build());
                assertEquals(true, result.getIsProfane());
        }
}