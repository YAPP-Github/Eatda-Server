package timeeat.client.map;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

public class MapServerErrorHandler implements ErrorHandler {

    @Override
    public void handle(HttpRequest request, ClientHttpResponse response) throws IOException {
        throw new BusinessException(BusinessErrorCode.MAP_SERVER_ERROR);
    }
}
