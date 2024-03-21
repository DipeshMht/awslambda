package org.example.dipesh;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


    @Override
    public APIGatewayProxyResponseEvent  handleRequest(APIGatewayProxyRequestEvent apiGatewayRequest, Context context) {
        GymService gymService = new GymService();
        switch (apiGatewayRequest.getHttpMethod()) {

            case "POST":
                return gymService.saveGym(apiGatewayRequest, context);

            case "GET":
                if (apiGatewayRequest.getPathParameters() != null) {
                    return gymService.getGymById(apiGatewayRequest, context);
                }
                return gymService.getGyms(apiGatewayRequest, context);
            case "PUT":
                if (apiGatewayRequest.getPathParameters() != null) {
                    return gymService.updateGymById(apiGatewayRequest, context);
                } else {
                    throw new Error("Missing student ID in PUT request");
                }
            case "DELETE":
                if (apiGatewayRequest.getPathParameters() != null) {
                    return gymService.deleteGymById(apiGatewayRequest, context);
                }
            default:
                throw new Error("Unsupported Methods:::" + apiGatewayRequest.getHttpMethod());

        }
    }
}