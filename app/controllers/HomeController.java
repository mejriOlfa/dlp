package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.typesafe.config.Config;

import play.cache.SyncCacheApi;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private final Config config;
    private final SyncCacheApi cache;
    private final WSClient wsClient;

    @Inject
    public HomeController(Config config, SyncCacheApi cache, WSClient wsClient) {
        this.config = config;
        this.cache = cache;
        this.wsClient = wsClient;
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(views.html.index.render());
    }

    /**
     * This is where the test will be coded
     * @return Result
     */
    public Result test() { 
    	cache.set("image.url",config.getInt("randomImg.cache"));
       return ok(views.html.hello.render(this.config.getString("randomImageUrl")));
       }
    
    
    public CompletionStage<Result> testMultiple() throws IOException {
    cache.set("image.url",config.getInt("randomImg.cache"));
    this.mapUrls();
    return  wsClient
    .url(config.getString("randomImgMult.url"))
    .get()
    .thenApply(file -> ok(file.asJson()));

    //return ok(views.html.hello.render(this.config.getString("randomImg.url")));
    }
    
    public void mapUrls() throws IOException {
    	String sURL = config.getString("randomImgMult.url") ; //just a string

    	// Connect to the URL using java's native library
    	URL url = new URL(sURL);
    	URLConnection request = url.openConnection();
    	request.connect();


    	InputStream in =((InputStream) request.getContent());
    	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    	StringBuilder result = new StringBuilder();
    	String line;
    	while((line = reader.readLine()) != null) {
    	result.append(line);
    	}

    	ObjectMapper mapper = new ObjectMapper();


    	ImageMap[] url1 = mapper.readValue(result.toString(), ImageMap[].class);

    	// 2. convert JSON array to List of objects
    	List<ImageMap> ppl2 = Arrays.asList(mapper.readValue(result.toString(), ImageMap[].class));
    	List<ImageMap> l2 = new ArrayList<ImageMap>();
    	for (ImageMap a: ppl2) {
    		if ((a.getId()%2==0)){
    			l2.add(a);
    	System.out.println("****************"+a.getId());
    		}
    	}
    	}

    public Result test() {
    	int id = 0;
    	String url = "";
    	//dto config.get
    	//generating a random id to add it to the url
    	if ( cache.get("imgUrl") == null ){
    	// id = (int)(Math.random() * 900 + 100);
    	Random random = new Random();
    	id=(int) random.nextInt(1085) ;
    	//the complete url
    	this.cache.set("imgUrl",id,this.config.getInt("randomImg.cache"));
    	} else{
    	id = (cache.get("imgUrl"));
    	}
    	url = config.getString("imgUrl") + id+"/200/300" ;

    	System.out.println(url);

    	return ok(views.html.hello.render(url));
    	}
}
