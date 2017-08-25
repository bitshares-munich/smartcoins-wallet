package de.bitsharesmunich.graphenej.models.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.bitsharesmunich.graphenej.RPC;
import de.bitsharesmunich.graphenej.interfaces.WitnessResponseListener;
import de.bitsharesmunich.graphenej.models.ApiCall;
import de.bitsharesmunich.graphenej.models.BaseResponse;
import de.bitsharesmunich.graphenej.models.Market;
import de.bitsharesmunich.graphenej.models.WitnessResponse;

/**
 * Created by hvarona on 12/12/16.
 */
public class GetLimitOrders extends WebSocketAdapter {

    private String a;
    private String b;
    private int limit;
    private WitnessResponseListener mListener;

    public GetLimitOrders(String a, String b, int limit, WitnessResponseListener mListener) {
        this.a = a;
        this.b = b;
        this.limit = limit;
        this.mListener = mListener;
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        ArrayList<Serializable> accountParams = new ArrayList<>();
        accountParams.add(this.a);
        accountParams.add(this.b);
        accountParams.add(this.limit);
        ApiCall getAccountByName = new ApiCall(0, RPC.CALL_GET_LIMIT_ORDERS, accountParams, "2.0", 1);
        websocket.sendText(getAccountByName.toJsonString());
    }

    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        try {
            String response = frame.getPayloadText();
            Gson gson = new Gson();

            Type GetLimitiOrdersResponse = new TypeToken<WitnessResponse<ArrayList<Market>>>() {
            }.getType();
            WitnessResponse<ArrayList<Market>> witnessResponse = gson.fromJson(response, GetLimitiOrdersResponse);
            if (witnessResponse.error != null) {
                this.mListener.onError(witnessResponse.error);
            } else {
                this.mListener.onSuccess(witnessResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        websocket.disconnect();
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        mListener.onError(new BaseResponse.Error(cause.getMessage()));
        websocket.disconnect();
    }

    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
        mListener.onError(new BaseResponse.Error(cause.getMessage()));
        websocket.disconnect();
    }
}