package timeeat.client.oauth;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

@Component
public class OauthServerErrorHandler implements ErrorHandler {

    @Override
    public void handle(HttpRequest request, ClientHttpResponse response) throws IOException {
        throw new BusinessException(BusinessErrorCode.OAUTH_SERVER_ERROR);
    }
}
