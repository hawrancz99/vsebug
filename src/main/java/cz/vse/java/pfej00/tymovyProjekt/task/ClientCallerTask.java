package cz.vse.java.pfej00.tymovyProjekt.task;

import cz.vse.java.pfej00.tymovyProjekt.main.ServerClient;
import javafx.concurrent.Task;
import okhttp3.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientCallerTask extends Task<Response> {

    private String url;

    private String post;

    private final ServerClient SERVER_CLIENT = new ServerClient();

    private static final Logger logger = LogManager.getLogger(ClientCallerTask.class);

    public ClientCallerTask(String url, String post) {
        this.url = url;
        this.post = post;
    }

    @Override
    public Response call() throws Exception {
        if (url.equals("sendGetIssues")) {
           return SERVER_CLIENT.sendGetIssues();
        }
        if (url.equals("sendUpdateIssue")) {
           return SERVER_CLIENT.sendUpdateIssue();
        }
        if (url.equals("sendLoginUser")) {
            return SERVER_CLIENT.sendLoginUser(post);
        }
        if (url.equals("registerNewUser")) {
            return SERVER_CLIENT.sendRegisterNewUser(post);
        }
        return null;
    }
}
