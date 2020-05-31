package cz.vse.java.pfej00.tymovyProjekt.task;

import cz.vse.java.pfej00.tymovyProjekt.main.ServerClient;
import javafx.concurrent.Task;
import okhttp3.Response;

/**
 * Třída dědí ze třídy Task
 * Slouží k multithreadingovým opearcím
 * Task je následně vyvolán pomocí třídy ExecutorService
 * Jako odpověď vrací vždy RESPONSE, tedy výsledek volání BE
 */
public class ClientCallerTask extends Task<Response> {

    private String url;

    private String post;

    private final ServerClient SERVER_CLIENT = new ServerClient();


    /**
     * Konstruktor třídy
     *
     * @param url  slouží k identifikaci, jaká URL se má zavolat
     * @param post slouží k plnění body či části url, pokud to volání vyžaduje
     */
    public ClientCallerTask(String url, String post) {
        this.url = url;
        this.post = post;
    }

    @Override
    public Response call() throws Exception {
        if (url.equals("sendGetIssuesForProject")) {
            return SERVER_CLIENT.sendGetIssuesForProject(post);
        }
        if (url.equals("sendUpdateIssue")) {
            return SERVER_CLIENT.sendUpdateIssue(post);
        }
        if (url.equals("sendDeleteIssue")) {
            return SERVER_CLIENT.sendDeleteIssue(post);
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
        return null;
    }
}
