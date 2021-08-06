package org.example.network.mqtt.callback;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.network.mqtt.client.Subscriber;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import java.util.Base64;

public class VideoMqttCallback implements MqttCallback {
    private Subscriber subscriber = null;
    private Base64.Decoder decoder = Base64.getDecoder();
    private CascadeClassifier carCascade = new CascadeClassifier();

    public VideoMqttCallback(Subscriber subscriber, String carCascadeFile){
        this.subscriber = subscriber;
        cascadeLoad(carCascadeFile);
    }

    /* Load the classifier model file */
    private void cascadeLoad(String file){
        if (!carCascade.load(file)) {
            System.err.println("--(!)Error loading car cascade: " + file);
            System.exit(0);
        }else {
            System.out.println("cascade file is successful loaded.");
        }
    }

    /* detect the number of car in a frame */
    private void detectCar(Mat frame, CascadeClassifier carCascade){
        MatOfRect cars = new MatOfRect();
        carCascade.detectMultiScale(frame, cars);
        System.out.println(cars.size());
    }

    /* Called when connection is lost */
    public void connectionLost(Throwable throwable) {
        throwable.printStackTrace();
        System.out.println("Connection to MQTT broker lost!");
    }

    /* Called when message is arrived */
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        byte[] img = decoder.decode(mqttMessage.getPayload());
        Mat frame = Imgcodecs.imdecode(new MatOfByte(img), Imgcodecs.IMREAD_UNCHANGED);
        detectCar(frame, carCascade);
        //JSONObject jsonObject = new JSONObject(new String(mqttMessage.getPayload()));
        //System.out.println("Message received:\n\t"+ jsonObject.toString());
        //subscriber.addQueue(jsonObject);
        //System.out.println("Message received:\n\t"+ new String(mqttMessage.getPayload()) );
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // not used in this example
    }
}
