package cz.vse.java.pfej00.tymovyProjekt.task;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.vse.java.pfej00.tymovyProjekt.main.ServerClient;
import javafx.concurrent.Task;
import okhttp3.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientCallerTask extends Task<Response> {

    private String url;

    private String post;

    private final ServerClient SERVER_CLIENT = new ServerClient();


    public ClientCallerTask(String url, String post) {
        this.url = url;
        this.post = post;
    }

    @Override
    public Response call() throws Exception {
        if (url.equals("sendGetIssues")) {
           return SERVER_CLIENT.sendGetIssues();
        }
        if (url.equals("sendGetIssuesForProject")) {
            return SERVER_CLIENT.sendGetIssuesForProject(post);
        }
        if (url.equals("sendUpdateIssue")) {
           return SERVER_CLIENT.sendUpdateIssue();
        }
        if (url.equals("sendLoginUser")) {
            return SERVER_CLIENT.sendLoginUser(post);
        }
        if (url.equals("sendRegisterNewUser")) {
            return SERVER_CLIENT.sendRegisterNewUser(post);
        }
        if (url.equals("sendGetProjects")) {
            return SERVER_CLIENT.sendGetProjects();
        }
        if (url.equals("sendGetUsers")) {
            return SERVER_CLIENT.sendGetUsers();
        }
        if (url.equals("sendCreateProject")) {
            return SERVER_CLIENT.sendCreateProject(post);
        }
        if (url.equals("sendDeleteProject")) {
            return SERVER_CLIENT.sendDeleteProject(post);
        }
        if (url.equals("sendUpdateProject")) {
            return SERVER_CLIENT.sendUpdateProject(post);
        }
        if (url.equals("sendCreateIssue")) {
            return SERVER_CLIENT.sendCreateIssue(post);
        }
        if (url.equals("sendFindUser")) {
            return SERVER_CLIENT.sendFindUser(post);
        }
        if (url.equals("sendFindIssue")) {
            return SERVER_CLIENT.sendFindIssue(post);
        }
        return null;
    }
}
