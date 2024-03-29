package unsl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import unsl.entities.*;

@Service
public class RestService {

    @Autowired
    private RestTemplate restTemplate;
    /*
     * @param url
     * @return
     * @throws Exception
     * */

    public User getUser(String url) throws Exception {
        User user;   

        try {
            user = restTemplate.getForObject(url, User.class);
        }  catch (Exception e){
            throw new Exception( buildMessageError(e));
        }
        return user;
    }
    
    private String buildMessageError(Exception e) {
        String msg = e.getMessage();
        if (e instanceof HttpClientErrorException) {
            msg = ((HttpClientErrorException) e).getResponseBodyAsString();
        } else if (e instanceof HttpServerErrorException) {
            msg =  ((HttpServerErrorException) e).getResponseBodyAsString();
        }
        return msg;
    }

}

