package org.example.dipesh;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.example.dipesh.Entity.Gym;

import java.util.List;
import java.util.Map;

public class GymService {
    private DynamoDBMapper dynamoDBMapper;
    private static String jsonBody = null;

    public APIGatewayProxyResponseEvent saveGym(APIGatewayProxyRequestEvent apiGatewayRequest, Context context){
        initDynamoDB();
        Gym gym = Utility.convertStringToObj(apiGatewayRequest.getBody(), context);
        dynamoDBMapper.save(gym);
        jsonBody = Utility.convertObjToString(gym, context);
        context.getLogger().log("data saved successfully to dynamodb:::" + jsonBody);
        return createAPIResponse(jsonBody, 201, Utility.createHeaders());
    }

    public APIGatewayProxyResponseEvent getGymById(APIGatewayProxyRequestEvent apiGatewayRequest, Context context){
        initDynamoDB();
        String memberID = apiGatewayRequest.getPathParameters().get("memberID");
        Gym gym = dynamoDBMapper.load(Gym.class, memberID);
        if(gym != null) {
            jsonBody = Utility.convertObjToString(gym, context);
            context.getLogger().log("fetch members By ID:::" + jsonBody);
            return createAPIResponse(jsonBody, 200, Utility.createHeaders());
        } else {
            jsonBody = "Member Not Found Exception :" + memberID;
            return createAPIResponse(jsonBody, 400, Utility.createHeaders());
        }
    }

    public APIGatewayProxyResponseEvent getGyms(APIGatewayProxyRequestEvent apiGatewayRequest, Context context){
        initDynamoDB();
        List<Gym> gym = dynamoDBMapper.scan(Gym.class, new DynamoDBScanExpression());
        jsonBody =  Utility.convertListOfObjToString(gym, context);
        context.getLogger().log("fetch member List:::" + jsonBody);
        return createAPIResponse(jsonBody, 200, Utility.createHeaders());
    }

    public APIGatewayProxyResponseEvent updateGymById(APIGatewayProxyRequestEvent apiGatewayRequest, Context context) {
        initDynamoDB();
        String memberID = apiGatewayRequest.getPathParameters().get("memberID");

        // Check if the gym member exists before updating
        Gym existingGym = dynamoDBMapper.load(Gym.class, memberID);
        if (existingGym == null) {
            String errorMessage = "Gym Member Not Found: " + memberID;
            context.getLogger().log(errorMessage);
            return createAPIResponse(errorMessage, 404, Utility.createHeaders());
        }

        // Convert the request body to a Gym object
        Gym updatedGym = Utility.convertStringToObj(apiGatewayRequest.getBody(), context);

        // Update existing gym member with new data
        existingGym.setMemberID(updatedGym.getMemberID());
        existingGym.setName(updatedGym.getName());
        existingGym.setEmail(updatedGym.getEmail());
        existingGym.setDiet(updatedGym.getDiet());
        existingGym.setDuration(updatedGym.getDuration());
        existingGym.setPhno(updatedGym.getPhno());

        // Save the updated gym member to DynamoDB
        dynamoDBMapper.save(existingGym);

        // Log and return success response
        String responseBody = Utility.convertObjToString(existingGym, context);
        context.getLogger().log("Gym member updated successfully: " + responseBody);
        return createAPIResponse(responseBody, 200, Utility.createHeaders());
    }

    public APIGatewayProxyResponseEvent deleteGymById(APIGatewayProxyRequestEvent apiGatewayRequest, Context context){
        initDynamoDB();
        String memberID = apiGatewayRequest.getPathParameters().get("memberID");
        Gym gym = dynamoDBMapper.load(Gym.class, memberID);
        if(gym != null) {
            dynamoDBMapper.delete(gym);
            context.getLogger().log("data deleted successfully :::" + memberID);
            return createAPIResponse("data deleted successfully." + memberID, 200, Utility.createHeaders());
        } else {
            jsonBody = "Member Not Found Exception :" + memberID;
            return createAPIResponse(jsonBody, 400, Utility.createHeaders());
        }
    }

    private void initDynamoDB(){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        dynamoDBMapper = new DynamoDBMapper(client);
    }

    private APIGatewayProxyResponseEvent createAPIResponse(String body, int statusCode, Map<String,String> headers ){
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setBody(body);
        responseEvent.setHeaders(headers);
        responseEvent.setStatusCode(statusCode);
        return responseEvent;
    }
}
