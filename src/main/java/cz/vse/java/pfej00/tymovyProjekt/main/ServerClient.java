package cz.vse.java.pfej00.tymovyProjekt.main;

import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;

import java.io.IOException;


/**
 * All methods for calling the server
 */
public class ServerClient {
    private final OkHttpClient httpClient = new OkHttpClient();
    private static final Logger logger = LogManager.getLogger(ClientCallerTask.class);

    //user operations
    public Response sendRegisterNewUser(String post) throws Exception {
        RequestBody body = RequestBody.create(post, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://vsebug-be.herokuapp.com/register/")
                .post(body)
                .build();

        try {
            logger.info("Registering user {}", post);
            return httpClient.newCall(request).execute();
        }
        catch (IOException e) {
            logger.error("Error while registering user caused by {}", e.getMessage());
        }
        return null;
    }

    public Response sendLoginUser(String post) throws Exception {

            RequestBody body = RequestBody.create(post, MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("https://vsebug-be.herokuapp.com/login/")
                    .post(body)
                    .build();

        try {
            logger.info("Logging user with credentials {}", post);
            return httpClient.newCall(request).execute();
        }
        catch (IOException e) {
            logger.error("Logging user failed, because of {}", e.getMessage());
        }
        return null;
    }


    //issues
    public Response sendGetIssues() throws Exception {
        Request request = new Request.Builder()
                .url("https://vsebug-be.herokuapp.com/issues/")
                .addHeader("custom-key", "mkyong")  // add request headers
                .addHeader("User-Agent", "OkHttp Bot")
                .build();

        try {
            logger.info("Getting issues");
            return httpClient.newCall(request).execute();
        }
        catch (IOException e) {
            logger.error("Error occurred while getting issues, caused by {}", e.getMessage());
        }
        return null;
    }

    public Response sendUpdateIssue() throws Exception {
            RequestBody requestBody = RequestBody.create("asdawd".getBytes());
            Request request = new Request.Builder().method("POST", requestBody).build();
            return httpClient.newCall(request).execute();
    }

    //Projects
    public Response sendGetProjects() throws Exception {
        Request request = new Request.Builder()
                .url("https://vsebug-be.herokuapp.com/projects/")
                .build();
        try {
            logger.info("Getting projects");
            return httpClient.newCall(request).execute();
        }
        catch (IOException e) {
            logger.error("Error occurred while getting projects, caused by {}", e.getMessage());
        }
        return null;
    }

    //nevim routu
    public Response sendUpdateProject() throws Exception {
        RequestBody requestBody = RequestBody.create("asdawd".getBytes());
        Request request = new Request.Builder().method("POST", requestBody).build();
        try {
            logger.info("Updating project");
            return httpClient.newCall(request).execute();
        }
        catch (IOException e) {
            logger.error("Error occurred while updating project, caused by {}", e.getMessage());
        }
        return null;
    }
}
