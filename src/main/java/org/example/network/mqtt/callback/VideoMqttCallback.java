package org.example.network.mqtt.callback;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.network.mqtt.client.Subscriber;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import java.util.Base64;
import java.util.List;

/**
 * Video callback in MQTT
 */
public class VideoMqttCallback implements MqttCallback {
    private Subscriber subscriber = null;
    private Base64.Decoder decoder = Base64.getDecoder();
    private CascadeClassifier carCascade = new CascadeClassifier();

    /**
     * Constructor
     * @param subscriber: The subscriber instance
     * @param carCascadeFile: The car classifier model
     */
    public VideoMqttCallback(Subscriber subscriber, String carCascadeFile){
        this.subscriber = subscriber;
        cascadeLoad(carCascadeFile);
    }

    /**
     * Load car classifier model
     * @param file: The classifier model file
     */
    private void cascadeLoad(String file){
        if (!carCascade.load(file)) {
            System.err.println("--(!)Error loading car cascade: " + file);
            System.exit(0);
        }else {
            System.out.println("cascade file is successful loaded.");
        }
    }

    /**
     * Detect the car per frame
     * @param frame: Video frame
     * @param carCascade: Classifier model
     * @return number of cars per frame
     */
    private int detectCar(Mat frame, CascadeClassifier carCascade){
        MatOfRect cars = new MatOfRect();
        // detect the car
        carCascade.detectMultiScale(frame, cars);
        // collect the number of car in a frame
        List<Rect> listOfcars = cars.toList();
        //System.out.println(listOfcars.size());
        return listOfcars.size();
    }

    /**
     * (Callback) When connection is lost, then call
     * @param throwable: Connection lost exception
     */
    public void connectionLost(Throwable throwable) {
        throwable.printStackTrace();
        System.out.println("Connection to MQTT broker lost!");
    }

    /**
     * (Callback) When the message is arrived, then call
     * @param topic: The message maps to which topic
     * @param mqttMessage: The mqtt message
     */
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        byte[] img = decoder.decode(mqttMessage.getPayload());
        Mat frame = Imgcodecs.imdecode(new MatOfByte(img), Imgcodecs.IMREAD_UNCHANGED);
        int carsNumber = detectCar(frame, carCascade);
        JSONObject jsonObject = new JSONObject(String.format("{\"number\":%d}", carsNumber));
        System.out.println("Message received:" + jsonObject);
        subscriber.addQueue(jsonObject);
    }

    /**
     * (Callback) Deliver completely
     * @param iMqttDeliveryToken: The delivery token
     */
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // not used in this example
    }
}
