package cz.vse.java.pfej00.tymovyProjekt.main;

import io.restassured.internal.http.Status;
import jdk.net.SocketFlow;
import okhttp3.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ServerClientTest {

    @InjectMocks
    private ServerClient serverClient = Mockito.mock(ServerClient.class);

    private OkHttpClient okHttpClient = new OkHttpClient();

    @Captor
    private ArgumentCaptor<Response> responseArgumentCaptor;

    private MockMvc mockMvc;


    public ServerClientTest() {
        mockMvc = MockMvcBuilders.standaloneSetup(serverClient).build();
    }

    @Before
    public void setup()  {
    }

/*
    @Test
    public void gettingAllUsers() throws Exception {
        Request mockRequest = new Request.Builder()
                .url("http://google.cz")
                .get()
                .build();

        Response response = new Response.Builder()
                .request(mockRequest)
                .protocol(Protocol.HTTP_2)
                .code(400)
                .message("")
                .build();

        Mockito.when(okHttpClient.newCall(mockRequest).execute()).thenReturn(response);
        serverClient.sendGetUsers();

     //   mockMvc.perform(get("https://vsebug-be.herokuapp.com/users/")).andExpect(status().isOk());

        Mockito.verify(serverClient, Mockito.times(1)).sendGetUsers();
        Assert.assertEquals(response.code(), 200);
    } */


    private Call createResponse(){
        return null;
    }


/*
    Response responseA = ;
    Response responseB = ;

    OkHttpClient okHttpClient = mock(OkHttpClient.class);
    Mockito.
    Call call = mock(Call.class);

    Mockito.when()
    when(call.execute()).thenReturn(responseA).thenReturn(responseB);
    when(okHttpClient.newCall(any(Request.class))).thenReturn(call); */
}
