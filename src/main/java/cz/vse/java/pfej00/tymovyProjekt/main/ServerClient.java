package cz.vse.java.pfej00.tymovyProjekt.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.vse.java.pfej00.tymovyProjekt.Model.TokenDto;
import cz.vse.java.pfej00.tymovyProjekt.task.ClientCallerTask;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;


/**
 * All methods for calling the server
 */
public class ServerClient {
    private final OkHttpClient httpClient = new OkHttpClient();
    private static final Logger logger = LogManager.getLogger(ClientCallerTask.class);
    private  String TOKEN = "Bearer " + TokenDto.getTOKEN().getTokenValue();

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

    public Response sendGetUsers() throws Exception {

        Request request = new Request.Builder()
                .url("https://vsebug-be.herokuapp.com/users/")
                .addHeader("authorization", TOKEN)
                .get()
                .build();

        try {
            logger.info("Getting list of all users");
            return httpClient.newCall(request).execute();
        }
        catch (IOException e) {
            logger.error("Getting users failed caused by {}", e.getMessage());
        }
        return null;
    }


    //issues
    public Response sendGetIssues() throws Exception {
        Request request = new Request.Builder()
                .url("https://vsebug-be.herokuapp.com/issues/")
                .addHeader("authorization", TOKEN)
                .get()
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

    public Response sendUpdateIssue() throws Exception {
            RequestBody requestBody = RequestBody.create("asdawd".getBytes());
            Request request = new Request.Builder().method("POST", requestBody).build();
            return httpClient.newCall(request).execute();
    }

    //Projects
    public Response sendGetProjects() throws Exception {
        Request request = new Request.Builder()
                .url("https://vsebug-be.herokuapp.com/projects/")
                .addHeader("authorization", TOKEN)
                .get()
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
    //Roles
    public Response sendGetRoles() throws Exception {
        Request request = new Request.Builder()
                .url("https://vsebug-be.herokuapp.com/roles/")
                .build();
        try {
            logger.info("Getting Roles");
            return httpClient.newCall(request).execute();
        }
        catch (IOException e) {
            logger.error("Error occurred while getting roles, caused by {}", e.getMessage());
        }
        return null;
    }

    public Response sendCreateProject(String post) throws Exception {
        RequestBody body = RequestBody.create(post, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://vsebug-be.herokuapp.com/projects/")
                .post(body)
                .addHeader("authorization", TOKEN)
                .build();

        try {
            logger.info("Creating new project {}", post);
            return httpClient.newCall(request).execute();
        }
        catch (IOException e) {
            logger.error("Error while creating project caused by {}", e.getMessage());
        }
        return null;
    }

    public Response sendDeleteProject(String post) throws Exception {
        int id = Integer.parseInt(post);
        Request request = new Request.Builder()
                .url("https://vsebug-be.herokuapp.com/projects/" + id +"/")
                .delete()
                .addHeader("authorization", TOKEN)
                .build();

        try {
            logger.info("Deleting project with id {}", id);
            return httpClient.newCall(request).execute();
        }
        catch (IOException e) {
            logger.error("Error while deleting project caused by {}", e.getMessage());
        }
        return null;
    }


    //nevim routu
    public Response sendUpdateProject(String post) throws Exception {
        //musim rozsekat ten post a vzít si z toho jen to co chci
        final ObjectNode node = new ObjectMapper().readValue(post, ObjectNode.class);
        JsonNode idAsText = node.get("id");
        int id = idAsText.asInt();
        JsonNode name = node.get("name");
        JSONObject updatedPost = new JSONObject();
        updatedPost.put("name", name.asText());

        RequestBody body = RequestBody.create(updatedPost.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://vsebug-be.herokuapp.com/projects/" + id +"/")
                .put(body)
                .addHeader("authorization", TOKEN)
                .build();

        try {
            logger.info("Updating project with id {}", id);
            return httpClient.newCall(request).execute();
        }
        catch (IOException e) {
            logger.error("Error while updating project project caused by {}", e.getMessage());
        }
        return null;
    }
}
