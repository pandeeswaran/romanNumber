package com.realai.realaitv;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MqttHelper {

    public MqttAndroidClient mqttAndroidClient;

    //pubic ip inside chennai office is 192.168.6.105:1883
    //public ip outside chennai office is 183.82.241.246:1883

     // final String serverUri = "tcp://192.168.6.105:1883";
    //  final String serverUri = "tcp://192.168.0.123:1883"; //testing
      final String serverUri = "tcp://183.82.241.246:1883";

    final String subscriptionTopic = "realAI";

    public MqttHelper(Context context, String get_device_id) {
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, get_device_id);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Mqtt", mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        //mqttConnectOptions.setUserName(username);
        //mqttConnectOptions.setPassword(password.toCharArray());

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                   /* DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(false);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);*/
                    subscribeToTopic(subscriptionTopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to: " + serverUri + exception.toString());
                }
            });


        } catch (MqttException | NullPointerException ex) {
            ex.printStackTrace();
        }
    }


    public void subscribeToTopic(String topic) {
        try {
            mqttAndroidClient.subscribe(topic, 2, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt", "Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Subscribed fail!");
                }
            });

        } catch (MqttException | NullPointerException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void unSubscribeMqttChannel() {
        try {
            IMqttToken unsubToken = mqttAndroidClient.unsubscribe(subscriptionTopic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                    Log.e("On Mqtt unSubscribed", "");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                    Log.e("unSubscribe failure", exception.getMessage());
                }
            });
        } catch (MqttException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void disconnectMqtt() {
        try {
            IMqttToken token = mqttAndroidClient.disconnect();
            mqttAndroidClient.unregisterResources();
         //   mqttAndroidClient.close();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.e("On Mqtt disconnected", "");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.e("Mqtt disconnect failure", exception.getMessage());

                }
            });
        } catch (MqttException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void publishConnect(String topic, String input, boolean isRetain) {
        String publishTopic = "realAI/playername";

        if (mqttAndroidClient != null) {
            MqttMessage message = new MqttMessage();
            message.setRetained(false);
            message.setPayload(input.getBytes());
            message.setQos(2);
            try {
                mqttAndroidClient.publish(topic, message);
            } catch (MqttException | NullPointerException e) {
                e.printStackTrace();
            }
        }

    }
}
