package org.example.operators;

import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.common.client.module.ParseException;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import com.espertech.esper.runtime.client.UpdateListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.example.engine.EsperEngine;
import org.example.engine.exceptions.NoListenersException;
import org.example.network.mqtt.client.Publisher;
import org.example.network.mqtt.client.Subscriber;
import org.example.operators.exceptions.NoTopicsException;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Operator implemented by Complex Event Processing (CEP) technique
 */
public class CEPOperator {
    private String name = null; // name of operator
    private List<String> subNetwork = null;
    private List<String> pubNetwork = null;
    private String[] subTopics = null;
    private List<String> pubTopics  = new ArrayList<>();
    private Subscriber subscriber = null;
    private Publisher publisher = null;
    private MqttCallback mqttCallback = null;
    private EsperEngine esperEngine = EsperEngine.getInstance();;
    private URL eplResource;
    private Map<String, List<UpdateListener>> statements = new HashMap<>();

    /**
     * Constructor
     * @param name: Operator name
     * @param sub: List of subscribers
     * @param pub: List of publishers
     * @param eplResource: Event Processing Language (EPL) file
     */
    public CEPOperator(String name, List<String> sub, List<String> pub, URL eplResource){
        this.name = name;
        this.subNetwork = sub;
        this.pubNetwork = pub;
        this.eplResource = eplResource;
    }

    /**
     * Set the EPL file
     * @param resource: EPL file
     */
    public void setEplResource(URL resource){
        this.eplResource = resource;
    }

    /**
     * Set the topics that a subscriber expects
     * @param subTopics: Topics which would like to subscribe
     */
    public void setSubTopics(String[] subTopics) {
        this.subTopics = subTopics;
    }

    /**
     * Set the topics that a publisher expects
     * @param pubTopics: Topics which would like to publish
     */
    public void setPubTopics(String[] pubTopics){
        this.pubTopics.addAll(Arrays.asList(pubTopics));
    }

    /**
     * Set the callback of subscriber in MQTT
     * (e.g., when you connect successfully, when message has arrived, etc.)
     * @param mqttCallback: The callback function that you would like.
     */
    public void setCallback(MqttCallback mqttCallback){
        this.mqttCallback = mqttCallback;
    }

    /**
     * Set the EPL statement with listeners
     * @param statementName: Statement name that's set in EPL file
     * @param updateListeners: List of listeners that run when event is detected
     */
    public void setStatement(String statementName, List<UpdateListener> updateListeners){
        statements.put(statementName, updateListeners);
    }

    /**
     * Get operator name
     * @return operator name
     */
    public String getName(){
        return name;
    }

    /**
     * Get subscriber
     * @return subscriber instance
     */
    public Subscriber getSubscriber(){
        try{
            if(subscriber != null){
                return subscriber;
            }else {
                throw new NullPointerException();
            }
        }catch (NullPointerException e){
            System.out.println("Subscriber isn't still set.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get publisher
     * @return publisher instance
     */
    public Publisher getPublisher(){
        try{
            if(publisher != null){
                return publisher;
            }else {
                throw new NullPointerException();
            }
        }catch (NullPointerException e){
            System.out.println("Publisher isn't still set.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get publisher topics
     * @return list of topics that a publisher expects
     */
    public List<String> getPubTopics(){
        try{
            if(pubTopics != null){
                return pubTopics;
            }else {
                throw new NullPointerException();
            }
        }catch (NullPointerException e){
            System.out.println("Publisher's Topic isn't still set.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create a subscriber instance
     * @throws MqttException: MQTT Exception
     */
    public void createSubscriber() throws MqttException{
        subscriber = new Subscriber(String.format("tcp://%s:%s", subNetwork.get(0), subNetwork.get(1)));
    }

    /**
     * Create a publisher instance
     * @throws MqttException: MQTT Exception
     */
    public void createPublisher() throws MqttException{
        publisher = new Publisher(String.format("tcp://%s:%s", pubNetwork.get(0), pubNetwork.get(1)));
    }

    /**
     * Setup the esper configuration
     * @throws EPCompileException: Esper compiler error
     * @throws ParseException: Esper compiler fails to parse the EPL
     * @throws IOException: IO error
     * @throws EPDeployException: Fail to deploy esper compiled module
     */
    private void esperSetup() throws EPCompileException, ParseException,
            IOException, EPDeployException {
        Configuration configuration = new Configuration();
        esperEngine.createCompiler();
        esperEngine.compile(eplResource, configuration);
        esperEngine.createRuntime(configuration);
        esperEngine.deploy();
        try {
            for(String name : statements.keySet()){
                esperEngine.setStatement(name, statements.get(name));
            }
            esperEngine.setListener();
        }catch (NoListenersException ex){
            System.out.println("Listener is not set.");
            ex.printStackTrace();
        }
    }

    /**
     * Setup the whole operator
     * @throws NoTopicsException: Error for no topic is set in operator
     */
    public void setup() throws NoTopicsException {
        if(subTopics.length == 0 || pubTopics.isEmpty()){
            throw new NoTopicsException();
        }else {
            try {
                subscriber.setCallback(mqttCallback);
                esperSetup();
            }catch (EPCompileException | ParseException | IOException | EPDeployException ex){
                System.out.println("Esper Setup Error occurs!!");
                ex.printStackTrace();
            }
        }
    }

    /**
     * Start running the operator
     * @param timerTask: Timer thread
     * @param delay: the delay time that ready to start
     * @param period: the interval time that operator runs
     */
    public void startup(TimerTask timerTask, int delay, int period){
        try {
            subscriber.connect();
            subscriber.subscribe(subTopics);
            publisher.connect();
        }catch (MqttException ex){
            System.out.println("MQTT Connection Error occurs!!!");
            ex.printStackTrace();
        }

        Timer timer = new Timer();
        timer.schedule(timerTask, delay, period);
    }

}
