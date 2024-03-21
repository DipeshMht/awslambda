package org.example.dipesh;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dipesh.Entity.Gym;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Utility {
    public static Map<String,String> createHeaders(){
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        headers.put("X-amazon-author","Lipsa");
        headers.put("X-amazon-apiVersion","v1");
        return  headers ;
    }

    public static String convertObjToString(Gym gym, Context context){
        String jsonBody = null;
        try {
            jsonBody =   new ObjectMapper().writeValueAsString(gym);
        } catch (JsonProcessingException e) {
            context.getLogger().log( "Error while converting obj to string:::" + e.getMessage());
        }
        return jsonBody;
    }
    public static Gym convertStringToObj(String jsonBody, Context context){
        Gym gym = null;
        try {
            gym =   new ObjectMapper().readValue(jsonBody,Gym.class);
        } catch (JsonProcessingException e) {
            context.getLogger().log( "Error while converting string to obj:::" + e.getMessage());
        }
        return gym;
    }
    public static String convertListOfObjToString(List<Gym> gyms, Context context){
        String jsonBody = null;
        try {
            jsonBody =   new ObjectMapper().writeValueAsString(gyms);
        } catch (JsonProcessingException e) {
            context.getLogger().log( "Error while converting obj to string:::" + e.getMessage());
        }
        return jsonBody;
    }
}
