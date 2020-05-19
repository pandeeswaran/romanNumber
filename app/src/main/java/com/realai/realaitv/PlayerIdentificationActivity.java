package com.realai.realaitv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import utils.BlurTransformation;

import static com.realai.realaitv.BuildConfig.DEBUG;


public class PlayerIdentificationActivity extends Activity {

    MqttHelper mqttHelper;
    private String courtId = "", loginType = "club", matchType = null, setMatchId = "",
            gameSet = "1", matchClassification = null, matchRound = null;
    private JSONObject playerInfo = new JSONObject();
    JSONArray playerArray = new JSONArray();
    TextView tvPlayerName, tvPlayerAI,
            score_tv_playerName1, score_tv_playerName2, score_tv_playerName3, score_tv_playerName4,
            tvScoreDisplay1, tvScoreDisplay2, tvScoreDisplay3, tvScoreDisplay4, tvScoreDisplay5, tvScoreDisplay6,
            tvNearDoublePlayerName1, tvNearDoublePlayerName2, tvFarDoublePlayerName1, tvFarDoublePlayerName2,
            tvNearDoublePlayerPerformance1, tvNearDoublePlayerPerformance2, tvFarDoublePlayerPerformance1, tvFarDoublePlayerPerformance2,
            getTvTimer1, getTvTimer2, getTvTimer3, /*Two player detect*/
            tvPlayerIdentificationName, tvPlayerIdentificationAIName,/*SingleSetPLayerContainer*/
            tvSingleSetPlayerName1, tvSingleSetPlayerName2, tv_single_vs_player, tv_double_vs_player, tv_single_set_timer,
            tvSingleSetDoublePlayerFar1, tvSingleSetDoublePlayerFar2,
            tvSingleSetDoublePlayerNear1, tvSingleSetDoublePlayerNear2,
            tvSingleSetScore1, tvSingleSetScore2, /*Training Player Name*/
            tvTrainingPlayerName1, tvTrainingPlayerName2, tvTrainingPlayerName3, tvTrainingPlayerName4,
            tvTrainingPlayerName5, tvTrainingPlayerName6, tvTrainingPlayerName7, tvTrainingPlayerName8,
            tvNearSinglePlayerName, tvFarSinglePlayerName, tvWinMessage,
            tv_three_single_far_player_name, tv_three_single_near_player_name, tv_double_score_vs_player, tv_single_score_vs_player;

    CircleImageView profile_image, profile_double_player_image,
            score_ci_player1, score_ci_player2, score_ci_player3, score_ci_player4,
            ciNearSinglePlayer, ciFarSinglePlayer,/*SingleSetPLayerContainer*/
            ciSingleSetPlayerImage1, ciSingleSetPlayerImage2,
            ciSingleSetDoublePlayerFar1, ciSingleSetDoublePlayerFar2,
            ciSingleSetDoublePlayerNear1, ciSingleSetDoublePlayerNear2,/*Training Player Name*/
            ci_training_player_bg_image1,
            ciTrainingPlayerImage1, ciTrainingPlayerImage2, ciTrainingPlayerImage3, ciTrainingPlayerImage4,
            ciTrainingPlayerImage5, ciTrainingPlayerImage6, ciTrainingPlayerImage7, ciTrainingPlayerImage8,
            ciNearDoublePlayer1, ciNearDoublePlayer2, ciFarDoublePlayer1, ciFarDoublePlayer2,
            ci_three_single_far_player, ci_three_single_near_player;

    MaterialLetterIcon material_icon;

    RelativeLayout ll_single_player_container, ll_score_container, llEditImageContainer, ll_identification_player_container,
            llContentSingle_player, llContentDouble_player, llContainerTraining,
            rlWinStatusContainer, llSingleSetPlayerContainer,
            rlSingleSetDoublePlayerFar, rlSingleSetPlayerFar,
            rlSingleSetDoublePlayerNear, rlSingleSetPlayerNear,
            rl_three_double_player_far_container, rl_three_double_player_near_container;

    LinearLayout llDecisionContainer, llTrainingParentContainer, llTrainingParentContainer1, llTrainingParentContainer2,
            llTrainingPlayerContainer1, llTrainingPlayerContainer2, llTrainingPlayerContainer3, llTrainingPlayerContainer4,
            llTrainingPlayerContainer5, llTrainingPlayerContainer6, llTrainingPlayerContainer7, llTrainingPlayerContainer8,
            llContentDoubleNearContainer1, llContentDoubleNearContainer2, llContentDoubleFarContainer1, llContentDoubleFarContainer2,
            rl_three_single_player_far_container, rl_three_single_player_near_container;

    VerticalGridView verticalGridView;
    TrainingPlayerAdapter trainingPlayerAdapter;
    ImageView imgEditedView;

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    JSONArray removeOidList = new JSONArray();
    boolean isThreadRun = true;

    private int mScrollState = RecyclerView.SCROLL_STATE_IDLE;
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (DEBUG) {
                final String[] stateNames = {"IDLE", "DRAGGING", "SETTLING"};
                Log.v("Scroll", "onScrollStateChanged "
                        + (newState < stateNames.length ? stateNames[newState] : newState));
            }
            mScrollState = newState;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_identification);

        if (getIntent().getStringExtra("courtId") != null) {
            courtId = getIntent().getStringExtra("courtId");
            Log.e("courtId", courtId);
        }

        if (getIntent().getStringExtra("loginType") != null) {
            loginType = getIntent().getStringExtra("loginType");
            Log.e("loginType", loginType);
        }

        llEditImageContainer = findViewById(R.id.ll_edit_image_container);
        rlWinStatusContainer = findViewById(R.id.rl_win_status_container);
        imgEditedView = findViewById(R.id.img_edited_image);
        tvWinMessage = findViewById(R.id.tv_win_message);

        setTrainingView();
        setSecondPlayerView();
        setSinglePlayerView();
        initScoreBoardContainer();
        setSinglePlayerContainer();
        setDoublePlayerContainer();
        singleSetPlayerScoreContainer();
        trainingPlayerViewInitization();
        hideAllContainerView();
        startMqtt();
    }

    private void trainingPlayerViewInitization() {
        tvTrainingPlayerName1 = findViewById(R.id.tv_training_player_name1);
        tvTrainingPlayerName2 = findViewById(R.id.tv_training_player_name2);
        tvTrainingPlayerName3 = findViewById(R.id.tv_training_player_name3);
        tvTrainingPlayerName4 = findViewById(R.id.tv_training_player_name4);
        tvTrainingPlayerName5 = findViewById(R.id.tv_training_player_name5);
        tvTrainingPlayerName6 = findViewById(R.id.tv_training_player_name6);
        tvTrainingPlayerName7 = findViewById(R.id.tv_training_player_name7);
        tvTrainingPlayerName8 = findViewById(R.id.tv_training_player_name8);

        ciTrainingPlayerImage1 = findViewById(R.id.ci_training_player_image1);
        //   ci_training_player_bg_image1 = findViewById(R.id.ci_training_player_bg_image1);
        ciTrainingPlayerImage2 = findViewById(R.id.ci_training_player_image2);
        ciTrainingPlayerImage3 = findViewById(R.id.ci_training_player_image3);
        ciTrainingPlayerImage4 = findViewById(R.id.ci_training_player_image4);
        ciTrainingPlayerImage5 = findViewById(R.id.ci_training_player_image5);
        ciTrainingPlayerImage6 = findViewById(R.id.ci_training_player_image6);
        ciTrainingPlayerImage7 = findViewById(R.id.ci_training_player_image7);
        ciTrainingPlayerImage8 = findViewById(R.id.ci_training_player_image8);

        llTrainingParentContainer = findViewById(R.id.ll_training_parent_container);
        llTrainingParentContainer1 = findViewById(R.id.ll_training_parent_container1);
        llTrainingParentContainer2 = findViewById(R.id.ll_training_parent_container2);

        llTrainingPlayerContainer1 = findViewById(R.id.ll_training_player_container1);
        llTrainingPlayerContainer2 = findViewById(R.id.ll_training_player_container2);
        llTrainingPlayerContainer3 = findViewById(R.id.ll_training_player_container3);
        llTrainingPlayerContainer4 = findViewById(R.id.ll_training_player_container4);
        llTrainingPlayerContainer5 = findViewById(R.id.ll_training_player_container5);
        llTrainingPlayerContainer6 = findViewById(R.id.ll_training_player_container6);
        llTrainingPlayerContainer7 = findViewById(R.id.ll_training_player_container7);
        llTrainingPlayerContainer8 = findViewById(R.id.ll_training_player_container8);
    }

    private void hideTrainingChildView() {
        llTrainingParentContainer1.setVisibility(View.GONE);
        llTrainingParentContainer2.setVisibility(View.GONE);
        llTrainingPlayerContainer1.setVisibility(View.GONE);
        llTrainingPlayerContainer2.setVisibility(View.GONE);
        llTrainingPlayerContainer3.setVisibility(View.GONE);
        llTrainingPlayerContainer4.setVisibility(View.GONE);
        llTrainingPlayerContainer5.setVisibility(View.GONE);
        llTrainingPlayerContainer6.setVisibility(View.GONE);
        llTrainingPlayerContainer7.setVisibility(View.GONE);
        llTrainingPlayerContainer8.setVisibility(View.GONE);
    }

    private void setTrainingView() {
        llContainerTraining = findViewById(R.id.ll_container_training);
        llContainerTraining.setVisibility(View.GONE);
        verticalGridView = findViewById(R.id.ll_vertical_view);
        verticalGridView.setNumColumns(4);
        verticalGridView.setWindowAlignment(VerticalGridView.WINDOW_ALIGN_BOTH_EDGE);
        verticalGridView.setWindowAlignmentOffsetPercent(35);
        verticalGridView.setOnScrollListener(mScrollListener);
        //verticalGridView.setAdapter(trainingPlayerAdapter);
    }

    private void setSinglePlayerContainer() {
        llContentSingle_player = findViewById(R.id.ll_content_single_player);
        llContentSingle_player.setVisibility(View.GONE);
        ciNearSinglePlayer = findViewById(R.id.ci_near_single_player);
        ciFarSinglePlayer = findViewById(R.id.ci_far_single_player);
        tvNearSinglePlayerName = findViewById(R.id.tv_near_single_player_name);
        tvFarSinglePlayerName = findViewById(R.id.tv_far_single_player_name);
    }

    private void setDoublePlayerContainer() {
        llContentDouble_player = findViewById(R.id.ll_content_double_player);
        llContentDouble_player.setVisibility(View.GONE);
        llDecisionContainer = findViewById(R.id.ll_decision_container);
        llContentDoubleNearContainer1 = findViewById(R.id.ll_content_double_near_container1);
        llContentDoubleNearContainer2 = findViewById(R.id.ll_content_double_near_container2);
        llContentDoubleFarContainer1 = findViewById(R.id.ll_content_double_far_container1);
        llContentDoubleFarContainer2 = findViewById(R.id.ll_content_double_far_container2);
        ciNearDoublePlayer1 = findViewById(R.id.ci_near_double_player1);
        ciNearDoublePlayer2 = findViewById(R.id.ci_near_double_player2);
        ciFarDoublePlayer1 = findViewById(R.id.ci_far_double_player1);
        ciFarDoublePlayer2 = findViewById(R.id.ci_far_double_player2);
        tvNearDoublePlayerPerformance1 = findViewById(R.id.tv_near_double_player_performance1);
        tvNearDoublePlayerPerformance2 = findViewById(R.id.tv_near_double_player_performance2);
        tvFarDoublePlayerPerformance1 = findViewById(R.id.tv_far_double_player_performance1);
        tvFarDoublePlayerPerformance2 = findViewById(R.id.tv_far_double_player_performance2);
        tvNearDoublePlayerName1 = findViewById(R.id.tv_near_double_player_name1);
        tvNearDoublePlayerName2 = findViewById(R.id.tv_near_double_player_name2);
        tvFarDoublePlayerName1 = findViewById(R.id.tv_far_double_player_name1);
        tvFarDoublePlayerName2 = findViewById(R.id.tv_far_double_player_name2);
    }

    private void setSinglePlayerView() {
        ll_single_player_container = findViewById(R.id.ll_single_player_container);
        ll_single_player_container.setVisibility(View.GONE);
        tvPlayerName = findViewById(R.id.tv_player_name);
        tvPlayerAI = findViewById(R.id.tv_player_ai);
        profile_image = findViewById(R.id.profile_image);
        material_icon = findViewById(R.id.material_icon);
        material_icon.setVisibility(View.GONE);
    }

    private void setSecondPlayerView() {
        ll_identification_player_container = findViewById(R.id.ll_identification_player_container);
        ll_identification_player_container.setVisibility(View.GONE);
        tvPlayerIdentificationName = findViewById(R.id.tv_identification_name);
        tvPlayerIdentificationAIName = findViewById(R.id.tv_identification_ai);
        profile_double_player_image = findViewById(R.id.profile_double_player_image);
    }

    private void initScoreBoardContainer() {
        ll_score_container = findViewById(R.id.ll_score_container);
        ll_score_container.setVisibility(View.GONE);
        score_tv_playerName1 = findViewById(R.id.tv_player1);
        score_tv_playerName2 = findViewById(R.id.tv_player2);
        score_tv_playerName3 = findViewById(R.id.tv_player3);
        score_tv_playerName4 = findViewById(R.id.tv_player4);
        tv_single_score_vs_player = findViewById(R.id.tv_single_score_vs_player);
        tv_double_score_vs_player = findViewById(R.id.tv_double_score_vs_player);
        score_ci_player1 = findViewById(R.id.ci_player1);
        score_ci_player2 = findViewById(R.id.ci_player2);
        score_ci_player3 = findViewById(R.id.ci_player3);
        score_ci_player4 = findViewById(R.id.ci_player4);
        tvScoreDisplay1 = findViewById(R.id.tv_score1);
        tvScoreDisplay2 = findViewById(R.id.tv_score2);
        tvScoreDisplay3 = findViewById(R.id.tv_score3);
        tvScoreDisplay4 = findViewById(R.id.tv_score4);
        tvScoreDisplay5 = findViewById(R.id.tv_score5);
        tvScoreDisplay6 = findViewById(R.id.tv_score6);
        getTvTimer1 = findViewById(R.id.tv_timer1);
        getTvTimer2 = findViewById(R.id.tv_timer2);
        getTvTimer3 = findViewById(R.id.tv_timer3);

        rl_three_double_player_far_container = findViewById(R.id.rl_three_double_player_far_container);
        rl_three_double_player_near_container = findViewById(R.id.rl_three_double_player_near_container);
        rl_three_single_player_far_container = findViewById(R.id.rl_three_single_player_far_container);
        rl_three_single_player_near_container = findViewById(R.id.rl_three_single_player_near_container);

        ci_three_single_far_player = findViewById(R.id.ci_three_single_far_player);
        ci_three_single_near_player = findViewById(R.id.ci_three_single_near_player);

        tv_three_single_far_player_name = findViewById(R.id.tv_three_single_far_player_name);
        tv_three_single_near_player_name = findViewById(R.id.tv_three_single_near_player_name);
    }

    private void startMqtt() {
        mqttHelper = new MqttHelper(getApplicationContext(), Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.e("String", s);
                mqttHelper.subscribeToTopic(Constants.REAL_AI_LOGIN_TYPE + courtId);
                mqttHelper.subscribeToTopic(Constants.REAL_AI_BADMINTON_COURT_ID + courtId);
                mqttHelper.subscribeToTopic(Constants.REAL_AI_BADMINTON_PLAYERS_INFO + courtId);
                mqttHelper.subscribeToTopic(Constants.REAL_AI_BADMINTON_DETAILS + courtId);
                mqttHelper.subscribeToTopic(Constants.REAL_AI_BADMINTON_MATCH_SCORE_STATUS + courtId);
                mqttHelper.subscribeToTopic(Constants.REAL_AI_BADMINTON_ROUND_STATUS + courtId);
                mqttHelper.subscribeToTopic(Constants.REAL_AI_BADMINTON_MATCH_STATUS_ACK + courtId);
                mqttHelper.subscribeToTopic(Constants.REAL_AI_BADMINTON_MATCH_SETTINGS_ACK + courtId);
                mqttHelper.subscribeToTopic(Constants.REAL_AI_BADMINTON_TEAM_INFO + courtId);
                mqttHelper.subscribeToTopic(Constants.REAL_AI_GET_VIDEO_PATH + courtId);
                mqttHelper.subscribeToTopic(Constants.REAL_AI_GET_TRAINING_VIDEO_PATH + courtId);
                mqttHelper.subscribeToTopic(Constants.REAL_AI_EDITED_IMAGE + courtId);
                mqttHelper.subscribeToTopic(Constants.REAL_AI_EDITED_IMAGE_ACK + courtId);
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.e("connectionLost", "connectionLost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.e("mqttMessage", mqttMessage.toString());
                JSONObject object = new JSONObject(new String(mqttMessage.getPayload()));
                playerInfo = new JSONObject();
                if (topic.equalsIgnoreCase(Constants.REAL_AI_LOGIN_TYPE + courtId)) {
                    loginType = object.optString("loginType");
                    if (object.optString("loginStatus").equalsIgnoreCase("signout")) {
                        mqttHelper.unSubscribeMqttChannel();
                        mqttHelper.disconnectMqtt();
                        mqttHelper = null;
                        startActivity(new Intent(PlayerIdentificationActivity.this, SplashActivity.class));
                        finish();
                    }
                    hideAllContainerView();
                } else if (topic.equals(Constants.REAL_AI_BADMINTON_PLAYERS_INFO + courtId)) {
                    try {
                        // rightAnimation(ll_single_player_container);
                        playerInfo.put("playerOid", object.optString("playerId"));
                        playerInfo.put("playerName", object.optString("playerName"));
                        playerInfo.put("profileImageUrl", object.optString("profileImageUrl"));
                        playerInfo.put("performanceThumbNail", object.optString("performanceThumbNail"));
                        if (!setMatchId.equalsIgnoreCase("")) {
                            Log.e("removeOidList1456", "" + removeOidList);
                            for (int i = 0; i < removeOidList.length(); i++) {
                                if (removeOidList.opt(i) != null && removeOidList.getJSONObject(i).optString("playerOid").equals(object.optString("playerId"))) {
                                    playerInfo.put("playerPosition", removeOidList.getJSONObject(i).optString("playerPosition"));
                                }
                            }
                        }
                        playerArray.put(playerInfo);
                        Log.e("before", "" + playerArray);
                        removeDuplicateArray();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (topic.equals(Constants.REAL_AI_GET_VIDEO_PATH + courtId) || topic.equals(Constants.REAL_AI_GET_TRAINING_VIDEO_PATH + courtId)) {
                    startActivity(new Intent(PlayerIdentificationActivity.this, ReplayVideoActivity.class)
                            .putExtra("REPLAY_URL", object.optString("replayUrl")));
                } else if (topic.equals(Constants.REAL_AI_BADMINTON_MATCH_STATUS_ACK + courtId)) {
                    if (object.optString("matchStatus").equalsIgnoreCase("END") ||
                            object.optString("matchStatus").equalsIgnoreCase("SUSPENDED")) {
                        isThreadRun = false;
                        if (!matchClassification.equalsIgnoreCase("training")) {
                            mqttHelper.unSubscribeMqttChannel();
                            mqttHelper.disconnectMqtt();
                            mqttHelper = null;
                            startActivity(new Intent(PlayerIdentificationActivity.this, SplashActivity.class));
                            finish();
                        }
                    } else if (object.optString("matchStatus").equalsIgnoreCase("INPROGRESS")) {
                        for (int i = 0; i < playerArray.length(); i++) {
                            String removeOid = object.optString("removeId");
                            if (playerArray.optJSONObject(i).optString("playerOid").equals(removeOid)) {
                                removeOidList.put(i, playerArray.optJSONObject(i));
                                Log.e("removeOidList123", "" + removeOidList);
                                playerArray.remove(i);
                                if (playerArray != null && !matchClassification.equalsIgnoreCase("training")) {
                                    if (matchRound.equalsIgnoreCase("1")) {
                                        enableSingleSetPlayerScoreBoard();
                                    } else {
                                        enableThreeSetScoreBoard();
                                    }
                                }
                            }
                        }
                    } else if (object.has("playerList")) {
                        Log.e("topice", "" + object.optJSONArray("playerList"));
                        playerArray = new JSONArray();
                        playerArray = object.optJSONArray("playerList");
                        Log.e("playerArray", "" + playerArray);
                        animationMatchConvertedPlayers();

                    }
                } else if (topic.equals(Constants.REAL_AI_EDITED_IMAGE + courtId)) {
                    //hideAllContainerView();
                    Log.e("edited Image", object.optString("url"));
                    llEditImageContainer.setVisibility(View.VISIBLE);
                    imgEditedView.setImageDrawable(null);
                    /*imgEditedView.setImageResource(0);
                    imgEditedView.setBackgroundResource(0);*/
                    Glide.with(PlayerIdentificationActivity.this)
                            .load(object.optString("url"))
                            .error(R.drawable.default_background).into(imgEditedView);
                } else if (topic.equals(Constants.REAL_AI_EDITED_IMAGE_ACK + courtId)) {
                    //hideAllContainerView();
                    imgEditedView.setImageDrawable(null);
                    llEditImageContainer.setVisibility(View.GONE);
                } else if (topic.equalsIgnoreCase(Constants.REAL_AI_BADMINTON_MATCH_SETTINGS_ACK + courtId)) {
                    Log.e("MATCH_SETTINGS", "" + object);
                    matchType = object.optString("matchType");
                    matchRound = object.optString("gameSet");
                    if (!matchClassification.equalsIgnoreCase("training")) {
                        resetDecisionTextView();
                        resetDecisionContainer();
                    }
                } else if (topic.equalsIgnoreCase(Constants.REAL_AI_BADMINTON_MATCH_SCORE_STATUS + courtId)) {
                    Log.e("MATCH_SCORE_STATUS", "" + object);
                    setScoreCardInformation(object);
                } else if (topic.equalsIgnoreCase(Constants.REAL_AI_BADMINTON_DETAILS + courtId)) {
                    Log.e(" details", "badminton details");
                    Log.e(" details", "" + object);
                    matchClassification = object.optString("matchClassification");
                    setMatchId = object.optString("match");
                    isThreadRun = true;
                    if (object.optString("matchClassification").equalsIgnoreCase("training")) {
                    } else {
                        JsonParser parser = new JsonParser();
                        JsonArray jsonArray = parser.parse(object.optString("playerInfo")).getAsJsonArray();
                        Log.e("jsonArray", "" + jsonArray);
                        playerArray = new JSONArray(object.optString("playerInfo"));
                        Log.e("playerArray", "" + playerArray);
                    }
                } else if (topic.equalsIgnoreCase(Constants.REAL_AI_BADMINTON_ROUND_STATUS + courtId)) {
                    disabledScoreCard(object);
                } else if (topic.equalsIgnoreCase(Constants.REAL_AI_BADMINTON_TEAM_INFO + courtId)) {
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.w("Debug", "deliveryComplete");
            }
        });
    }

    private void resetDecisionTextView() {
        tvNearDoublePlayerName1.setText("");
        tvNearDoublePlayerName2.setText("");
        tvNearDoublePlayerPerformance1.setText("");
        tvNearDoublePlayerPerformance2.setText("");
        tvFarDoublePlayerName1.setText("");
        tvFarDoublePlayerName2.setText("");
        tvFarDoublePlayerPerformance1.setText("");
        tvFarDoublePlayerPerformance2.setText("");
    }

    private void resetDecisionContainer() {
        llDecisionContainer.setVisibility(View.VISIBLE);
        llContentDoubleNearContainer1.setVisibility(View.GONE);
        llContentDoubleNearContainer2.setVisibility(View.GONE);
        llContentDoubleFarContainer1.setVisibility(View.GONE);
        llContentDoubleFarContainer2.setVisibility(View.GONE);
        if (playerArray != null) {
            for (int k = 0; k < playerArray.length(); k++) {
                switch (playerArray.optJSONObject(k).optString("playerPosition")) {
                    case "near":
                        if (tvNearDoublePlayerName1.getText().toString().trim().isEmpty()) {
                            llContentDoubleNearContainer1.setVisibility(View.VISIBLE);
                            tvNearDoublePlayerName1.setText(playerArray.optJSONObject(k).optString("playerName"));
                            tvNearDoublePlayerPerformance1.setText(playerArray.optJSONObject(k).optString("performanceThumbNail"));
                            updateProfilePicture(playerArray.optJSONObject(k).optString("profileImageUrl"), ciNearDoublePlayer1);
                        } else {
                            llContentDoubleNearContainer2.setVisibility(View.VISIBLE);
                            tvNearDoublePlayerName2.setText(playerArray.optJSONObject(k).optString("playerName"));
                            tvNearDoublePlayerPerformance2.setText(playerArray.optJSONObject(k).optString("performanceThumbNail"));
                            updateProfilePicture(playerArray.optJSONObject(k).optString("profileImageUrl"), ciNearDoublePlayer2);
                        }
                        break;
                    case "far":
                        if (tvFarDoublePlayerName1.getText().toString().isEmpty()) {
                            llContentDoubleFarContainer1.setVisibility(View.VISIBLE);
                            tvFarDoublePlayerName1.setText(playerArray.optJSONObject(k).optString("playerName"));
                            tvFarDoublePlayerPerformance1.setText(playerArray.optJSONObject(k).optString("performanceThumbNail"));
                            updateProfilePicture(playerArray.optJSONObject(k).optString("profileImageUrl"), ciFarDoublePlayer1);
                        } else {
                            tvFarDoublePlayerName2.setText(playerArray.optJSONObject(k).optString("playerName"));
                            tvFarDoublePlayerPerformance2.setText(playerArray.optJSONObject(k).optString("performanceThumbNail"));
                            llContentDoubleFarContainer2.setVisibility(View.VISIBLE);
                            updateProfilePicture(playerArray.optJSONObject(k).optString("profileImageUrl"), ciFarDoublePlayer2);
                        }
                        break;
                }
            }
        }

        View layout;

        if (matchClassification.equals("training")) {
            layout = llTrainingParentContainer;
        } else {
            if (ll_identification_player_container.getVisibility() == View.VISIBLE) {
                layout = ll_identification_player_container;
            } else {
                layout = llContentDouble_player;
            }
        }


        layout.animate()
                .alpha(0f)
                .setDuration(5000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        hideAllContainerView();
                        if (matchRound.equalsIgnoreCase("1")) {
                            enableSingleSetPlayerScoreBoard();
                        } else {
                            gameSet = "1";
                            enableThreeSetScoreBoard();
                        }
                    }
                });
    }

    private void setScoreCardInformation(JSONObject object) {
        gameSet = String.valueOf(object.optInt("gameSet"));

        if (rlWinStatusContainer.getVisibility() == View.VISIBLE) {
            rlWinStatusContainer.setVisibility(View.GONE);
        }
        if (matchRound.equalsIgnoreCase("1")) {
            tvSingleSetScore1.setText(object.optString("teamBScore"));
            tvSingleSetScore2.setText(object.optString("teamAScore"));
        } else {
            if (object.optInt("gameSet") == 1) {
                tvScoreDisplay1.setText(object.optString("teamBScore"));
                tvScoreDisplay4.setText(object.optString("teamAScore"));
            } else if (object.optInt("gameSet") == 2) {
                tvScoreDisplay2.setText(object.optString("teamBScore"));
                tvScoreDisplay5.setText(object.optString("teamAScore"));
            } else {
                tvScoreDisplay3.setText(object.optString("teamBScore"));
                tvScoreDisplay6.setText(object.optString("teamAScore"));
            }
        }
    }

    private void disabledScoreCard(JSONObject object) {
        gameSet = object.optString("gameSet");
        startTime = SystemClock.uptimeMillis();
        if (object.optInt("setCount") == 1) {
            if (object.optString("winTeam").equals("Team B")) {
                tvScoreDisplay1.setTextColor(getResources().getColor(R.color.win_text));
                tvSingleSetScore1.setTextColor(getResources().getColor(R.color.win_text));
            } else {
                tvScoreDisplay4.setTextColor(getResources().getColor(R.color.win_text));
                tvSingleSetScore2.setTextColor(getResources().getColor(R.color.win_text));
            }
        } else if (object.optInt("setCount") == 2) {
            if (object.optString("winTeam").equals("Team B")) {
                tvScoreDisplay2.setTextColor(getResources().getColor(R.color.win_text));
            } else {
                tvScoreDisplay5.setTextColor(getResources().getColor(R.color.win_text));
            }
        } else if (object.optInt("setCount") == 3) {
            if (object.optString("winTeam").equals("Team B")) {
                tvScoreDisplay3.setTextColor(getResources().getColor(R.color.win_text));
            } else {
                tvScoreDisplay6.setTextColor(getResources().getColor(R.color.win_text));
            }
        }

        if (!object.optBoolean("nextSetExists")) {
            isThreadRun = false;
            if (matchRound.equalsIgnoreCase("1")) {

            } else {

            }
            // hideAllContainerView();
           /* rlWinStatusContainer.setVisibility(View.VISIBLE);
            tvWinMessage.setText(object.optString("finalWinTeamName") + " won the match");
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.applause10);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                Log.e("Win Status", "called");
            });*/
        }
    }

    private void enableTvScoreDisplay() {
        tvScoreDisplay1.setVisibility(View.VISIBLE);
        tvScoreDisplay2.setVisibility(View.VISIBLE);
        tvScoreDisplay3.setVisibility(View.VISIBLE);
        tvScoreDisplay4.setVisibility(View.VISIBLE);
        tvScoreDisplay5.setVisibility(View.VISIBLE);
        tvScoreDisplay6.setVisibility(View.VISIBLE);
        getTvTimer1.setVisibility(View.VISIBLE);
        getTvTimer2.setVisibility(View.VISIBLE);
        getTvTimer3.setVisibility(View.VISIBLE);
    }

    private void singleSetPlayerScoreContainer() {
        tvSingleSetPlayerName1 = findViewById(R.id.tv_single_set_player_name1);
        tvSingleSetPlayerName2 = findViewById(R.id.tv_single_set_player_name2);
        ciSingleSetPlayerImage1 = findViewById(R.id.ci_single_set_player1);
        ciSingleSetPlayerImage2 = findViewById(R.id.ci_single_set_player2);
        llSingleSetPlayerContainer = findViewById(R.id.ll_single_set_player_container);
        rlSingleSetDoublePlayerNear = findViewById(R.id.rl_single_set_double_player_near);
        rlSingleSetDoublePlayerFar = findViewById(R.id.rl_single_set_double_player_far);
        rlSingleSetPlayerNear = findViewById(R.id.rl_single_set_player_near);
        rlSingleSetPlayerFar = findViewById(R.id.rl_single_set_player_far);

        tvSingleSetScore1 = findViewById(R.id.tv_single_set_score1);
        tvSingleSetScore2 = findViewById(R.id.tv_single_set_score2);

        tv_single_set_timer = findViewById(R.id.tv_single_set_timer);
        tv_single_vs_player = findViewById(R.id.tv_single_vs_player);
        tv_double_vs_player = findViewById(R.id.tv_double_vs_player);

        /*Double Player CircleImageView*/
        ciSingleSetDoublePlayerFar1 = findViewById(R.id.ci_single_set_double_player_far1);
        ciSingleSetDoublePlayerFar2 = findViewById(R.id.ci_single_set_double_player_far2);
        ciSingleSetDoublePlayerNear1 = findViewById(R.id.ci_single_set_double_player_near1);
        ciSingleSetDoublePlayerNear2 = findViewById(R.id.ci_single_set_double_player_near2);
        /*Double Player TextView*/
        tvSingleSetDoublePlayerFar1 = findViewById(R.id.tv_single_set_double_player_far1);
        tvSingleSetDoublePlayerFar2 = findViewById(R.id.tv_single_set_double_player_far2);
        tvSingleSetDoublePlayerNear1 = findViewById(R.id.tv_single_set_double_player_near1);
        tvSingleSetDoublePlayerNear2 = findViewById(R.id.tv_single_set_double_player_near2);
    }

    private void enableDisableSingleSetScoreBoard() {

        llSingleSetPlayerContainer.setVisibility(View.VISIBLE);
        rlSingleSetPlayerNear.setVisibility(View.GONE);
        rlSingleSetDoublePlayerNear.setVisibility(View.GONE);
        rlSingleSetPlayerFar.setVisibility(View.GONE);
        rlSingleSetDoublePlayerFar.setVisibility(View.GONE);
        tv_single_vs_player.setVisibility(View.GONE);
        tv_double_vs_player.setVisibility(View.GONE);

        ciSingleSetDoublePlayerNear1.setVisibility(View.GONE);
        ciSingleSetDoublePlayerNear2.setVisibility(View.GONE);
        ciSingleSetDoublePlayerFar1.setVisibility(View.GONE);
        ciSingleSetDoublePlayerFar2.setVisibility(View.GONE);
        tvSingleSetDoublePlayerNear1.setVisibility(View.GONE);
        tvSingleSetDoublePlayerNear2.setVisibility(View.GONE);
        tvSingleSetDoublePlayerFar1.setVisibility(View.GONE);
        tvSingleSetDoublePlayerFar2.setVisibility(View.GONE);

        tvSingleSetDoublePlayerNear1.setText("");
        tvSingleSetDoublePlayerNear2.setText("");
        tvSingleSetDoublePlayerFar1.setText("");
        tvSingleSetDoublePlayerFar2.setText("");
    }

    private void enableSingleSetPlayerScoreBoard() {

        if (removeOidList.length() == 0) {
            hideAllContainerView();
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
        }

        enableDisableSingleSetScoreBoard();

        if (playerArray != null) {
            for (int i = 0; i < playerArray.length(); i++) {
                Log.e("AAA", playerArray.optJSONObject(i).optString("playerPosition"));
                switch (playerArray.optJSONObject(i).optString("playerPosition")) {
                    case "near":
                        if (matchType.equalsIgnoreCase("singles")) {
                            rlSingleSetPlayerNear.setVisibility(View.VISIBLE);
                            tv_single_vs_player.setVisibility(View.VISIBLE);
                            Glide.with(this)
                                    .load(playerArray.optJSONObject(i).optString("profileImageUrl"))
                                    .error(R.drawable.default_background).into(ciSingleSetPlayerImage2);
                            tvSingleSetPlayerName2.setText(playerArray.optJSONObject(i).optString("playerName"));

                        } else {
                            rlSingleSetDoublePlayerNear.setVisibility(View.VISIBLE);
                            tv_double_vs_player.setVisibility(View.VISIBLE);
                            if (tvSingleSetDoublePlayerNear1.getText().toString().trim().isEmpty()) {
                                ciSingleSetDoublePlayerNear1.setVisibility(View.VISIBLE);
                                tvSingleSetDoublePlayerNear1.setVisibility(View.VISIBLE);
                                Glide.with(this)
                                        .load(playerArray.optJSONObject(i).optString("profileImageUrl"))
                                        .error(R.drawable.default_background).into(ciSingleSetDoublePlayerNear1);
                                tvSingleSetDoublePlayerNear1.setText(playerArray.optJSONObject(i).optString("playerName"));
                            } else {
                                ciSingleSetDoublePlayerNear2.setVisibility(View.VISIBLE);
                                tvSingleSetDoublePlayerNear2.setVisibility(View.VISIBLE);
                                Glide.with(this)
                                        .load(playerArray.optJSONObject(i).optString("profileImageUrl"))
                                        .error(R.drawable.default_background).into(ciSingleSetDoublePlayerNear2);
                                tvSingleSetDoublePlayerNear2.setText(playerArray.optJSONObject(i).optString("playerName"));
                            }
                        }
                        break;
                    case "far":
                        if (matchType.equalsIgnoreCase("singles")) {
                            rlSingleSetPlayerFar.setVisibility(View.VISIBLE);
                            tv_single_vs_player.setVisibility(View.VISIBLE);
                            Glide.with(this)
                                    .load(playerArray.optJSONObject(i).optString("profileImageUrl"))
                                    .error(R.drawable.default_background).into(ciSingleSetPlayerImage1);
                            tvSingleSetPlayerName1.setText(playerArray.optJSONObject(i).optString("playerName"));
                        } else {
                            rlSingleSetDoublePlayerFar.setVisibility(View.VISIBLE);
                            tv_double_vs_player.setVisibility(View.VISIBLE);
                            if (tvSingleSetDoublePlayerFar1.getText().toString().trim().isEmpty()) {
                                ciSingleSetDoublePlayerFar1.setVisibility(View.VISIBLE);
                                tvSingleSetDoublePlayerFar1.setVisibility(View.VISIBLE);
                                Glide.with(this)
                                        .load(playerArray.optJSONObject(i).optString("profileImageUrl"))
                                        .error(R.drawable.default_background).into(ciSingleSetDoublePlayerFar1);
                                tvSingleSetDoublePlayerFar1.setText(playerArray.optJSONObject(i).optString("playerName"));
                            } else {
                                ciSingleSetDoublePlayerFar2.setVisibility(View.VISIBLE);
                                tvSingleSetDoublePlayerFar2.setVisibility(View.VISIBLE);
                                Glide.with(this)
                                        .load(playerArray.optJSONObject(i).optString("profileImageUrl"))
                                        .error(R.drawable.default_background).into(ciSingleSetDoublePlayerFar2);
                                tvSingleSetDoublePlayerFar2.setText(playerArray.optJSONObject(i).optString("playerName"));
                            }
                        }
                        break;
                }
            }
        }

        //  if(setMatchId.equalsIgnoreCase("")) {
        //  startTime = SystemClock.uptimeMillis();
        // customHandler.postDelayed(updateTimerThread, 0);
        //  }


    }

    private void setSinglePlayerNameAndImage(CircleImageView imageView, TextView tvPlayerName1, TextView tvPlayerName2) {

    }

    private void enableThreeSetScoreBoard() {
        if (removeOidList.length() == 0) {
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
            hideAllContainerView();
        }
        ll_score_container.setVisibility(View.VISIBLE);
        rl_three_single_player_far_container.setVisibility(View.GONE);
        rl_three_single_player_near_container.setVisibility(View.GONE);
        rl_three_double_player_far_container.setVisibility(View.GONE);
        rl_three_double_player_near_container.setVisibility(View.GONE);
        Log.e("game", gameSet);

        score_ci_player1.setVisibility(View.GONE);
        score_ci_player2.setVisibility(View.GONE);
        score_ci_player3.setVisibility(View.GONE);
        score_ci_player4.setVisibility(View.GONE);
        score_tv_playerName1.setVisibility(View.GONE);
        score_tv_playerName2.setVisibility(View.GONE);
        score_tv_playerName3.setVisibility(View.GONE);
        score_tv_playerName4.setVisibility(View.GONE);
        tv_single_score_vs_player.setVisibility(View.GONE);
        tv_double_score_vs_player.setVisibility(View.GONE);
        enableTvScoreDisplay();

        score_tv_playerName1.setText("");
        score_tv_playerName2.setText("");
        score_tv_playerName3.setText("");
        score_tv_playerName4.setText("");

        if (playerArray != null) {
            Log.e("123", "" + playerArray);
            for (int i = 0; i < playerArray.length(); i++) {
                Log.e("AAA", playerArray.optJSONObject(i).optString("playerPosition"));
                switch (playerArray.optJSONObject(i).optString("playerPosition")) {
                    case "far":
                        if (matchType.equalsIgnoreCase("singles")) {
                            rl_three_single_player_near_container.setVisibility(View.VISIBLE);
                            tv_single_score_vs_player.setVisibility(View.VISIBLE);
                            updateProfilePicture(playerArray.optJSONObject(i).optString("profileImageUrl"), ci_three_single_near_player);
                            tv_three_single_near_player_name.setText(playerArray.optJSONObject(i).optString("playerName"));
                        } else {
                            tv_double_score_vs_player.setVisibility(View.VISIBLE);
                            rl_three_double_player_near_container.setVisibility(View.VISIBLE);
                            if (score_tv_playerName1.getText().toString().trim().isEmpty()) {
                                score_ci_player1.setVisibility(View.VISIBLE);
                                score_tv_playerName1.setVisibility(View.VISIBLE);
                                updateProfilePicture(playerArray.optJSONObject(i).optString("profileImageUrl"), score_ci_player1);
                                score_tv_playerName1.setText(playerArray.optJSONObject(i).optString("playerName"));
                            } else {
                                score_ci_player2.setVisibility(View.VISIBLE);
                                score_tv_playerName2.setVisibility(View.VISIBLE);
                                updateProfilePicture(playerArray.optJSONObject(i).optString("profileImageUrl"), score_ci_player2);
                                score_tv_playerName2.setText(playerArray.optJSONObject(i).optString("playerName"));
                            }
                        }
                        break;
                    case "near":
                        if (matchType.equalsIgnoreCase("singles")) {
                            tv_single_score_vs_player.setVisibility(View.VISIBLE);
                            rl_three_single_player_far_container.setVisibility(View.VISIBLE);
                            updateProfilePicture(playerArray.optJSONObject(i).optString("profileImageUrl"), ci_three_single_far_player);
                            tv_three_single_far_player_name.setText(playerArray.optJSONObject(i).optString("playerName"));
                        } else {
                            rl_three_double_player_far_container.setVisibility(View.VISIBLE);
                            tv_double_score_vs_player.setVisibility(View.VISIBLE);
                            if (score_tv_playerName3.getText().toString().trim().isEmpty()) {
                                score_ci_player3.setVisibility(View.VISIBLE);
                                score_tv_playerName3.setVisibility(View.VISIBLE);
                                updateProfilePicture(playerArray.optJSONObject(i).optString("profileImageUrl"), score_ci_player3);
                                score_tv_playerName3.setText(playerArray.optJSONObject(i).optString("playerName"));
                            } else {
                                score_ci_player4.setVisibility(View.VISIBLE);
                                score_tv_playerName4.setVisibility(View.VISIBLE);
                                updateProfilePicture(playerArray.optJSONObject(i).optString("profileImageUrl"), score_ci_player4);
                                score_tv_playerName4.setText(playerArray.optJSONObject(i).optString("playerName"));
                            }
                        }
                        break;
                }
            }
        }
    }

    private void hideAllContainerView() {
        ll_single_player_container.setVisibility(View.GONE);
        ll_score_container.setVisibility(View.GONE);
        llContentDouble_player.setVisibility(View.GONE);
        llContentSingle_player.setVisibility(View.GONE);
        llContainerTraining.setVisibility(View.GONE);
        llEditImageContainer.setVisibility(View.GONE);
        rlWinStatusContainer.setVisibility(View.GONE);
        ll_identification_player_container.setVisibility(View.GONE);
        llSingleSetPlayerContainer.setVisibility(View.GONE);
        llTrainingParentContainer.setVisibility(View.GONE);
    }

    private void updateProfilePicture(String uri, CircleImageView imageView) {
        imageView.setImageBitmap(null);
        Log.e("uri", uri);
        String removeSlashUri = uri.replaceAll("\\\\/", "/");
        Log.e("removeSlashUri", removeSlashUri);
        /*Glide.with(this)
                .load(removeSlashUri)
                .transform(new BlurTransformation(this))
                .error(R.drawable.default_background).into(ci_training_player_bg_image1);*/
        Glide.with(this)
                .load(removeSlashUri)
                .error(R.drawable.default_background).into(imageView);
    }


    private void leftAnimation(View view) {
        Animation RightSwipe = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        view.startAnimation(RightSwipe);
    }

    private void rightAnimation(View view) {
        Animation RightSwipe = AnimationUtils.loadAnimation(this, R.anim.translate_right);
        view.startAnimation(RightSwipe);
    }

    private void setTrainingData() {
        // hideTrainingChildView();
        if (playerArray != null) {
            for (int j = 0; j < playerArray.length(); j++) {
                switch (j) {
                    case 0:
                        llTrainingParentContainer1.setVisibility(View.VISIBLE);
                        llTrainingPlayerContainer1.setVisibility(View.VISIBLE);
                        tvTrainingPlayerName1.setText(playerArray.optJSONObject(j).optString("playerName"));
                        updateProfilePicture(playerArray.optJSONObject(j).optString("profileImageUrl"), ciTrainingPlayerImage1);
                        break;
                    case 1:
                        llTrainingParentContainer1.setVisibility(View.VISIBLE);
                        llTrainingPlayerContainer2.setVisibility(View.VISIBLE);
                        tvTrainingPlayerName2.setText(playerArray.optJSONObject(j).optString("playerName"));
                        updateProfilePicture(playerArray.optJSONObject(j).optString("profileImageUrl"), ciTrainingPlayerImage2);
                        break;

                    case 2:
                        llTrainingParentContainer1.setVisibility(View.VISIBLE);
                        llTrainingPlayerContainer3.setVisibility(View.VISIBLE);
                        tvTrainingPlayerName3.setText(playerArray.optJSONObject(j).optString("playerName"));
                        updateProfilePicture(playerArray.optJSONObject(j).optString("profileImageUrl"), ciTrainingPlayerImage3);
                        break;
                    case 3:
                        //if (playerArray.length() <= 4) {
                        llTrainingParentContainer1.setVisibility(View.VISIBLE);
                        llTrainingPlayerContainer4.setVisibility(View.VISIBLE);
                        tvTrainingPlayerName4.setText(playerArray.optJSONObject(j).optString("playerName"));
                        updateProfilePicture(playerArray.optJSONObject(j).optString("profileImageUrl"), ciTrainingPlayerImage4);
                       /* } else {
                            llTrainingParentContainer2.setVisibility(View.VISIBLE);
                            llTrainingPlayerContainer5.setVisibility(View.VISIBLE);
                            updateProfilePicture(playerArray.optJSONObject(j).optString("profileImageUrl"), ciTrainingPlayerImage5);
                        }*/
                        break;
                    case 4:
                        // if (playerArray.length() > 4) {
                        llTrainingParentContainer2.setVisibility(View.VISIBLE);
                        llTrainingPlayerContainer5.setVisibility(View.VISIBLE);
                        tvTrainingPlayerName5.setText(playerArray.optJSONObject(j).optString("playerName"));
                        updateProfilePicture(playerArray.optJSONObject(j).optString("profileImageUrl"), ciTrainingPlayerImage5);
                        //  }
                        break;
                    case 5:
                        llTrainingParentContainer2.setVisibility(View.VISIBLE);
                        llTrainingPlayerContainer6.setVisibility(View.VISIBLE);
                        tvTrainingPlayerName6.setText(playerArray.optJSONObject(j).optString("playerName"));
                        updateProfilePicture(playerArray.optJSONObject(j).optString("profileImageUrl"), ciTrainingPlayerImage6);
                        break;
                    case 6:
                        llTrainingParentContainer2.setVisibility(View.VISIBLE);
                        llTrainingPlayerContainer7.setVisibility(View.VISIBLE);
                        tvTrainingPlayerName7.setText(playerArray.optJSONObject(j).optString("playerName"));
                        updateProfilePicture(playerArray.optJSONObject(j).optString("profileImageUrl"), ciTrainingPlayerImage7);
                        break;
                    case 7:
                        llTrainingParentContainer2.setVisibility(View.VISIBLE);
                        llTrainingPlayerContainer8.setVisibility(View.VISIBLE);
                        tvTrainingPlayerName8.setText(playerArray.optJSONObject(j).optString("playerName"));
                        updateProfilePicture(playerArray.optJSONObject(j).optString("profileImageUrl"), ciTrainingPlayerImage8);
                        break;

                }

            }
        }
    }

    private void removeDuplicateArray() throws JSONException {
        Set<String> playerId = new HashSet<String>();
        JSONArray tempArray = new JSONArray();
        for (int i = 0; i < playerArray.length(); i++) {
            String stationCode = playerArray.optJSONObject(i).optString("playerOid");
            if (playerId.contains(stationCode)) {
                continue;
            } else {
                playerId.add(stationCode);
                tempArray.put(playerArray.optJSONObject(i));
            }

        }
        playerArray = tempArray; //assign temp to original
        Log.e("remove array", "" + playerArray);

        if (loginType.equalsIgnoreCase("coach")) {
            /*llContainerTraining.setVisibility(View.VISIBLE);
            trainingPlayerAdapter = new TrainingPlayerAdapter(this, playerArray);
            verticalGridView.setAdapter(trainingPlayerAdapter);*/
            llTrainingParentContainer.setVisibility(View.VISIBLE);
            hideTrainingChildView();
            setTrainingData();
        } else if (playerArray != null && !setMatchId.equalsIgnoreCase("")) {
            if (matchRound.equalsIgnoreCase("1")) {
                enableSingleSetPlayerScoreBoard();
            } else {
                enableThreeSetScoreBoard();
            }
        } else {
            if (playerArray.length() == 1) {
                tvPlayerName.setText(playerArray.optJSONObject(playerArray.length() - 1).optString("playerName"));
                tvPlayerAI.setText(playerArray.optJSONObject(playerArray.length() - 1).optString("performanceThumbNail"));
                updateProfilePicture(playerArray.optJSONObject(playerArray.length() - 1).optString("profileImageUrl"), profile_image);
                ll_single_player_container.setVisibility(View.VISIBLE);
                leftAnimation(ll_single_player_container);
            } else if (playerArray.length() == 2) {
                ll_single_player_container.animate()
                        .alpha(0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                hideAllContainerView();
                                ll_identification_player_container.setVisibility(View.VISIBLE);
                                leftAnimation(ll_identification_player_container);
                                tvPlayerIdentificationName.setText(playerArray.optJSONObject(playerArray.length() - 1).optString("playerName"));
                                tvPlayerIdentificationAIName.setText(playerArray.optJSONObject(playerArray.length() - 1).optString("performanceThumbNail"));
                                updateProfilePicture(playerArray.optJSONObject(playerArray.length() - 1).optString("profileImageUrl"), profile_double_player_image);
                                animationDetectTwoPlayers();
                            }
                        });
            } else if (playerArray.length() == 3) {
                /*ll_identification_player_container.animate()
                        .alpha(0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                hideAllContainerView();
                                llContentDouble_player.setVisibility(View.VISIBLE);
                                setValueForDecisionView();
                            }
                        });*/
                setValueForDecisionView();
            } else if (playerArray.length() == 4) {
                setValueForDecisionView();
            }
            /*material_icon.setVisibility(View.GONE);
            material_icon.setInitials(true);
            material_icon.setInitialsNumber(4);
            material_icon.setLetter(playerArray.optJSONObject(0).optString("playerName"));
            material_icon.setLetterSize(60);
            material_icon.setLetterColor(getResources().getColor(R.color.search_opaque));
            material_icon.setShapeColor(getResources().getColor(R.color.background_gradient_end));*/
        }

    }

    private void animationDetectTwoPlayers() {
        ll_identification_player_container.animate()
                .alpha(0f)
                .setDuration(3000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        hideAllContainerView();
                        llContentDouble_player.setVisibility(View.VISIBLE);
                        setValueForDecisionView();
                    }
                });
    }

    private void animationMatchConvertedPlayers() {
        llTrainingParentContainer.animate()
                .alpha(0f)
                .setDuration(3000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        hideAllContainerView();
                        llContentDouble_player.setVisibility(View.VISIBLE);
                        setValueForDecisionView();
                    }
                });
    }

    private void setValueForDecisionView() {
        if (playerArray != null) {
            for (int k = 0; k < playerArray.length(); k++) {
                switch (k) {
                    case 0:
                        llContentDoubleNearContainer1.setVisibility(View.VISIBLE);
                        tvNearDoublePlayerName1.setText(playerArray.optJSONObject(k).optString("playerName"));
                        tvNearDoublePlayerPerformance1.setText(playerArray.optJSONObject(k).optString("performanceThumbNail"));
                        updateProfilePicture(playerArray.optJSONObject(k).optString("profileImageUrl"), ciNearDoublePlayer1);
                        break;
                    case 1:
                        llContentDoubleNearContainer2.setVisibility(View.VISIBLE);
                        tvNearDoublePlayerName2.setText(playerArray.optJSONObject(k).optString("playerName"));
                        tvNearDoublePlayerPerformance2.setText(playerArray.optJSONObject(k).optString("performanceThumbNail"));
                        updateProfilePicture(playerArray.optJSONObject(k).optString("profileImageUrl"), ciNearDoublePlayer2);
                        break;
                    case 2:
                        llContentDoubleFarContainer1.setVisibility(View.VISIBLE);
                        tvFarDoublePlayerName1.setText(playerArray.optJSONObject(k).optString("playerName"));
                        tvFarDoublePlayerPerformance1.setText(playerArray.optJSONObject(k).optString("performanceThumbNail"));
                        updateProfilePicture(playerArray.optJSONObject(k).optString("profileImageUrl"), ciFarDoublePlayer1);
                        break;
                    case 3:
                        tvFarDoublePlayerName2.setText(playerArray.optJSONObject(k).optString("playerName"));
                        tvFarDoublePlayerPerformance2.setText(playerArray.optJSONObject(k).optString("performanceThumbNail"));
                        llContentDoubleFarContainer2.setVisibility(View.VISIBLE);
                        updateProfilePicture(playerArray.optJSONObject(k).optString("profileImageUrl"), ciFarDoublePlayer2);
                        break;
                }
            }
        }
    }

    private void populatePlayerName() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttHelper != null) {
            mqttHelper.unSubscribeMqttChannel();
            mqttHelper.disconnectMqtt();
            isThreadRun = false;
        }
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            if (isThreadRun) {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

                updatedTime = timeSwapBuff + timeInMilliseconds;
                Log.e("gameSet", gameSet);
                int secs = (int) (updatedTime / 1000);
                int mins = secs / 60;
                secs = secs % 60;
                // int milliseconds = (int) (updatedTime % 1000);
                if (matchRound.equals("1")) {
                    tv_single_set_timer.setText("" + mins + ":"
                            + String.format("%02d", secs));
                } else {
                    if (gameSet.equalsIgnoreCase("1")) {
                        getTvTimer1.setText("" + mins + ":"
                                + String.format("%02d", secs));
                    } else if (gameSet.equalsIgnoreCase("2")) {
                        getTvTimer2.setText("" + mins + ":"
                                + String.format("%02d", secs));
                    } else if (gameSet.equalsIgnoreCase("3")) {
                        getTvTimer3.setText("" + mins + ":"
                                + String.format("%02d", secs));
                    }
                }
                customHandler.postDelayed(this, 0);
            }
        }

    };

    private void resetTimer() {
        startTime = 0L;
        customHandler = new Handler();
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
    }

}
